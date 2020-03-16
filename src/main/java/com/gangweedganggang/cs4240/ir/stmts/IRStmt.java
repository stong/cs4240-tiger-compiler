package com.gangweedganggang.cs4240.ir.stmts;

import com.gangweedganggang.cs4240.flowgraph.Stmt;
import com.gangweedganggang.cs4240.ir.IRLocal;

import java.util.Collections;
import java.util.Set;

public abstract class IRStmt extends Stmt<IRLocal> {
    public IRStmt() {
        super(Collections.emptyList());
    }

    @Override
    public Set<Integer> defOperands() {
        throw new UnsupportedOperationException("dont feel like refactoring this rn");
    }

    @Override
    public Set<Integer> useOperands() {
        throw new UnsupportedOperationException("dont feel like refactoring this rn");
    }
}
