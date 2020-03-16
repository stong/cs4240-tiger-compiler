package com.gangweedganggang.cs4240.ast;

public class ASTBinOpExpr extends ASTExpr {

    public final TigerBinOp operation;

    public ASTBinOpExpr(int line, int col, TigerBinOp operation, ASTExpr left, ASTExpr right) {
        super(line, col, 2);
        this.operation = operation;
        setOperand1(left);
        setOperand2(right);
    }

    public void setOperand1(ASTExpr lhs) {
        children[0] = lhs;
    }

    public void setOperand2(ASTExpr rhs) {
        children[1] = rhs;
    }

    public ASTExpr getOperand1() {
        return (ASTExpr) children[0];
    }

    public ASTExpr getOperand2() {
        return (ASTExpr) children[1];
    }

    @Override
    public String toString() {
        return "( " + getOperand1() + " " + operation + " " + getOperand2() + " )";
    }
}
