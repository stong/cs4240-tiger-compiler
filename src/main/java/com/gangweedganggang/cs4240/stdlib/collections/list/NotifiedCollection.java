package com.gangweedganggang.cs4240.stdlib.collections.list;

import java.util.Collection;
import java.util.function.Consumer;

public interface NotifiedCollection<T> extends Collection<T> {
    void subscribeAdded(Consumer<T> callback);
    void unsubscribeAdded(Consumer<T> callback);

    void subscribeRemoved(Consumer<T> callback);
    void unsubscribeRemoved(Consumer<T> callback);
}
