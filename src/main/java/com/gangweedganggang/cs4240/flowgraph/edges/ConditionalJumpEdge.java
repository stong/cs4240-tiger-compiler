package com.gangweedganggang.cs4240.flowgraph.edges;

import com.gangweedganggang.cs4240.stdlib.collections.graph.FastGraphVertex;

import static com.gangweedganggang.cs4240.flowgraph.edges.FlowEdgeType.COND;

public class ConditionalJumpEdge<N extends FastGraphVertex> extends JumpEdge<N> {
	public ConditionalJumpEdge(N src, N dst) {
		super(COND, src, dst);
	}

	@Override
	public String toGraphString() {
		return "BrTrue";
	}

	@Override
	public String toString() {
		return "BrTrue " + super.toString();
	}
	
	@Override
	public String toInverseString() {
		return "BrTrue " + super.toInverseString();
	}

	@Override
	public <M extends FastGraphVertex> ConditionalJumpEdge<M> clone(M src, M dst) {
		return new ConditionalJumpEdge<>(src, dst);
	}
}