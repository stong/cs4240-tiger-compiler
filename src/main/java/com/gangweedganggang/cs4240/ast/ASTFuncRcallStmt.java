package com.gangweedganggang.cs4240.ast;

import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

public class ASTFuncRcallStmt extends AbstractASTStmt {
    public String funcID;
    public List<ASTExpr> args;

    public ASTFuncRcallStmt(TerminalNode t, TerminalNode lvalue, String funcID, List<ASTExpr> args) {
        super(t, 1 + args.size());
        this.funcID = funcID;
        setLvalue(new ASTLvalue(
                lvalue.getSymbol().getLine(),
                lvalue.getSymbol().getCharPositionInLine(),
                lvalue.getText(),
                null
        ));
        for (int i = 0; i < args.size(); i++)
            children[i+1] = args.get(i);
        this.args = args;
    }

    public void setLvalue(ASTLvalue lvalue) {
        children[0] = lvalue;
    }

    public ASTLvalue getLvalue() {
        return (ASTLvalue) children[0];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append(getLvalue()).append(" := ").append(funcID).append("( ");
        for (int i = 1 ; i < children.length; i++) {
            sb.append(children[i]);
            if (i < children.length - 1) sb.append(",");
        }
        sb.append(")");
        return sb.toString();
    }
}
