package com.gangweedganggang.cs4240.backend.mips32.insn;

import com.gangweedganggang.cs4240.backend.PseudoInsn;
import com.gangweedganggang.cs4240.backend.TargetInsn;
import com.gangweedganggang.cs4240.backend.mips32.MIPS32Function;
import com.gangweedganggang.cs4240.backend.mips32.MIPS32Insn;
import com.gangweedganggang.cs4240.backend.mips32.MIPS32Reg;

import java.util.*;

import static com.gangweedganggang.cs4240.backend.mips32.MIPS32Reg.*;

// PSEUDO INSTRUCTIONS HMMM WONDER WHERE I GOT THAT IDEA FROM megajoy
public class CallPseudoInsn extends MIPS32Insn implements PseudoInsn<MIPS32Reg> {
    private MIPS32Function callee;
    private int numParams;

    // java is a great programming language!
    private static List<MIPS32Reg> makeOperands(MIPS32Reg dst, List<MIPS32Reg> params) {
        List<MIPS32Reg> operands = new ArrayList<>(params);
        operands.add(0, dst);
        return operands;
    }

    public CallPseudoInsn(MIPS32Function callee, MIPS32Reg dst, List<MIPS32Reg> params) {
        super(makeOperands(dst, params));
        numParams = params.size();
        if (callee == null) throw new NullPointerException();
        this.callee = callee;
    }

    public MIPS32Function getCallee() {
        return callee;
    }

    public void setCallee(MIPS32Function callee) {
        this.callee = callee;
    }

    @Override
    public Set<Integer> defOperands() {
        return Collections.singleton(0);
    }

    @Override
    public Set<Integer> useOperands() {
        Set<Integer> uses = new HashSet<>();
        for (int i = 0; i < numParams; i++)
            uses.add(i+1);
        return uses;
    }

    @Override
    public List<TargetInsn<MIPS32Reg>> baseInsns() {
        List<TargetInsn<MIPS32Reg>> insns = new ArrayList<>();
        for (int i = numParams - 1; i >= 0; i--) {
            insns.add(new AddiInsn(SP, SP, -4));
            insns.add(new StoreInsn(getOperand(1+i), SP, 0));
        }
        insns.add(new JalInsn(callee));
        if (!getOperand(0).equals(ZERO))
            insns.add(new MoveInsn(getOperand(0), V0));
        return insns;
    }

    @Override
    public String emit() {
        return PseudoInsn.super.emit();
    }
}
