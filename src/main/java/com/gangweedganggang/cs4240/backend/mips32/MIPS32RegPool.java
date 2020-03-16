package com.gangweedganggang.cs4240.backend.mips32;

import com.gangweedganggang.cs4240.backend.RegType;
import com.gangweedganggang.cs4240.backend.TargetRegPool;
import com.gangweedganggang.cs4240.stdlib.collections.bitset.BitSetIndexer;
import com.gangweedganggang.cs4240.stdlib.collections.bitset.GenericBitSet;
import com.gangweedganggang.cs4240.stdlib.collections.bitset.IncrementalBitSetIndexer;
import com.gangweedganggang.cs4240.stdlib.collections.map.ValueCreator;

import java.util.*;

import static com.gangweedganggang.cs4240.backend.mips32.MIPS32Reg.*;

// hate this class. bad design. separate the Flyweight pattern from 'target reg info'
public class MIPS32RegPool implements TargetRegPool<MIPS32Reg>, ValueCreator<GenericBitSet<MIPS32Reg>> {
    private final Map<String, MIPS32Reg> cache;
    private final BitSetIndexer<MIPS32Reg> indexer;

    public MIPS32RegPool() {
        cache = new HashMap<>();
        indexer = new IncrementalBitSetIndexer<>();
    }

    // factory
    public GenericBitSet<MIPS32Reg> createBitSet() {
        return new GenericBitSet<>(indexer);
    }

    @Override
    public GenericBitSet<MIPS32Reg> create() {
        return createBitSet();
    }
    // end factory

    public List<MIPS32Reg> getOrderedList() {
        List<MIPS32Reg> list = new ArrayList<>();
        list.addAll(cache.values());
        Collections.sort(list);
        return list;
    }

    public MIPS32Reg get(int index, RegType type) {
        String key = key(index, type);
        if (cache.containsKey(key)) {
            return  cache.get(key);
        } else {
            MIPS32Reg v = type == RegType.INT ? MIPS32Reg.newInt(index) : MIPS32Reg.newFloat(index);
            cache.put(key, v);
            return v;
        }
    }

    public MIPS32Reg newLocal(int i, RegType type) {
        while (true) {
            String key = key(i, type);
            if (!cache.containsKey(key)) {
                return get(i, type);
            }
            i++;
        }
    }

    @Override
    public MIPS32Reg getTemp(RegType type) {
        return newLocal(MIPS32Reg.MAX_REG, type);
    }

    private final MIPS32Reg[] volatileIntRegs = new MIPS32Reg[] { A0, A1, A2, A3, T0, T1, T2, T3, T4, T5, T6, T7, S0, S1, S2, S3, S4, S5, S6, S7, T8, T9 };

    @Override
    public int numRegs(RegType type) {
        if (type.equals(RegType.INT))
            return volatileIntRegs.length;
        else if (type.equals(RegType.FLOAT))
            return 32;
        else
            throw new IllegalArgumentException("jump die");
    }

    @Override
    public MIPS32Reg getReg(RegType type, int i) {
        if (type.equals(RegType.INT))
            return volatileIntRegs[i];
        else if (type.equals(RegType.FLOAT))
            return MIPS32Reg.newFloat(i);
        else
            throw new IllegalArgumentException("jump die");
    }

    // crappy design
    @Override
    public RegType getRegType(MIPS32Reg reg) {
        return (RegType)reg.getType();
    }

    private static String key(int index, RegType type) {
        return type.name.toLowerCase().substring(0, 1) + index;
    }
}