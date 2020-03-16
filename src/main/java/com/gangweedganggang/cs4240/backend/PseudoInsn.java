package com.gangweedganggang.cs4240.backend;

import java.util.List;

// PSEUDO INSTRUCTIONS HMMM WONDER WHERE I GOT THAT IDEA FROM megajoy
public interface PseudoInsn<L extends TargetReg> {
    List<TargetInsn<L>> baseInsns();

    default String emit() {
        StringBuilder s = new StringBuilder();
        for (TargetInsn<L> insn : baseInsns()) {
            s.append(insn).append("\n");
        }
        return s.substring(0, s.length()-1); // trim newline
    }
}
