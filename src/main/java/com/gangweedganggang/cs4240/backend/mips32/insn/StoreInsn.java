package com.gangweedganggang.cs4240.backend.mips32.insn;

import com.gangweedganggang.cs4240.backend.mips32.MIPS32Insn;
import com.gangweedganggang.cs4240.backend.mips32.MIPS32Reg;
import com.gangweedganggang.cs4240.backend.RegType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class StoreInsn extends MIPS32Insn {
    private short offset;

    public StoreInsn(MIPS32Reg src, MIPS32Reg dst, int offset) {
        super(dst, src);
        this.offset = checkImmediate(offset);
    }

    public MIPS32Reg getDst() {
        return getOperand(0);
    }

    public void setDst(MIPS32Reg dst) {
        setOperand(0, dst);
    }

    public MIPS32Reg getSrc() {
        return getOperand(1);
    }

    public void setSrc(MIPS32Reg src) {
        setOperand(1, src);
    }

    public short getOffset() {
        return offset;
    }

    public void setOffset(short offset) {
        this.offset = offset;
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
        if (getSrc().getType().equals(RegType.FLOAT))
            return "s.s " + getSrc()  + "," + offset + "(" + getDst() + ")";
        else
            return "sw " + getSrc()  + "," + offset + "(" + getDst() + ")";
    }
}
