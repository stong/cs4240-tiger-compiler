package com.gangweedganggang.cs4240.stdlib.collections.map;

import java.util.concurrent.atomic.AtomicInteger;

public class IntegerValueCreator implements ValueCreator<AtomicInteger> {

	@Override
	public AtomicInteger create() {
		return new AtomicInteger();
	}
}