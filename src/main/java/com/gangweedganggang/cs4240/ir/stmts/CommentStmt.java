package com.gangweedganggang.cs4240.ir.stmts;

public class CommentStmt extends IRStmt {
    private final String comment;

    public CommentStmt(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return ";" + comment;
    }
}
