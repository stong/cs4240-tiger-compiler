package com.gangweedganggang.cs4240.backend.mips32.insn;

import com.gangweedganggang.cs4240.backend.mips32.MIPS32Insn;
import com.gangweedganggang.cs4240.backend.mips32.MIPS32Reg;
import com.gangweedganggang.cs4240.backend.RegType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FsubInsn extends MIPS32Insn {
    public FsubInsn(MIPS32Reg dst, MIPS32Reg a, MIPS32Reg b) {
       super(dst, a, b);
        if (dst.getType() != RegType.FLOAT || a.getType() != RegType.FLOAT || b.getType() != RegType.FLOAT)
            throw new IllegalArgumentException("jump die");
    }

    public MIPS32Reg getDst() {
        return getOperand(0);
    }

    public void setDst(MIPS32Reg dst) {
        setOperand(0, dst);
    }

    public MIPS32Reg getA() {
        return getOperand(1);
    }

    public void setA(MIPS32Reg a) {
        setOperand(1, a);
    }

    public MIPS32Reg getB() {
        return getOperand(2);
    }

    public void setB(MIPS32Reg b) {
        setOperand(2, b);
    }

    public Set<Integer> defOperands() {
        return Collections.singleton(0);
    }

    @Override
    public Set<Integer> useOperands() {
        return new HashSet<>(Arrays.asList(1, 2));
    }

    @Override
    public String emit() {
        return "sub.s " + getDst()  + "," + getA() + "," + getB();
    }
}
