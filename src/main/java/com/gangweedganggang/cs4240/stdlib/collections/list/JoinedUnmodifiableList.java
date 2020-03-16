package com.gangweedganggang.cs4240.stdlib.collections.list;

import java.util.AbstractList;
import java.util.List;

public class JoinedUnmodifiableList<E> extends AbstractList<E> {

    private final List<E> list1;
    private final List<E> list2;

    public JoinedUnmodifiableList(List<E> list1, List<E> list2) {
        this.list1 = list1;
        this.list2 = list2;
    }

    @Override
    public E get(int index) {
        if (index < list1.size()) {
            return list1.get(index);
        }
        return list2.get(index-list1.size());
    }

    @Override
    public int size() {
        return list1.size() + list2.size();
    }
}
