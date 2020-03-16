package com.gangweedganggang.cs4240.backend;

import com.gangweedganggang.cs4240.flowgraph.BasicBlock;
import com.gangweedganggang.cs4240.flowgraph.ControlFlowGraph;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

// braindamaged regalloc that stores all temporaries into locals(?! lol)
public class NaiveRegisterAllocator<Reg extends TargetReg, Insn extends TargetInsn<Reg>,
        BB extends BasicBlock<Insn>, CFG extends ControlFlowGraph<BB>,
        ISA extends TargetISA<Reg, Insn, BB, CFG, Func, ISA>,
        Func extends TargetFunction<Reg, Insn, BB, CFG, Func, ISA>,
        Context extends BackendContext<Reg, Insn, BB, CFG, ISA, Func>> {

    protected final Func func;
    protected final Context ctx;

    protected final Map<Reg, Integer> spills;

    NaiveRegisterAllocator(Context ctx, Func func) {
        this.ctx = ctx;
        this.func = func;
        spills = new HashMap<>();
    }

    public void run() {
        TargetRegPool<Reg> regInfo = func.getRegPool();
        for (BB bb : func.getCfg().vertices()) {
            for (ListIterator<Insn> it = bb.listIterator(); it.hasNext(); ) {
                Insn insn = it.next();
                int regCount = 0;
                for (int useIdx : insn.useOperands()) {
                    Reg use = insn.getOperand(useIdx);
                    if (!use.isPhysicalRegister()) {
                        int stackOff = getStackLocation(use);
                        it.previous();
                        Reg reg = regInfo.getReg(regInfo.getRegType(use), regCount++);
                        for (Insn loadInsn : ctx.isa.load(reg, ctx.isa.getFp(), stackOff))
                            it.add(loadInsn);
                        it.next();
                        System.out.println("Load " + use + " to fp+" + stackOff + " from " + reg);
                        insn.setOperand(useIdx, reg);
                    }
                }
                for (int defIdx : insn.defOperands()) {
                    Reg def = insn.getOperand(defIdx);
                    if (!def.isPhysicalRegister()) {
                        int stackOff = getStackLocation(def);
                        Reg reg = regInfo.getReg(regInfo.getRegType(def), regCount++);
                        for (Insn storeInsn : ctx.isa.store(reg, ctx.isa.getFp(), stackOff))
                            it.add(storeInsn);
                        System.out.println("Store " + def + " from fp+" + stackOff + " to " + reg);
                        insn.setOperand(defIdx, reg);
                    }
                }
            }
        }
    }

    private Integer getStackLocation(Reg r) {
        if (spills.containsKey(r)) {
            return spills.get(r);
        }
        int off = func.allocLocal(ctx.isa.getWordSize());
        spills.put(r, off);
        return off;
    }
}
