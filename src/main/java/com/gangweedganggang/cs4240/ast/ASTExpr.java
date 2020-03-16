package com.gangweedganggang.cs4240.ast;

public abstract class ASTExpr extends AbstractASTNode {

    public TigerType type;

    public ASTExpr(int line, int col, int n) {
        super(line, col, n);
    }
}
