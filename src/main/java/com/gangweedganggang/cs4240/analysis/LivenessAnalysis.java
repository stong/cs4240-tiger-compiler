package com.gangweedganggang.cs4240.analysis;

import com.gangweedganggang.cs4240.flowgraph.BasicBlock;
import com.gangweedganggang.cs4240.flowgraph.ControlFlowGraph;
import com.gangweedganggang.cs4240.flowgraph.Local;
import com.gangweedganggang.cs4240.flowgraph.Stmt;
import com.gangweedganggang.cs4240.flowgraph.edges.FlowEdge;
import com.gangweedganggang.cs4240.stdlib.collections.bitset.GenericBitSet;
import com.gangweedganggang.cs4240.stdlib.collections.graph.FastGraphEdge;
import com.gangweedganggang.cs4240.stdlib.collections.map.ValueCreator;

import java.util.*;

// stupid, iterative, fixed-point, worklist based algorithm
public class LivenessAnalysis<L extends Local, S extends Stmt<L>, BB extends BasicBlock<S>> {
    private final Map<BB, GenericBitSet<L>> use;
	private final Map<BB, GenericBitSet<L>> def;

    private final Map<BB, GenericBitSet<L>> out;
	private final Map<BB, GenericBitSet<L>> in;

	private Map<BB, List<GenericBitSet<L>>> stmtLiveness; // intra-block live ranges. at index i, holds live vars AFTER the statement has executed

	private final Queue<BB> queue;

	private final ControlFlowGraph<BB> cfg;
	private final ValueCreator<GenericBitSet<L>> setFactory;

	public LivenessAnalysis(ControlFlowGraph<BB> cfg, ValueCreator<GenericBitSet<L>> setFactory) {
	    this.setFactory = setFactory;

		use = new HashMap<>();
		def = new HashMap<>();

		out = new HashMap<>();
		in = new HashMap<>();

		queue = new ArrayDeque<>();

		this.cfg = cfg;

		init();
	}

	private void enqueue(BB b) {
		if (!queue.contains(b)) {
			// System.out.println("Enqueue " + b);
			queue.add(b);
		}
	}

	private void init() {
        // initialize in and out
        for (BB b : cfg.vertices()) {
        	in.put(b, setFactory.create());
        	out.put(b, setFactory.create());
        }

        // compute def, use, and phi for each block
        for (BB b : cfg.vertices())
            precomputeBlock(b);

        // enqueue every block
        for (BB b : cfg.vertices())
            enqueue(b);
    }

    // compute def, use for given block
	private void precomputeBlock(BB bb) {
		def.put(bb, setFactory.create());
		use.put(bb, setFactory.create());

		// we have to iterate in reverse order because a definition will kill a use in the current block
		// this is so that uses do not escape a block if its def is in the same block. this is basically
		// simulating a statement graph analysis
		ListIterator<S> it = bb.listIterator(bb.size());
		while (it.hasPrevious()) {
			S stmt = it.previous();
			for (L defVar : stmt.defs()) {
				def.get(bb).add(defVar);
				use.get(bb).remove(defVar);
            }
			for (L useVar : stmt.uses()) {
				use.get(bb).add(useVar);
			}
		}
	}

	public void compute() {
		// +use and -def affect out
		// -use and +def affect in
		// negative handling always goes after positive and any adds
		while (!queue.isEmpty()) {
			BB b = queue.remove();
			// System.out.println("\n\nProcessing " + b.getId());

			GenericBitSet<L> oldIn = new GenericBitSet<>(in.get(b));
			GenericBitSet<L> curIn = new GenericBitSet<>(use.get(b));
			GenericBitSet<L> curOut = setFactory.create();

			// out[n] = U(s in succ[n])(in[s])
			for (FlowEdge<BB> succEdge : cfg.getEdges(b))
				curOut.addAll(in.get(succEdge.dst()));

			// in[n] = use[n] U(out[n] - def[n])
			curIn.addAll(curOut.relativeComplement(def.get(b)));

			in.put(b, curIn);
			out.put(b, curOut);

			// queue preds if dataflow state changed
			if (!oldIn.equals(curIn)) {
				cfg.getReverseEdges(b).stream().map(FastGraphEdge::src).forEach(this::enqueue);

				// for (BB b2 : cfg.vertices())
				// System.out.println(b2.getId() + " |||| IN: " + in.get(b2) + " ||||| OUT: " + out.get(b2));
			}
		}
		finalizeLiveness();
	}

	private void finalizeLiveness() {
		stmtLiveness = new HashMap<>();
		for (BB bb : cfg.vertices()) {
			List<GenericBitSet<L>> liveness = new ArrayList<>(bb.size());
			stmtLiveness.put(bb, liveness);
			for (int i = 0; i < bb.size(); i++) {
				liveness.add(null);
			}
			ListIterator<S> it = bb.listIterator(bb.size());
			GenericBitSet<L> live = out.get(bb).copy();
			while (it.hasPrevious()) {
				liveness.set(it.previousIndex(), live.copy());
				S stmt = it.previous();
				for (L defVar : stmt.defs()) {
					live.remove(defVar);
				}
				for (L useVar : stmt.uses()) {
					live.add(useVar);
				}
			}
			assert live.equals(in.get(bb));
		}
	}

	public GenericBitSet<L> in(BB b) {
		return in.get(b);
	}

	public GenericBitSet<L> out(BB b) {
		return out.get(b);
	}

	// Locals that are LIVE OUT OF this statement, I.E. LIVE AFTER THIS STATEMENT EXECUTES
	public GenericBitSet<L> liveAt(BB b, int i) {
		return stmtLiveness.get(b).get(i);
	}
}
