package com.gangweedganggang.cs4240.backend.mips32.insn;

import com.gangweedganggang.cs4240.backend.mips32.MIPS32Insn;
import com.gangweedganggang.cs4240.backend.mips32.MIPS32Reg;
import com.gangweedganggang.cs4240.backend.RegType;

import java.util.Collections;
import java.util.Set;

// fp to int
public class CvtWSInsn extends MIPS32Insn {
    public CvtWSInsn(MIPS32Reg dst, MIPS32Reg src) {
        super(dst, src);
        if (src.getType() != RegType.FLOAT || dst.getType() != RegType.FLOAT)
            throw new IllegalArgumentException("jump die");
    }

    public void setSrc(MIPS32Reg src) {
        setOperand(1, src);
    }

    public void setDst(MIPS32Reg dst) {
        setOperand(0, dst);
    }

    public MIPS32Reg getSrc() {
        return getOperand(1);
    }

    public MIPS32Reg getDst() {
        return getOperand(0);
    }

    @Override
    public Set<Integer> defOperands() {
        return Collections.singleton(0);
    }

    @Override
    public Set<Integer> useOperands() {
        return Collections.singleton(1);
    }

    @Override
    public String emit() {
        return "cvt.w.s " + getDst() + "," + getSrc();
    }
}
