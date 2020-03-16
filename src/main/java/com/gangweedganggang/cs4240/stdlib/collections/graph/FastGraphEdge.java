package com.gangweedganggang.cs4240.stdlib.collections.graph;

public interface FastGraphEdge<N extends FastGraphVertex> {
	N src();

	N dst();
}
