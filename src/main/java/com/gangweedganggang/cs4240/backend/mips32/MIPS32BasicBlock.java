package com.gangweedganggang.cs4240.backend.mips32;

import com.gangweedganggang.cs4240.flowgraph.BasicBlock;
import com.gangweedganggang.cs4240.flowgraph.ControlFlowGraph;

public class MIPS32BasicBlock extends BasicBlock<MIPS32Insn> {
    public MIPS32BasicBlock(ControlFlowGraph cfg) {
        super(cfg);
    }

    @Override
    public String getDisplayName() {
        return ((MIPS32ControlFlowGraph) cfg).getName() + "_" + getNumericId();
    }

    @Override
	public String toString() {
        return getDisplayName();
    }
}
