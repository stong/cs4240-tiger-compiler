package com.gangweedganggang.cs4240.backend;

import com.gangweedganggang.cs4240.analysis.LivenessAnalysis;
import com.gangweedganggang.cs4240.flowgraph.BasicBlock;
import com.gangweedganggang.cs4240.flowgraph.ControlFlowGraph;
import com.gangweedganggang.cs4240.stdlib.collections.bitset.GenericBitSet;
import com.gangweedganggang.cs4240.stdlib.collections.graph.FastGraphEdgeImpl;
import com.gangweedganggang.cs4240.stdlib.collections.graph.FastUndirectedGraph;
import com.gangweedganggang.cs4240.stdlib.collections.itertools.IterableUtils;

import java.util.*;

// braindamaged regalloc that stores all temporaries into locals(?! lol)
public class BriggsRegisterAllocator<Reg extends TargetReg, Insn extends TargetInsn<Reg>,
        BB extends BasicBlock<Insn>, CFG extends ControlFlowGraph<BB>,
        ISA extends TargetISA<Reg, Insn, BB, CFG, Func, ISA>,
        Func extends TargetFunction<Reg, Insn, BB, CFG, Func, ISA>,
        Context extends BackendContext<Reg, Insn, BB, CFG, ISA, Func>> {

    protected final Func func;
    protected final Context ctx;

    protected final Map<Reg, Integer> spills;

    private LivenessAnalysis<Reg, Insn, BB> liveness;
    private Map<RegType, InterferenceGraph> interferences; // different reg classes have separate interference graphs.
    private TargetRegPool<Reg> regInfo;
    private Map<Reg, Integer> coloring;

    public BriggsRegisterAllocator(Context ctx, Func func) {
        this.ctx = ctx;
        this.func = func;
        spills = new HashMap<>();
    }

    private void compute() {
        regInfo = func.getRegPool();

        liveness = new LivenessAnalysis<>(func.getCfg(), func.getRegPool());
        liveness.compute();
        System.out.println("liveness");
        for (BB bb : func.getCfg().verticesInOrder()) {
            System.out.println(bb.getDisplayName());
            for (int i = 0 ; i < bb.size(); i++) {
                System.out.println("\t" + bb.get(i) + "\t" + liveness.liveAt(bb, i));
            }
        }
        System.out.println();

        interferences = new HashMap<>();
        interferences.put(RegType.INT, new InterferenceGraph());
        interferences.put(RegType.FLOAT, new InterferenceGraph());

        for (BB bb : func.getCfg().vertices()) {
            for (int i = 0 ; i < bb.size(); i++) {
                GenericBitSet<Reg> live = liveness.liveAt(bb, i);
                for (Reg r1 : live) {
                    if (r1.isPhysicalRegister()) continue; // dont consider vars we don't allocate
                    InterferenceGraph g = interferences.get(regInfo.getRegType(r1));
                    if (g == null) continue; // just consider only int and float for now, please.
                    g.addVertex(r1);
                    for (Reg r2 : live) {
                        if (r2.isPhysicalRegister()) continue; // dont consider vars we don't allocate
                        if (r1.equals(r2)) continue; // lol
                        if (regInfo.getRegType(r1).equals(regInfo.getRegType(r2))) { // only 2 regs of same class can interfere
                            g.addEdge(new FastGraphEdgeImpl<>(r1, r2));
                        }
                    }
                }
            }
        }
        System.out.println("interference:");
        for (InterferenceGraph g : interferences.values()) {
            System.out.println(g);
            System.out.println("---");
        }
        System.out.println();

        coloring = new HashMap<>();
        for (Map.Entry<RegType, InterferenceGraph> e : interferences.entrySet()) {
            coloring.putAll(e.getValue().briggsColor(regInfo.numRegs(e.getKey())));
        }
        System.out.println("coloring:");
        for (Map.Entry<Reg, Integer> e : coloring.entrySet()) {
            System.out.println(e.getKey() + " -> " + (e.getValue() < 0 ? "spilled" : e.getValue()));
        }
        System.out.println();
    }

    public void run() {
        compute();
        renumber();
    }

    private boolean isSpilled(Reg r) {
        return !r.isPhysicalRegister() && coloring.get(r) < 0;
    }

    // tell me which "real" register this "temp" register got allocated to
    private Reg getRegAllocation(Reg r) {
        if (isSpilled(r))
            throw new IllegalArgumentException("what are you doing man!");
        return regInfo.getReg(regInfo.getRegType(r), coloring.get(r));
    }

    private void renumber() {
        for (BB bb : func.getCfg().vertices()) {
            int i = 0; // WHY TRACK INDEX MANUALLY INSTEAD OF USING ITERATOR? Because we modify the statement list.
            for (ListIterator<Insn> it = bb.listIterator(); it.hasNext(); ) {
                Insn insn = it.next();

                List<Insn> prepend = new ArrayList<>();
                List<Insn> append = new ArrayList<>();

                int regCount = 0;
                for (int useIdx : insn.useOperands()) {
                    Reg use = insn.getOperand(useIdx);
                    if (use.isPhysicalRegister()) continue;
                    Reg allocation;
                    if (isSpilled(use)) {
                        int stackOff = getSpillLocation(use);
                        allocation = regInfo.getReg(regInfo.getRegType(use), regCount++);
                        prepend.addAll(0, ctx.isa.push(allocation));
                        prepend.addAll(ctx.isa.load(allocation, ctx.isa.getFp(), stackOff));
                        append.addAll(ctx.isa.pop(allocation));
                        System.out.println("Load " + use + " to fp+" + stackOff + " from " + allocation);
                    } else {
                        allocation = getRegAllocation(use);
                    }
                    insn.setOperand(useIdx, allocation);
                }
                for (int defIdx : insn.defOperands()) {
                    Reg def = insn.getOperand(defIdx);
                    if (def.isPhysicalRegister()) continue;
                    Reg allocation;
                    if (isSpilled(def)) {
                        int stackOff = getSpillLocation(def);
                        allocation = regInfo.getReg(regInfo.getRegType(def), regCount++);
                        prepend.addAll(0, ctx.isa.push(allocation));
                        append.addAll(0, ctx.isa.store(allocation, ctx.isa.getFp(), stackOff));
                        append.addAll(ctx.isa.pop(allocation));
                        System.out.println("Store " + def + " from fp+" + stackOff + " to " + allocation);
                    } else {
                        allocation = getRegAllocation(def);
                    }
                    insn.setOperand(defIdx, allocation);
                }

                // handle call convention reg saving and whatnot!
                if (ctx.isa.isCall(insn)) {
                    // need to save any live-OUT, BUT NOT DEFINED BY THIS, non-spilled, non-callee saved registers.
                    for (Reg live : liveness.liveAt(bb, i)) {
                        if (!live.isCalleeSaved() && !isSpilled(live)) {
                            if (!live.isPhysicalRegister())
                                live = getRegAllocation(live);
                            if (!IterableUtils.contains(insn.defs(), live)) {
                                System.out.println("need to save register " + live + " before call " + insn);
                                prepend.addAll(ctx.isa.push(live));
                                append.addAll(0, ctx.isa.pop(live));
                            }
                        }
                    }
                }

                it.previous();
                for (Insn toAdd : prepend)
                    it.add(toAdd);
                it.next();
                for (Insn toAdd : append)
                    it.add(toAdd);

                i++;
            }
        }
    }

    private Integer getSpillLocation(Reg r) {
        if (spills.containsKey(r)) {
            return spills.get(r);
        }
        int off = func.allocLocal(ctx.isa.getWordSize());
        spills.put(r, off);
        return off;
    }

    private class InterferenceGraph extends FastUndirectedGraph<Reg, FastGraphEdgeImpl<Reg>> {
        @Override
        public FastGraphEdgeImpl<Reg> clone(FastGraphEdgeImpl<Reg> edge, Reg newSrc, Reg newDst) {
            return new FastGraphEdgeImpl<>(newSrc, newDst);
        }

        // return map of Reg to color. a numbering of -1 suggests that the register must be spilled :c
        public Map<Reg, Integer> briggsColor(int k) {
            InterferenceGraph graphSpilled = clone(); // graph with spilled nodes removed
            Map<Reg, Integer> coloring = new HashMap<>();
            Deque<Reg> stack = new ArrayDeque<>();
            while (coloring.size() < this.vertices().size()) {
                InterferenceGraph graph = graphSpilled.clone(); // graph we work with this iteration
                while (graph.vertices().size() > 0) {
                    // step 1. remove all the nodes with degree < k and push them onto the stack.
                    for (Reg r : graph.vertices()) {
                        if (graph.getEdges(r).size() < k) {
                            stack.push(r);
                            graph.removeVertex(r);
                        }
                    }

                    // step 2. if the graph is non-empty, remove some arbitrary node and push onto the stack and return to step 1.
                    // otherwise, go to step 3.
                    if (graph.vertices().size() > 0) {
                        Reg toRemove = graph.vertices().iterator().next();
                        stack.push(toRemove);
                        graph.removeVertex(toRemove);
                    }
                }
                // step 3. pop nodes from the stack and try to color them by some available color.
                // if there are no free colors, spill it then go to step 1.
                tryColor: while (!stack.isEmpty()) {
                    Reg toColor = stack.pop();
                    boolean[] availColors = new boolean[k];
                    Arrays.fill(availColors, true);
                    for (FastGraphEdgeImpl<Reg> e : graphSpilled.getEdges(toColor))
                        if (coloring.containsKey(e.dst()))
                            availColors[coloring.get(e.dst())] = false;
                    for (int i = 0; i < k; i++) {
                        if (availColors[i]) {
                            coloring.put(toColor, i);
                            continue tryColor;
                        }
                    }
                    // spill and start over.
                    coloring.put(toColor, -1);
                    graphSpilled.removeVertex(toColor);
                }
            }
            return coloring;
        }

        @Override
        public InterferenceGraph clone() {
            InterferenceGraph copy = new InterferenceGraph();
            for (Reg r : vertices()) {
                copy.addVertex(r);
                for (FastGraphEdgeImpl<Reg> e : getEdges(r)) {
                    copy.addEdge(e);
                }
            }
            return copy;
        }
    }
}
