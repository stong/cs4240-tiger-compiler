package com.gangweedganggang.cs4240.ast;

public class ASTLvalue extends AbstractASTNode {
    public String getIdentifier() {
        return lValueID;
    }

    String lValueID;

    public ASTLvalue(int line, int col, String lValueID, ASTExpr index) {
        super(line, col, 1);
        this.lValueID = lValueID;
        setIndex(index);
    }

    public void setIndex(ASTExpr index) {
        children[0] = index;
    }

    public ASTExpr getIndex() {
        return (ASTExpr) children[0];
    }


    @Override
    public String toString() {
        if (getIndex() != null) {
            return lValueID + "[" + getIndex() + "]";
        }
        return lValueID;
    }
}
