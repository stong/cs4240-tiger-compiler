package com.gangweedganggang.cs4240.frontend;

import com.gangweedganggang.cs4240.ir.IRLocal;
import com.gangweedganggang.cs4240.stdlib.collections.bitset.BitSetIndexer;
import com.gangweedganggang.cs4240.stdlib.collections.bitset.GenericBitSet;
import com.gangweedganggang.cs4240.stdlib.collections.bitset.IncrementalBitSetIndexer;
import com.gangweedganggang.cs4240.stdlib.collections.map.ValueCreator;

import java.util.*;

// Non-SSA locals info
public class LocalsPool implements ValueCreator<GenericBitSet<IRLocal>> {
    private final Map<String, IRLocal> cache;
    private final BitSetIndexer<IRLocal> indexer;

    public LocalsPool() {
        cache = new HashMap<>();
        indexer = new IncrementalBitSetIndexer<>();
    }

    // factory
    public GenericBitSet<IRLocal> createBitSet() {
        return new GenericBitSet<>(indexer);
    }

    @Override
    public GenericBitSet<IRLocal> create() {
        return createBitSet();
    }
    // end factory

    public List<IRLocal> getOrderedList() {
        List<IRLocal> list = new ArrayList<>();
        list.addAll(cache.values());
        Collections.sort(list);
        return list;
    }

    public IRLocal get(int index, SymbolType type) {
        String key = key(index);
        if (cache.containsKey(key)) {
            return (IRLocal) cache.get(key);
        } else {
            IRLocal v = new IRLocal(index, type);
            cache.put(key, v);
            return v;
        }
    }

    public IRLocal newLocal(int i, SymbolType type) {
        while (true) {
            String key = key(i);
            if (!cache.containsKey(key)) {
                return get(i, type);
            }
            i++;
        }
    }

    public IRLocal getNextFree(SymbolType type) {
        return newLocal(0, type);
    }

    public static String key(int index) {
        return "var" + index;
    }
}
