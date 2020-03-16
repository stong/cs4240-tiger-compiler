package com.gangweedganggang.cs4240.ast;

import java.util.List;

public class ASTVarDeclList extends AbstractASTNode {
    public ASTVarDeclList(int line, int col, List<ASTVarDecl> decls) {
        super(line, col, decls.size());
        decls.toArray(children);
    }

    public AbstractASTNode[] getDecls() {
        return (AbstractASTNode[])children;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (AbstractASTNode stmt : children)
            sb.append(stmt).append('\n');
        return sb.toString();
    }
}
