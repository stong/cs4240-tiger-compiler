package com.gangweedganggang.cs4240.backend.mips32.insn;

import com.gangweedganggang.cs4240.backend.mips32.MIPS32Insn;
import com.gangweedganggang.cs4240.backend.mips32.MIPS32Reg;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JumpRegInsn extends MIPS32Insn {
    public JumpRegInsn(MIPS32Reg dst) {
        super(dst);
    }

    public MIPS32Reg getDst() {
        return getOperand(0);
    }

    public void setDst(MIPS32Reg dst) {
        setOperand(0, dst);
    }

    @Override
    public Set<Integer> defOperands() {
        return new HashSet<>();
    }

    @Override
    public Set<Integer> useOperands() {
        return Collections.singleton(0);
    }

    @Override
    public String emit() {
        return "jr " + getDst();
    }
}
