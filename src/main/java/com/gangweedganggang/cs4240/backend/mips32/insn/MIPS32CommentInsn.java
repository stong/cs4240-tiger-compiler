package com.gangweedganggang.cs4240.backend.mips32.insn;

import com.gangweedganggang.cs4240.backend.mips32.MIPS32Insn;

import java.util.Collections;
import java.util.Set;

public class MIPS32CommentInsn extends MIPS32Insn {
    private final String comment;

    public MIPS32CommentInsn(String comment) {
        super(Collections.emptyList());
        this.comment = comment;
    }

    @Override
    public String emit() {
        return null;
    }

    @Override
    public String toString() {
        return "# " + comment;
    }

    @Override
    public Set<Integer> defOperands() {
        return Collections.emptySet();
    }

    @Override
    public Set<Integer> useOperands() {
        return Collections.emptySet();
    }
}
