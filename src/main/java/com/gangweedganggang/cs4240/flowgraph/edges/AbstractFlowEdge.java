package com.gangweedganggang.cs4240.flowgraph.edges;

import com.gangweedganggang.cs4240.stdlib.collections.graph.FastGraphEdgeImpl;
import com.gangweedganggang.cs4240.stdlib.collections.graph.FastGraphVertex;

import java.util.Objects;

public abstract class AbstractFlowEdge<N extends FastGraphVertex> extends FastGraphEdgeImpl<N> implements FlowEdge<N> {
	protected final FlowEdgeType type;

	public AbstractFlowEdge(FlowEdgeType type, N src, N dst) {
		super(src, dst);
		this.type = type;
	}

	public int getType() {
		return type.ordinal();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		AbstractFlowEdge<?> that = (AbstractFlowEdge<?>) o;
		return getType() == that.getType();
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), getType());
	}
}
