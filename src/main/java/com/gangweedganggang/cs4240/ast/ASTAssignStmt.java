package com.gangweedganggang.cs4240.ast;

import org.antlr.v4.runtime.tree.TerminalNode;

public class ASTAssignStmt extends AbstractASTStmt {
    public ASTAssignStmt(TerminalNode t, ASTLvalue lvalue, ASTExpr rvalue) {
        super(t, 2);
        setLvalue(lvalue);
        setRvalue(rvalue);
    }

    public ASTAssignStmt(TerminalNode t, ASTExpr rValue) {
        super(t, 2);
        setLvalue(new ASTLvalue(
                t.getSymbol().getLine(),
                t.getSymbol().getCharPositionInLine(),
                t.getText(),
                null));
        setRvalue(rValue);
    }

    public void setLvalue(ASTLvalue lvalue) {
        children[0] = lvalue;
    }

    public void setRvalue(ASTExpr rvalue) {
        children[1] = rvalue;
    }

    public ASTLvalue getLvalue() {
        return (ASTLvalue) children[0];
    }

    public ASTExpr getRvalue() {
        return (ASTExpr) children[1];
    }

    @Override
    public String toString() {
        return getLvalue() + " := " + getRvalue();
    }
}
