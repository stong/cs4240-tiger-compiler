package com.gangweedganggang.cs4240.backend.mips32.insn;

import com.gangweedganggang.cs4240.backend.mips32.MIPS32Insn;
import com.gangweedganggang.cs4240.backend.mips32.MIPS32Reg;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DivInsn extends MIPS32Insn {
    public DivInsn(MIPS32Reg a, MIPS32Reg b) {
        super(a, b);
    }

    public MIPS32Reg getA() {
        return getOperand(0);
    }

    public void setA(MIPS32Reg a) {
        setOperand(0, a);
    }

    public MIPS32Reg getB() {
        return getOperand(1);
    }

    public void setB(MIPS32Reg b) {
        setOperand(1, b);
    }

    public Set<Integer> defOperands() {
        return Collections.emptySet();
    }

    @Override
    public Set<Integer> useOperands() {
        return new HashSet<>(Arrays.asList(0, 1));
    }

    @Override
    public String emit() {
        return "div " + getA() + "," + getB();
    }
}
