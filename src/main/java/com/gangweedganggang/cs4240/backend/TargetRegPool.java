package com.gangweedganggang.cs4240.backend;

import com.gangweedganggang.cs4240.flowgraph.Local;
import com.gangweedganggang.cs4240.stdlib.collections.bitset.GenericBitSet;
import com.gangweedganggang.cs4240.stdlib.collections.map.ValueCreator;

public interface TargetRegPool<Reg extends Local> extends ValueCreator<GenericBitSet<Reg>> {
    Reg getTemp(RegType type);

    int numRegs(RegType type);

    Reg getReg(RegType type, int i);

    RegType getRegType(Reg reg);
}
