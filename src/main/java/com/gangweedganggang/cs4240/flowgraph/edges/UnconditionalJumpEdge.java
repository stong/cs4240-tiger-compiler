package com.gangweedganggang.cs4240.flowgraph.edges;

import com.gangweedganggang.cs4240.stdlib.collections.graph.FastGraphVertex;

import static com.gangweedganggang.cs4240.flowgraph.edges.FlowEdgeType.UNCOND;

public class UnconditionalJumpEdge<N extends FastGraphVertex> extends JumpEdge<N> {
	
	public UnconditionalJumpEdge(N src, N dst) {
		super(UNCOND, src, dst);
	}

	@Override
	public String toGraphString() {
		return "Goto";
	}

	@Override
	public String toString() {
		return "Goto " + super.toString();
	}
	
	@Override
	public String toInverseString() {
		return "Goto " + super.toInverseString();
	}
	
	@Override
	public <M extends FastGraphVertex> UnconditionalJumpEdge<M> clone(M src, M dst) {
		return new UnconditionalJumpEdge<>(src, dst);
	}
}