package com.gangweedganggang.cs4240.flowgraph;

import com.gangweedganggang.cs4240.frontend.SymbolType;

public abstract class Local implements Comparable<Local> {
	private final int index;

	private final SymbolType type;

	public Local(int index, SymbolType type) {
		this.index = index;
		this.type = type;
		if (index < 0)
			throw new IllegalArgumentException("Index underflow; hashCode collision possible " + index);
	}

	public int getIndex() {
		return index;
	}

	public int getCodeIndex() {
		return index;
	}

	@Override
	public String toString() {
		return "var" + index + "." + type;
	}

	@Override
	public int compareTo(Local o) {
		return Integer.compare(index, o.index);
	}

	@Override
	public int hashCode() {
		return index;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		else if (o instanceof Local) {
			Local other = (Local) o;
			return index == other.index;
		} else
			return false;
	}

	public SymbolType getType() {
		return type;
	}
}
