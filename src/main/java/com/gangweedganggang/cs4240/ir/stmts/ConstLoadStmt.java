package com.gangweedganggang.cs4240.ir.stmts;

import com.gangweedganggang.cs4240.ir.IRLocal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// Load constant
public class ConstLoadStmt extends IRStmt {
    private IRLocal lhs;
    private Object constant;

    public ConstLoadStmt(IRLocal lhs, Object constant) {
        this.lhs = lhs;
        this.constant = constant;
    }

    public IRLocal getLhs() {
        return lhs;
    }

    public void setLhs(IRLocal lhs) {
        this.lhs = lhs;
    }

    public Object getConstant() {
        return constant;
    }

    public void setConstant(Object constant) {
        this.constant = constant;
    }

    @Override
    public String toString() {
        return "assign " + lhs + ", " + constant;
    }
}
