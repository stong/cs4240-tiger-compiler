package com.gangweedganggang.cs4240.ast;

public class ASTLvalueExpr extends ASTExpr {

    public ASTLvalueExpr(ASTLvalue lvalue) {
        super(lvalue.row, lvalue.col, 1);
        setLvalue(lvalue);
    }

    public void setLvalue(ASTLvalue lvalue) {
        children[0] = lvalue;
    }

    public ASTLvalue getLvalue() {
        return (ASTLvalue) children[0];
    }

    @Override
    public String toString() {
        return getLvalue().toString();
    }
}
