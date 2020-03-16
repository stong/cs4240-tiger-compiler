package com.gangweedganggang.cs4240.ast;

import org.antlr.v4.runtime.tree.TerminalNode;

public class ASTBreakStmt extends AbstractASTStmt {
    public ASTBreakStmt(TerminalNode t) {
        super(t, 0);
    }

    @Override
    public String toString() {
        return "break;";
    }
}
