package com.gangweedganggang.cs4240.stdlib.collections.itertools;

import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

public class FilterIterable<T> implements Iterable<T> {
    private final Iterable<T> backingIterable;
    private final Predicate<T> predicate;

    public FilterIterable(Iterable<T> backingIterable, Predicate<T> predicate) {
        this.backingIterable = backingIterable;
        this.predicate = predicate;
    }

    @Override
    public Iterator<T> iterator() {
        return StreamSupport.stream(backingIterable.spliterator(), false).filter(predicate).iterator();
    }
}
