package com.gangweedganggang.cs4240.backend;

import com.gangweedganggang.cs4240.flowgraph.BasicBlock;
import com.gangweedganggang.cs4240.flowgraph.ControlFlowGraph;
import com.gangweedganggang.cs4240.flowgraph.Local;
import com.gangweedganggang.cs4240.flowgraph.Stmt;
import com.gangweedganggang.cs4240.frontend.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.gangweedganggang.cs4240.frontend.PrimitiveSymbolType.FLOAT;
import static com.gangweedganggang.cs4240.frontend.PrimitiveSymbolType.INT;
import static com.gangweedganggang.cs4240.frontend.VoidSymbolType.VOID;

public class BackendContext<Reg extends TargetReg, Insn extends TargetInsn<Reg>,
        BB extends BasicBlock<Insn>, CFG extends ControlFlowGraph<BB>,
        ISA extends TargetISA<Reg, Insn, BB, CFG, Func, ISA>,
        Func extends TargetFunction<Reg, Insn, BB, CFG, Func, ISA>> {

    public static class Intrinsics {
        public static ITigerFunction MEMSET = new TigerExternFunction("_memset", new FunctionSymbolType(VOID, Arrays.asList(INT.arrayOfSize(0), INT, INT)));
        public static ITigerFunction POWF = new TigerExternFunction("_powf", new FunctionSymbolType(FLOAT, Arrays.asList(FLOAT, FLOAT)));
        public static ITigerFunction POW = new TigerExternFunction("_pow", new FunctionSymbolType(INT, Arrays.asList(INT, INT)));
    }

    final Map<ITigerFunction, Func> functionMap;
    final Map<TigerVariable, Integer> globalsOffsetMap;
    int globalsSize;

    final ISA isa;

    public BackendContext(ISA isa) {
        this.isa = isa;
        functionMap = new HashMap<>();
        globalsOffsetMap = new HashMap<>();
        globalsSize = 0;
    }

    protected Map<ITigerFunction, Func> getFunctionMap() {
        return functionMap;
    }

    public ISA getIsa() {
        return isa;
    }

    public Map<TigerVariable, Integer> getGlobalsOffsetMap() {
        return globalsOffsetMap;
    }

    public void addGlobal(TigerVariable var) {
        globalsOffsetMap.put(var, globalsSize);
        globalsSize += isa.getTypeSize(var.type);
    }
}
