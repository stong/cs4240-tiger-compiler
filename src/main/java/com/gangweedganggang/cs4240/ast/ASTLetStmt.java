package com.gangweedganggang.cs4240.ast;

import org.antlr.v4.runtime.tree.TerminalNode;

public class ASTLetStmt extends AbstractASTStmt {
    public ASTLetStmt(int line, int col, ASTDeclSegment decls, ASTBlock body) {
        super(line, col, 3);
        setDecls(decls);
        setBody(body);
    }

    public ASTLetStmt(TerminalNode t, ASTDeclSegment decls, ASTBlock body) {
        super(t.getSymbol().getLine(), t.getSymbol().getCharPositionInLine(), 3);
        setDecls(decls);
        setBody(body);
    }

    public ASTDeclSegment getDecls() {
        return (ASTDeclSegment) children[0];
    }

    public void setDecls(ASTDeclSegment cond) {
        children[0] = cond;
    }

    public ASTBlock getBody() {
        return (ASTBlock) children[1];
    }

    public void setBody(ASTBlock block) {
        children[1] = block;
    }

    @Override
    public String toString() {
        return getDecls() + " in " + getBody();
    }
}
