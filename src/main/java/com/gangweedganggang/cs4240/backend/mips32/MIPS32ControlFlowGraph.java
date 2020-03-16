package com.gangweedganggang.cs4240.backend.mips32;

import com.gangweedganggang.cs4240.flowgraph.ControlFlowGraph;

public class MIPS32ControlFlowGraph extends ControlFlowGraph<MIPS32BasicBlock> {
    private String name;

    public MIPS32ControlFlowGraph(String name) {
        super(MIPS32BasicBlock::new);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
	public void verify() {
		super.verify();
	}
}
