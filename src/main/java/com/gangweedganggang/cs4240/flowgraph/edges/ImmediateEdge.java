package com.gangweedganggang.cs4240.flowgraph.edges;

import com.gangweedganggang.cs4240.stdlib.collections.graph.FastGraphVertex;

import static com.gangweedganggang.cs4240.flowgraph.edges.FlowEdgeType.IMMEDIATE;

// Represents implicit 'fall-through' control flow.
public class ImmediateEdge<N extends FastGraphVertex> extends AbstractFlowEdge<N> {
	
	public ImmediateEdge(N src, N dst) {
		super(IMMEDIATE, src, dst);
	}

	@Override
	public String toGraphString() {
		return "Immediate";
	}

	@Override
	public String toString() {
		return String.format("Immediate #%s -> #%s", src.getDisplayName(), dst.getDisplayName());
	}

	@Override
	public String toInverseString() {
		return String.format("Immediate #%s <- #%s", dst.getDisplayName(), src.getDisplayName());
	}
	
	@Override
	public <M extends FastGraphVertex> ImmediateEdge<M> clone(M src, M dst) {
		return new ImmediateEdge<>(src, dst);
	}
}
