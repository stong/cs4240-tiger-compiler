package com.gangweedganggang.cs4240.stdlib.collections.graph.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.gangweedganggang.cs4240.stdlib.collections.graph.FastDirectedGraph;
import com.gangweedganggang.cs4240.stdlib.collections.graph.FastGraphEdge;
import com.gangweedganggang.cs4240.stdlib.collections.graph.FastGraphVertex;

public class BreadthFirstSorterImpl<N extends FastGraphVertex> {
	private List<N> bfs;
	
	public BreadthFirstSorterImpl(FastDirectedGraph<N, FastGraphEdge<N>> graph, N entry) {
		LinkedList<N> queue = new LinkedList<>();
		queue.add(entry);
		
		Set<N> visited = new HashSet<>(graph.size());
		bfs = new ArrayList<>();
		while(!queue.isEmpty()) {
			entry = queue.pop();
			
			if(visited.contains(entry)) {
				continue;
			}
			visited.add(entry);
			bfs.add(entry);
			
			for(FastGraphEdge<N> e : graph.getEdges(entry)) {
				N s = e.dst();
				queue.addLast(s);
			}
		}
	}
	
	public List<N> getOrder() {
		return bfs;
	}
}
