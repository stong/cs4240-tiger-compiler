package com.gangweedganggang.cs4240.ast;

import org.antlr.v4.runtime.tree.TerminalNode;

public abstract class AbstractASTStmt extends AbstractASTNode {
    private String codeFragment; // source code fragment associated with this stmt

    public AbstractASTStmt(int line, int col, int n) {
        super(line, col, n);
    }

    public AbstractASTStmt(TerminalNode t, int n) {
        this(t.getSymbol().getLine(), t.getSymbol().getCharPositionInLine(), n);
        codeFragment = t.getParent().getText(); // ugly kludge
    }

    public String getCode() {
        return codeFragment;
    }
}
