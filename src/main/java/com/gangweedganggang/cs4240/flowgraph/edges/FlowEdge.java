package com.gangweedganggang.cs4240.flowgraph.edges;

import com.gangweedganggang.cs4240.stdlib.collections.graph.FastGraphEdge;
import com.gangweedganggang.cs4240.stdlib.collections.graph.FastGraphVertex;

public interface FlowEdge<N extends FastGraphVertex> extends FastGraphEdge<N> {
	
	int getType();
	
	String toGraphString();
	
	@Override
	String toString();
	
	String toInverseString();
	
	<M extends FastGraphVertex> FlowEdge<M> clone(M src, M dst);
}
