package com.gangweedganggang.cs4240.backend.mips32.insn;

import com.gangweedganggang.cs4240.backend.mips32.MIPS32Function;
import com.gangweedganggang.cs4240.backend.mips32.MIPS32Insn;

import java.util.Collections;
import java.util.Set;

public class JalInsn extends MIPS32Insn {
    private MIPS32Function callee;

    public JalInsn(MIPS32Function callee) {
        if (callee == null) throw new NullPointerException();
        this.callee = callee;
    }

    public MIPS32Function getCallee() {
        return callee;
    }

    public void setCallee(MIPS32Function callee) {
        this.callee = callee;
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
        return "jal " + callee.name;
    }
}
