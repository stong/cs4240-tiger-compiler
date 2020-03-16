package com.gangweedganggang.cs4240.flowgraph.edges;

import com.gangweedganggang.cs4240.stdlib.collections.graph.FastGraphVertex;

public abstract class JumpEdge<N extends FastGraphVertex> extends AbstractFlowEdge<N> {
	public JumpEdge(FlowEdgeType type, N src, N dst) {
		super(type, src, dst);
	}

	@Override
	public String toString() {
		return String.format("#%s -> #%s", src.getDisplayName(), dst.getDisplayName());
	}

	@Override
	public String toInverseString() {
		return String.format("#%s <- #%s", dst.getDisplayName(), src.getDisplayName());
	}
}
