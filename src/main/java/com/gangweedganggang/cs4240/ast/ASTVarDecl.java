package com.gangweedganggang.cs4240.ast;

import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

public class ASTVarDecl extends AbstractASTNode {
    public List<String> identifiers;
    public String type;

    public ASTVarDecl(int line, int col, List<String> identifiers, String type, ASTConstExpr optionalInit) {
        super(line, col, 0);
        this.identifiers = identifiers;
        this.type = type;
        if (optionalInit != null)
            setInitializer(optionalInit);
    }

    public ASTVarDecl(TerminalNode t, List<String> identifiers, String type, ASTConstExpr optionalInit) {
        super(t, 1);
        this.identifiers = identifiers;
        this.type = type;
        if (optionalInit != null)
            setInitializer(optionalInit);
    }

    public ASTConstExpr getInitializer() {
        return (ASTConstExpr) children[0];
    }

    public void setInitializer(ASTConstExpr initial) {
        children[0] = initial;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type).append(' ');
        for (String s : identifiers) sb.append(s).append(", ");
        if (getInitializer() != null)
            sb.append(" = ").append(getInitializer());
        sb.append(';');
        return sb.toString();
    }
}
