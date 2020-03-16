package com.gangweedganggang.cs4240.backend.mips32.insn;

import com.gangweedganggang.cs4240.backend.RegType;
import com.gangweedganggang.cs4240.backend.mips32.MIPS32Insn;
import com.gangweedganggang.cs4240.backend.mips32.MIPS32Reg;

import java.util.Collections;
import java.util.Set;

public class LoadImmInsn extends MIPS32Insn {
    private int immediate;

    public LoadImmInsn(MIPS32Reg dst, int immediate) {
        super(dst);
        this.immediate = immediate;
    }

    public MIPS32Reg getDst() {
        return getOperand(0);
    }

    public void setDst(MIPS32Reg dst) {
        setOperand(0, dst);
    }

    public int getImmediate() {
        return immediate;
    }

    public void setImmediate(int immediate) {
        this.immediate = immediate;
    }

    @Override
    public Set<Integer> defOperands() {
        return Collections.singleton(0);
    }

    @Override
    public Set<Integer> useOperands() {
        return Collections.emptySet();
    }

    @Override
    public String emit() {
        if (getDst().getType().equals(RegType.FLOAT))
            return "li.s " + getDst() + "," + Float.intBitsToFloat(immediate); // LOL HORRIBLE
        else
            return "li " + getDst() + "," + immediate;
    }
}
