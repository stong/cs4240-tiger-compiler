package com.gangweedganggang.cs4240.backend.mips32.insn;

import com.gangweedganggang.cs4240.backend.mips32.MIPS32Insn;
import com.gangweedganggang.cs4240.backend.mips32.MIPS32Reg;
import com.gangweedganggang.cs4240.backend.RegType;

import java.util.Collections;
import java.util.Set;

public class MoveInsn extends MIPS32Insn {
    public MoveInsn(MIPS32Reg dst, MIPS32Reg src) {
        super(dst, src);
        if (dst.getType().equals(RegType.SPECIAL))
            throw new IllegalArgumentException("dont do that.");
        if (src.getType().equals(RegType.SPECIAL) && (!src.equals(MIPS32Reg.LO) && !src.equals(MIPS32Reg.HI)))
            throw new IllegalArgumentException("dont do that.");
        if (dst.getType().equals(RegType.FLOAT) && (src.getType().equals(RegType.SPECIAL)))
            throw new IllegalArgumentException("dont do that.");
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

    @Override
    public Set<Integer> defOperands() {
        return Collections.singleton(0);
    }

    @Override
    public Set<Integer> useOperands() {
        // if (getSrc().equals(MIPS32Reg.LO)) return Collections.emptySet();
        // if (getSrc().equals(MIPS32Reg.HI)) return Collections.emptySet();
        return Collections.singleton(1);
    }

    @Override
    public String emit() {
        if (getSrc().getType().equals(RegType.SPECIAL))
            if (getSrc() == MIPS32Reg.LO)
                return "mflo " + getDst();
            else if (getSrc() == MIPS32Reg.HI)
                return "mfhi " + getDst();
            else
                throw new UnsupportedOperationException("jump die");
        boolean dstFloat = getDst().getType().equals(RegType.FLOAT);
        boolean srcFloat = getSrc().getType().equals(RegType.FLOAT);
        if (dstFloat && srcFloat)
            return "mov.s " + getDst() + "," + getSrc();
        else if (dstFloat && !srcFloat)
            return "mtc1 " + getSrc() + "," + getDst();
        else if (!dstFloat && srcFloat)
            return "mfc1 " + getDst() + "," + getSrc();
        else
            return "move " + getDst() + "," + getSrc();
    }
}
