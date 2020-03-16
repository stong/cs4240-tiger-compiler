package com.gangweedganggang.cs4240.ir;

import com.gangweedganggang.cs4240.flowgraph.BasicBlock;
import com.gangweedganggang.cs4240.flowgraph.ControlFlowGraph;
import com.gangweedganggang.cs4240.ir.stmts.IRStmt;

public class IRBasicBlock extends BasicBlock<IRStmt> {
    public IRBasicBlock(ControlFlowGraph cfg) {
        super(cfg);
    }
}
