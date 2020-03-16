package com.gangweedganggang.cs4240.ast;

import org.antlr.v4.runtime.tree.TerminalNode;

public class ASTIfStmt extends AbstractASTStmt {
    public ASTExpr cond;

    public ASTIfStmt(TerminalNode t, ASTExpr cond, ASTBlock ifBlock, ASTBlock elseBlock) {
        super(t, 3);
        setCondition(cond);
        setIfBlock(ifBlock);
        setElseBlock(elseBlock);
    }

    public ASTExpr getCondition() {
        return (ASTExpr) children[0];
    }

    public void setCondition(ASTExpr cond) {
        children[0] = cond;
    }

    public ASTBlock getIfBlock() {
        return (ASTBlock) children[1];
    }

    public void setIfBlock(ASTBlock block) {
        children[1] = block;
    }

    public ASTBlock getElseBlock() {
        return (ASTBlock) children[2];
    }

    public void setElseBlock(ASTBlock block) {
        children[2] = block;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("if ( ").append(cond).append(" ) ").append(getIfBlock());
        if (getElseBlock() != null) {
            sb.append(" else ").append(getElseBlock());
        }
        return sb.toString();
    }
}
