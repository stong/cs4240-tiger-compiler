package com.gangweedganggang.cs4240.backend.mips32.insn;

import com.gangweedganggang.cs4240.backend.mips32.MIPS32Insn;

import java.util.Collections;
import java.util.Set;

public class BreakInsn extends MIPS32Insn {
    protected int trapCode;

    public BreakInsn(int trapCode) {
        super();
        this.trapCode = trapCode;
    }

    @Override
    public Set<Integer> defOperands() {
        return Collections.emptySet();
    }

    @Override
    public Set<Integer> useOperands() {
        return Collections.emptySet();
    }

    @Override
    public String emit() {
        return "break " + trapCode;
    }

}
