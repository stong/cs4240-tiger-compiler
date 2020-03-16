package com.gangweedganggang.cs4240.backend.mips32;

import com.gangweedganggang.cs4240.backend.TargetInsn;
import java.util.List;

import java.util.Arrays;

public abstract class MIPS32Insn extends TargetInsn<MIPS32Reg> {
    public MIPS32Insn(List<MIPS32Reg> operands) {
        super(operands);
    }

    public MIPS32Insn(MIPS32Reg... operands) {
        super(Arrays.asList(operands));
    }

    public static short checkImmediate(int imm) {
        if (imm > Short.MAX_VALUE || imm < Short.MIN_VALUE)
            throw new IllegalArgumentException("immediate exceeds 16 bits");
        return (short)imm;
    }
}
