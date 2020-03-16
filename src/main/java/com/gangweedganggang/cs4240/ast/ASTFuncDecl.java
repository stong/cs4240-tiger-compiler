package com.gangweedganggang.cs4240.ast;

import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

public class ASTFuncDecl extends AbstractASTNode {
    public String retType;
    public String name;
    public List<String> params;
    public List<String> paramTypes;

    public ASTFuncDecl(int line, int col, String retType, String name, List<String> params, List<String> paramTypes, ASTBlock body) {
        super(line, col, 1);
        this.name = name;
        this.params = params;
        this.paramTypes = paramTypes;
        this.retType = retType;
        setBody(body);
    }

    public ASTFuncDecl(TerminalNode t, String retType, String name, List<String> params, List<String> paramTypes, ASTBlock body) {
        super(t, 1);
        this.name = name;
        this.params = params;
        this.paramTypes = paramTypes;
        this.retType = retType;
        setBody(body);
    }

    public ASTBlock getBody() {
        return (ASTBlock) children[0];
    }

    public void setBody(ASTBlock block) {
        children[0] = block;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(retType).append(' ').append(name).append('(');
        for (int i = 0; i < params.size(); i++) {
            sb.append(paramTypes.get(i)).append(' ').append(params.get(i));
        }
        sb.append(") ").append(getBody());
        return sb.toString();
    }
}
