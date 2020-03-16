package com.gangweedganggang.cs4240.ast;

import org.antlr.v4.runtime.tree.TerminalNode;

public abstract class AbstractASTNode {
    public final int row, col;
    protected final AbstractASTNode[] children;

    protected AbstractASTNode(int row, int col, int n) {
        this.row = row;
        this.col = col;
        children = new AbstractASTNode[n];
    }

    protected AbstractASTNode(TerminalNode node, int n) {
        this(node.getSymbol().getLine(), node.getSymbol().getCharPositionInLine(), n);
    }
}
