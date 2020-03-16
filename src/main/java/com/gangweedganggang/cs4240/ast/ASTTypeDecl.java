package com.gangweedganggang.cs4240.ast;

import org.antlr.v4.runtime.tree.TerminalNode;

public class ASTTypeDecl extends AbstractASTNode {
    public String identifier;
    public String definition;
    public TigerType basetype;
    public int arraysize;
    public boolean isArray;

    public ASTTypeDecl(int line, int col, String identifier, String definition) {
        super(line, col, 0);
        this.identifier = identifier;
        this.definition = definition;
        isArray = false;
    }
    public ASTTypeDecl(TerminalNode t, String identifier, String definition) {
        super(t.getSymbol().getLine(), t.getSymbol().getCharPositionInLine(), 0);
        this.identifier = identifier;
        this.definition = definition;
        isArray = false;
    }

    public ASTTypeDecl(int line, int col, String identifier, TigerType basetype) {
        super(line, col, 0);
        this.identifier = identifier;
        this.basetype = basetype;
        isArray = false;
    }
    public ASTTypeDecl(TerminalNode t, String identifier, TigerType basetype) {
        super(t.getSymbol().getLine(), t.getSymbol().getCharPositionInLine(), 0);
        this.identifier = identifier;
        this.basetype = basetype;
        isArray = false;
    }

    public ASTTypeDecl(int line, int col, String identifier, TigerType basetype, int arraysize) {
        super(line, col, 0);
        this.identifier = identifier;
        this.basetype = basetype;
        this.arraysize = arraysize;
        isArray = true;
    }

    public ASTTypeDecl(TerminalNode t, String identifier, TigerType basetype, int arraysize) {
        super(t.getSymbol().getLine(), t.getSymbol().getCharPositionInLine(), 0);
        this.identifier = identifier;
        this.basetype = basetype;
        this.arraysize = arraysize;
        isArray = true;
    }
}
