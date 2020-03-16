package com.gangweedganggang.cs4240.backend.mips32.insn;

import com.gangweedganggang.cs4240.backend.mips32.MIPS32Insn;
import com.gangweedganggang.cs4240.backend.mips32.MIPS32Reg;

import java.util.Collections;
import java.util.Set;

public class AddiInsn extends MIPS32Insn {
    private short immediate;

    public AddiInsn(MIPS32Reg dst, MIPS32Reg src, int immediate) {
        super(dst, src);
        this.immediate = checkImmediate(immediate);
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
        this.setOperand(1, src);
    }

    public short getImmediate() {
        return immediate;
    }

    public void setImmediate(int immediate) {
        this.immediate = checkImmediate(immediate);
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
        return "addi " + getDst()  + "," + getSrc() + "," + immediate;
    }
}
