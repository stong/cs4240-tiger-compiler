package com.gangweedganggang.cs4240.backend.mips32.insn;

import com.gangweedganggang.cs4240.backend.mips32.MIPS32Insn;

import java.util.Collections;
import java.util.Set;

public class SyscallInsn extends MIPS32Insn {
    public SyscallInsn() {
        super();
    }

    public Set<Integer> defOperands() {
        return Collections.emptySet();
    }

    @Override
    public Set<Integer> useOperands() {
        return Collections.emptySet();
    }

    @Override
    public String emit() {
        return "syscall";
    }
}
