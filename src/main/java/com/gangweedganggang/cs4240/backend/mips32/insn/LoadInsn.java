package com.gangweedganggang.cs4240.backend.mips32.insn;

import com.gangweedganggang.cs4240.backend.mips32.MIPS32Insn;
import com.gangweedganggang.cs4240.backend.mips32.MIPS32Reg;
import com.gangweedganggang.cs4240.backend.RegType;

import java.util.Collections;
import java.util.Set;

public class LoadInsn extends MIPS32Insn {
    private short offset;

    public LoadInsn(MIPS32Reg dst, MIPS32Reg src, int offset) {
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
        return Collections.singleton(0);
    }

    @Override
    public Set<Integer> useOperands() {
        return Collections.singleton(1);
    }

    @Override
    public String emit() {
        if (getDst().getType().equals(RegType.FLOAT))
            return "l.s " + getDst()  + "," + offset + "(" + getSrc() + ")";
        else
            return "lw " + getDst()  + "," + offset + "(" + getSrc() + ")";
    }
}
