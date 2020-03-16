package com.gangweedganggang.cs4240.ast;

import org.antlr.v4.runtime.tree.TerminalNode;

public class ASTForStmt extends AbstractASTStmt {

    public ASTForStmt(int row, int col, ASTAssignStmt initial, ASTExpr upper, AbstractASTStmt block) {
        super(row, col, 3);
        setInitial(initial);
        setUpperBound(upper);
        setBlock(block);
    }

    public ASTForStmt(TerminalNode t, ASTAssignStmt initial, ASTExpr upper, AbstractASTStmt block) {
        super(t, 3);
        setInitial(initial);
        setUpperBound(upper);
        setBlock(block);
    }

    public ASTAssignStmt getInitial() {
        return (ASTAssignStmt) children[0];
    }

    public void setInitial(ASTAssignStmt initial) {
        children[0] = initial;
    }

    public ASTExpr getUpperBound() {
        return (ASTExpr) children[1];
    }

    public void setUpperBound(ASTExpr cond) {
        children[1] = cond;
    }

    public ASTBlock getBlock() {
        return (ASTBlock) children[2];
    }

    public void setBlock(AbstractASTStmt block) {
        children[2] = block;
    }

    @Override
    public String toString() {
        return "FOR( " + getInitial() + " to " + getUpperBound() + ") { " + getBlock()  + " }";
    }
}
