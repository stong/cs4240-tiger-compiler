package com.gangweedganggang.cs4240.stdlib.collections.map;

import java.util.ArrayList;
import java.util.List;

public class ListCreator<T> implements ValueCreator<List<T>> {

	@Override 
	public List<T> create() {
		return new ArrayList<>();
	}
}