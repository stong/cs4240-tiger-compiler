package com.gangweedganggang.cs4240.ast;

import org.antlr.v4.runtime.tree.TerminalNode;

public class ASTRoot extends AbstractASTNode {
    public ASTRoot(TerminalNode t, ASTDeclSegment decls, ASTBlock mainBody) {
        super(t.getSymbol().getLine(), t.getSymbol().getCharPositionInLine(),1);
        setRoot(new ASTLetStmt(t, decls, mainBody));
    }

    public void setRoot(ASTLetStmt root) {
        children[0] = root;
    }

    public ASTLetStmt getRoot() {
        return (ASTLetStmt) children[0];
    }

    @Override
    public String toString() {
        return "main " + getRoot();
    }
}
