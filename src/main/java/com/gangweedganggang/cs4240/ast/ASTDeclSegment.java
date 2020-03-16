package com.gangweedganggang.cs4240.ast;

import org.antlr.v4.runtime.tree.TerminalNode;

public class ASTDeclSegment extends AbstractASTStmt {
    public ASTTypeDeclList typeDecls;
    public ASTVarDeclList varDecls;
    public ASTFuncDeclList funcDecls;

    public ASTDeclSegment(int line, int col, ASTTypeDeclList typeDecls, ASTVarDeclList varDecls, ASTFuncDeclList funcDecls) {
        super(line, col, 3);
        setTypeDecls(typeDecls);
        setVarDecls(varDecls);
        setFuncDecls(funcDecls);
    }

    public ASTDeclSegment(TerminalNode t, ASTTypeDeclList typeDecls, ASTVarDeclList varDecls, ASTFuncDeclList funcDecls) {
        super(t.getSymbol().getLine(), t.getSymbol().getCharPositionInLine(), 3);
        setTypeDecls(typeDecls);
        setVarDecls(varDecls);
        setFuncDecls(funcDecls);
    }

    public ASTTypeDeclList getTypeDecls() {
        return (ASTTypeDeclList) children[0];
    }

    public void setTypeDecls(ASTTypeDeclList cond) {
        children[0] = cond;
    }

    public ASTVarDeclList getVarDecls() {
        return (ASTVarDeclList) children[1];
    }

    public void setVarDecls(ASTVarDeclList block) {
        children[1] = block;
    }

    public ASTFuncDeclList getFuncDecls() {
        return (ASTFuncDeclList) children[2];
    }

    public void setFuncDecls(ASTFuncDeclList block) {
        children[2] = block;
    }

    @Override
    public String toString() {
        return getTypeDecls() + "\n" + getVarDecls() + "\n" + getFuncDecls();
    }
}
