package com.gangweedganggang.cs4240.stdlib.collections.itertools;

import java.util.stream.StreamSupport;

public class IterableUtils {
    public static <T> boolean contains(Iterable<T> iterable, T t) {
        return StreamSupport.stream(iterable.spliterator(), false).anyMatch(t2 -> t2.equals(t));
    }
}
