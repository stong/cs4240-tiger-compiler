package com.gangweedganggang.cs4240.stdlib.collections.map;

public class NullCreator<V> implements ValueCreator<V> {

	@Override
	public V create() {
		return null;
	}
}