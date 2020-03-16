package com.gangweedganggang.cs4240.ast;

import java.util.List;

public class ASTFuncDeclList extends AbstractASTNode {
    public ASTFuncDeclList(int line, int col, List<ASTFuncDecl> decls) {
        super(line, col, decls.size());
        decls.toArray(children);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (AbstractASTNode stmt : children)
            sb.append(stmt).append('\n');
        return sb.toString();
    }

    public AbstractASTNode[] getDecls() {
        return children;
    }
}
