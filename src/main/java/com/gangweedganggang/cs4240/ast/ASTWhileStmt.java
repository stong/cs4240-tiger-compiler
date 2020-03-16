package com.gangweedganggang.cs4240.ast;

import org.antlr.v4.runtime.tree.TerminalNode;

public class ASTWhileStmt extends AbstractASTStmt {
    public ASTWhileStmt(int row, int col, ASTExpr cond, ASTBlock block) {
        super(row, col, 3);
        setCondition(cond);
        setBlock(block);
    }

    public ASTWhileStmt(TerminalNode t, ASTExpr cond, ASTBlock block) {
        super(t, 3);
        setCondition(cond);
        setBlock(block);
    }

    public ASTAssignStmt getInitial() {
        return (ASTAssignStmt) children[0];
    }

    public void setInitial(ASTAssignStmt initial) {
        children[0] = initial;
    }

    public ASTExpr getCondition() {
        return (ASTExpr) children[1];
    }

    public void setCondition(ASTExpr cond) {
        children[1] = cond;
    }

    public ASTBlock getBlock() {
        return (ASTBlock) children[2];
    }

    public void setBlock(ASTBlock block) {
        children[2] = block;
    }


    @Override
    public String toString() {
        return "WHILE( " + getCondition() + " ) " + getBlock();
    }
}
