package com.gangweedganggang.cs4240.backend;

import com.gangweedganggang.cs4240.flowgraph.BasicBlock;
import com.gangweedganggang.cs4240.flowgraph.ControlFlowGraph;
import com.gangweedganggang.cs4240.frontend.PrimitiveSymbolType;
import com.gangweedganggang.cs4240.frontend.TigerVariable;

import java.util.HashMap;
import java.util.Map;

// Handle the stack frame crap
public abstract class TargetFunction<Reg extends TargetReg, Insn extends TargetInsn<Reg>,
        BB extends BasicBlock<Insn>, CFG extends ControlFlowGraph<BB>,
        Func extends TargetFunction<Reg, Insn, BB, CFG, Func, ISA>,
        ISA extends TargetISA<Reg, Insn, BB, CFG, Func, ISA>> {
    protected final ISA isa;
    public final String name;
    protected final CFG cfg;
    protected final TargetRegPool<Reg> regPool;

    protected final Map<TigerVariable, Integer> stackFrameMap;

    protected int paramCount;
    private int localsSize;
    protected BB prologue, epilogue;

    public TargetFunction(ISA isa, String name, int paramCount) {
        this.isa = isa;
        this.name = name.replaceAll("!", "");
        this.cfg = cfgFactory();
        regPool = regsFactory();

        this.stackFrameMap = new HashMap<>();

        this.paramCount = paramCount;
        localsSize = 0;
        generatePrologue();
        generateEpilogue();
    }

    abstract protected CFG cfgFactory();
    abstract protected TargetRegPool<Reg> regsFactory();

    abstract protected void generatePrologue();
    abstract protected void generateEpilogue();

    public int getLocalsSize() {
        return localsSize;
    }

    public void setLocalsSize(int localsSize) {
        this.localsSize = localsSize;
    }

    public void setParamCount(int paramCount) {
        this.paramCount = paramCount;
    }

    public CFG getCfg() {
        return cfg;
    }

    public TargetRegPool<Reg> getRegPool() {
        return regPool;
    }

    public int getParamCount() {
        return paramCount;
    }

    public BB getPrologue() {
        return prologue;
    }

    public BB getEpilogue() {
        return epilogue;
    }

    public Integer getLocalFrameOffset(TigerVariable var) {
        return stackFrameMap.get(var);
    }

    public boolean haveLocal(TigerVariable var) {
        return stackFrameMap.containsKey(var);
    }

    public Integer allocLocal(TigerVariable var) {
        PrimitiveSymbolType primType = var.type.getPrimitiveType();
        int size = isa.getTypeSize(var.type);
        int off = allocLocal(size);
        stackFrameMap.put(var, off);
        return off;
    }

    // return the frame pointer offset of the newly allocated thang
    public abstract Integer allocLocal(int size);

    public abstract int getParamFrameOffset(int index);
}
