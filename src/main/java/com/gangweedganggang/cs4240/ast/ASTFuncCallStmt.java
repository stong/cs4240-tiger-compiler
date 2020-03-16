package com.gangweedganggang.cs4240.ast;

import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

public class ASTFuncCallStmt extends AbstractASTStmt {
    public String funcID;
    public List<ASTExpr> args;

    public ASTFuncCallStmt(TerminalNode t, String funcID, List<ASTExpr> args) {
        super(t, 1 + args.size());
        this.funcID = funcID;
        for (int i = 0; i < args.size(); i++)
            children[i+1] = args.get(i);
        this.args = args;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append(funcID).append("( ");
        for (int i = 1 ; i < children.length; i++) {
            sb.append(children[i]);
            if (i < children.length - 1) sb.append(",");
        }
        sb.append(")");
        return sb.toString();
    }
}
