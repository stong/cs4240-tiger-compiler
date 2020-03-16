package com.gangweedganggang.cs4240.ast;

import org.antlr.v4.runtime.tree.TerminalNode;

public class ASTReturnStmt extends AbstractASTStmt {
    public ASTReturnStmt(TerminalNode t, ASTExpr returnValue) {
        super(t, 1);
        setExpr(returnValue);
    }

    public void setExpr(ASTExpr rvalue) {
        children[0] = rvalue;
    }
    public ASTExpr getExpr() {
        return (ASTExpr) children[0];
    }

    @Override
    public String toString() {
        return "return " + getExpr() + ";";
    }
}
