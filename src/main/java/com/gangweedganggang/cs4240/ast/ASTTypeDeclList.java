package com.gangweedganggang.cs4240.ast;

import java.util.List;

public class ASTTypeDeclList extends AbstractASTNode {
    public ASTTypeDeclList(int line, int col, List<ASTTypeDecl> decls) {
        super(line, col, decls.size());
        decls.toArray(children);
    }

    public AbstractASTNode[] getDecls() {
        return children;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (AbstractASTNode stmt : children)
            sb.append(stmt).append('\n');
        return sb.toString();
    }
}
