package com.gangweedganggang.cs4240.stdlib.collections.bitset;

import java.util.HashMap;

import com.gangweedganggang.cs4240.stdlib.collections.map.NullPermeableHashMap;
import com.gangweedganggang.cs4240.stdlib.collections.map.ValueCreator;

public class IncrementalBitSetIndexer<N> implements BitSetIndexer<N> {

	private final NullPermeableHashMap<N, Integer> map;
	private final HashMap<Integer, N> reverseMap;
	
	public IncrementalBitSetIndexer() {
		map = new NullPermeableHashMap<>(new ValueCreator<Integer>() {
			@Override
			public Integer create() {
				return map.size() + 1;
			}
		});
		reverseMap = new HashMap<>();
	}
	
	@Override
	public int getIndex(N n) {
		int index = map.getNonNull(n);
		reverseMap.put(index, n);
		return index;
	}
	
	@Override
	public N get(int index) {
		return reverseMap.get(index);
	}
	
	@Override
	public boolean isIndexed(N o) {
		return map.containsKey(o);
	}
}
