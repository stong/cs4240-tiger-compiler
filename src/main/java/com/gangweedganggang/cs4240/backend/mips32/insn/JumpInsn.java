package com.gangweedganggang.cs4240.backend.mips32.insn;

import com.gangweedganggang.cs4240.backend.mips32.MIPS32BasicBlock;
import com.gangweedganggang.cs4240.backend.mips32.MIPS32Insn;

import java.util.HashSet;
import java.util.Set;

public class JumpInsn extends MIPS32Insn {
    private MIPS32BasicBlock dst;

    public JumpInsn(MIPS32BasicBlock dst) {
        super();
        this.dst = dst;
    }

    public MIPS32BasicBlock getDst() {
        return dst;
    }

    public void setDst(MIPS32BasicBlock dst) {
        this.dst = dst;
    }

    @Override
    public Set<Integer> defOperands() {
        return new HashSet<>();
    }

    @Override
    public Set<Integer> useOperands() {
        return new HashSet<>();
    }

    @Override
    public String emit() {
        return "j " + dst;
    }
}
