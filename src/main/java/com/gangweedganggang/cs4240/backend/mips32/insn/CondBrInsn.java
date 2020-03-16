package com.gangweedganggang.cs4240.backend.mips32.insn;

import com.gangweedganggang.cs4240.backend.mips32.MIPS32BasicBlock;
import com.gangweedganggang.cs4240.backend.mips32.MIPS32Insn;
import com.gangweedganggang.cs4240.backend.mips32.MIPS32Reg;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CondBrInsn extends MIPS32Insn {
    protected MIPS32BasicBlock dst;
    protected ComparisonOperator op;

    public enum ComparisonOperator {
        EQ,
        NE,
        LT,
        GT,
        GE,
        LE;

        public String toString() {
            return name().toLowerCase();
        }
    }

    public CondBrInsn(MIPS32BasicBlock dst, MIPS32Reg a, MIPS32Reg b, ComparisonOperator op) {
        super(a,b);
        this.dst = dst;
        this.op = op;
    }

    public MIPS32BasicBlock getDst() {
        return dst;
    }

    public void setDst(MIPS32BasicBlock dst) {
        this.dst = dst;
    }

    public ComparisonOperator getOp() {
        return op;
    }

    public void setOp(ComparisonOperator op) {
        this.op = op;
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
        return "b" + op + " " + getA() + "," + getB() + "," + dst;
    }
}
