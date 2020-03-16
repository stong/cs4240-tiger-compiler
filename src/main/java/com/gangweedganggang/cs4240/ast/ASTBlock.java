package com.gangweedganggang.cs4240.ast;

import java.util.List;

public class ASTBlock extends AbstractASTStmt {
    public ASTBlock(int line, int col, List<AbstractASTStmt> blockContent) {
        super(line, col, blockContent.size());
        blockContent.toArray(children);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{\n");
        for (AbstractASTNode stmt : children)
            sb.append(stmt).append('\n');
        return sb.append("}").toString();
    }

    public AbstractASTNode[] getStmts() {
        return children;
    }
}
