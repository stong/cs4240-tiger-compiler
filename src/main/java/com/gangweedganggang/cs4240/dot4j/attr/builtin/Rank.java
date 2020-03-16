package com.gangweedganggang.cs4240.dot4j.attr.builtin;

import com.gangweedganggang.cs4240.dot4j.attr.Attr;

public class Rank extends Attr<String> {

	public static final Rank SAME = new Rank("same"), MIN = new Rank("min"), MAX = new Rank("max"),
			SOURCE = new Rank("source"), SINK = new Rank("sink");

	protected Rank(String value) {
		super("rank", value);
	}
}
