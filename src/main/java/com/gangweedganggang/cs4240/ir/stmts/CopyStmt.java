package com.gangweedganggang.cs4240.ir.stmts;

import com.gangweedganggang.cs4240.ir.IRLocal;

import java.util.Collections;
import java.util.Set;

public class CopyStmt extends IRStmt {
    private IRLocal lhs, rhs;

    public CopyStmt(IRLocal lhs, IRLocal rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
        if (!lhs.getType().getPrimitiveType().equals(rhs.getType().getPrimitiveType()))
            throw new IllegalArgumentException("incompatible types");
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
        return "assign " + lhs + ", " + rhs;
    }
}
