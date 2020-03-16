package com.gangweedganggang.cs4240.stdlib.collections.list;

import java.util.function.Consumer;

public class NotifiedIndexedList<E> extends IndexedList<E> implements NotifiedCollection<E> {
    @Override
    public void subscribeAdded(Consumer<E> callback) {
        notifier.subscribeAdded(callback);
    }

    @Override
    public void unsubscribeAdded(Consumer<E> callback) {
        notifier.unsubscribeAdded(callback);
    }

    @Override
    public void subscribeRemoved(Consumer<E> callback) {
        notifier.subscribeRemoved(callback);
    }

    @Override
    public void unsubscribeRemoved(Consumer<E> callback) {
        notifier.unsubscribeRemoved(callback);
    }
}
