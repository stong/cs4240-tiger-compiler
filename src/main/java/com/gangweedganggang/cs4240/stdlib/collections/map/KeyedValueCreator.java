package com.gangweedganggang.cs4240.stdlib.collections.map;

public interface KeyedValueCreator<K, V> {
	V create(K k);
}
