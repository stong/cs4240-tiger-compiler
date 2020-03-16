package com.gangweedganggang.cs4240.flowgraph;

import com.gangweedganggang.cs4240.flowgraph.edges.FlowEdge;
import com.gangweedganggang.cs4240.flowgraph.edges.ImmediateEdge;
import com.gangweedganggang.cs4240.stdlib.collections.graph.FastGraphEdge;
import com.gangweedganggang.cs4240.stdlib.collections.graph.algorithms.ExtendedDfs;
import com.gangweedganggang.cs4240.stdlib.util.TabbedStringWriter;

import java.util.*;
import java.util.function.Function;

public class ControlFlowGraph<T extends BasicBlock> extends FlowGraph<T, FlowEdge<T>> {
	private final Function<ControlFlowGraph<T>, T> blockFactory;

	public ControlFlowGraph(Function<ControlFlowGraph<T>, T> blockFactory) {
		this.blockFactory = blockFactory;
	}

	public T newBlock() {
		T b = blockFactory.apply(this);
		addVertex(b);
		return b;
	}

	@Override
	public String toString() {
		TabbedStringWriter sw = new TabbedStringWriter();

		int insn = 0;

		for(T b : verticesInOrder()) {
			CFGUtils.blockToString(sw, this, b, insn);
		}
		return sw.toString();
	}

	@Override
	protected List<T> computeTopoorder() {
		return new ExtendedDfs<T>(this, ExtendedDfs.TOPO | ExtendedDfs.POST) {
			@Override
			protected Iterable<? extends FastGraphEdge<T>> order(Set<? extends FastGraphEdge<T>> fastGraphEdges) {
				List<? extends FastGraphEdge<T>> edges = new ArrayList<>(fastGraphEdges);
				edges.sort((a, b) -> Boolean.compare(a instanceof ImmediateEdge, b instanceof ImmediateEdge));
				// horrible hack, dont visit edges to blocks that have an incoming immediate edge
				for (Iterator<? extends FastGraphEdge<T>> it = edges.iterator(); it.hasNext();) {
					FastGraphEdge<T> e = it.next();
					if (!(e instanceof ImmediateEdge)) {
						if (getReverseEdges(e.dst()).stream().anyMatch(ImmediateEdge.class::isInstance)) {
							it.remove();
						}
					}
				}
				// yes, this order is reversed: other edges will come before ImmediateEdges.
				// that's intended since topoorder is the REVERSE of postorder. ImmediateEdges
				// must be visited LAST.
				return edges;
			}
		}.run(getEntries().iterator().next()).getTopoOrder();
	}

	// CFG may only have one entry
    public T getEntry() {
		if (getEntries().size() != 1)
			throw new IllegalStateException("not exactly one entry in CFG");
        return getEntries().iterator().next();
    }

    // used for assigning unique id's to basicblocks. ugly hack
	// fyi, we start at one arbitrarily.
	private int blockCounter = 1;

    public int makeBlockId() {
		return blockCounter++;
	}

	// sanity check
	public void verify() {
	}
}
