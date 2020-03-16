package com.gangweedganggang.cs4240.ir.stmts;

import com.gangweedganggang.cs4240.ir.IRLocal;

import java.util.Collections;
import java.util.Set;

public class CastStmt extends IRStmt {
    private IRLocal lhs, rhs;

    public CastStmt(IRLocal lhs, IRLocal rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public IRLocal getLhs() {
        return lhs;
    }

    public void setLhs(IRLocal lhs) {
        this.lhs = lhs;
    }

    public IRLocal getRhs() {
        return rhs;
    }

    public void setRhs(IRLocal rhs) {
        this.rhs = rhs;
    }

    @Override
    public String toString() {
        return "cast-assign " + lhs + ", " + rhs;
    }
}
