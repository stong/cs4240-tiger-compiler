package com.gangweedganggang.cs4240.backend.mips32;

import com.gangweedganggang.cs4240.backend.TargetFunction;
import com.gangweedganggang.cs4240.backend.TargetRegPool;
import com.gangweedganggang.cs4240.backend.mips32.insn.AddiInsn;
import com.gangweedganggang.cs4240.frontend.PrimitiveSymbolType;
import com.gangweedganggang.cs4240.frontend.TigerVariable;

import static com.gangweedganggang.cs4240.backend.mips32.MIPS32Reg.*;

public class MIPS32Function extends TargetFunction<MIPS32Reg, MIPS32Insn, MIPS32BasicBlock, MIPS32ControlFlowGraph, MIPS32Function, MIPS32Target> {

    private AddiInsn enterStackAdjustInsn, exitStackAdjustInsn; // alloc locals on enter; dealloc args on exit

    public MIPS32Function(MIPS32Target isa, String name, int paramCount) {
        super(isa, name, paramCount);
    }

    @Override
    protected MIPS32ControlFlowGraph cfgFactory() {
        return new MIPS32ControlFlowGraph(name);
    }

    @Override
    protected TargetRegPool<MIPS32Reg> regsFactory() {
        return new MIPS32RegPool();
    }

    @Override
    protected void generatePrologue() {
        cfg.getEntries().add(prologue = cfg.newBlock());
        // push $ra; push $fp; mov $fp, $sp;
        // need to add sub $sp, nlocals later
        prologue.addAll(isa.push(RA));
        prologue.addAll(isa.push(FP));
        prologue.addAll(isa.move(FP, SP));
        prologue.add(enterStackAdjustInsn = isa.adjust(SP, -getLocalsSize()));
    }

    @Override
    public void setLocalsSize(int localsSize) {
        super.setLocalsSize(localsSize);
        enterStackAdjustInsn.setImmediate(-localsSize);
    }

    @Override
    protected void generateEpilogue() {
        epilogue = cfg.newBlock();
        // mov $sp, $fp; pop $fp; pop $ra; add $sp, nargs; jl $ra
        epilogue.addAll(isa.move(SP, FP));
        epilogue.addAll(isa.pop(FP));
        epilogue.addAll(isa.pop(RA));
        epilogue.add(exitStackAdjustInsn = isa.adjust(SP, paramCount));
        epilogue.addAll(isa.jmpReg(RA));
    }

    @Override
    public void setParamCount(int paramCount) {
        super.setParamCount(paramCount);
        exitStackAdjustInsn.setImmediate(isa.getWordSize() * paramCount);
    }

    private static final int LOCALS_OFFSET = -4;
    private static final int PARAMS_OFFSET = 8;

    @Override
    public Integer allocLocal(int size) {
        int off = getLocalsSize();
        setLocalsSize(off + size);
        return LOCALS_OFFSET - off;
    }

    @Override
    public int getParamFrameOffset(int index) {
        return PARAMS_OFFSET + isa.getWordSize() * index;
    }
}
