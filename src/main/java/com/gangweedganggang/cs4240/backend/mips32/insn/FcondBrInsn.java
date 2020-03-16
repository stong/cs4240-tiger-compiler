package com.gangweedganggang.cs4240.backend.mips32.insn;

import com.gangweedganggang.cs4240.backend.mips32.MIPS32BasicBlock;
import com.gangweedganggang.cs4240.backend.mips32.MIPS32Insn;

import java.util.Collections;
import java.util.Set;

public class FcondBrInsn extends MIPS32Insn {
    private MIPS32BasicBlock dst;
    private boolean cond;

    public FcondBrInsn(MIPS32BasicBlock dst, boolean cond) {
        super();
        this.dst = dst;
        this.cond = cond;
    }

    public boolean isCond() {
        return cond;
    }

    public void setCond(boolean cond) {
        this.cond = cond;
    }

    public MIPS32BasicBlock getDst() {
        return dst;
    }

    public void setDst(MIPS32BasicBlock dst) {
        this.dst = dst;
    }

    @Override
    public Set<Integer> defOperands() {
        return Collections.emptySet();
    }

    @Override
    public Set<Integer> useOperands() {
        return Collections.emptySet();
    }

    @Override
    public String emit() {
        return "bc1" + (cond? "t" : "f") + " " + dst;
    }
}
