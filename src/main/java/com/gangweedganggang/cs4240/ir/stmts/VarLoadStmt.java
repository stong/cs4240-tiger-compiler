package com.gangweedganggang.cs4240.ir.stmts;

import com.gangweedganggang.cs4240.frontend.TigerVariable;
import com.gangweedganggang.cs4240.ir.IRLocal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// Copy from global variable, 'cause this language is actually stupid af and you can nest functions (LOL?????)
public class VarLoadStmt extends IRStmt {
    private IRLocal lhs;
    private TigerVariable variable;

    public VarLoadStmt(IRLocal lhs, TigerVariable variable) {
        this.lhs = lhs;
        this.variable = variable;
        if (!lhs.getType().getPrimitiveType().equals(variable.type.getPrimitiveType()))
            throw new IllegalArgumentException("incompatible types");
    }

    public IRLocal getLhs() {
        return lhs;
    }

    public void setLhs(IRLocal lhs) {
        this.lhs = lhs;
    }

    public TigerVariable getVariable() {
        return variable;
    }

    public void setVariable(TigerVariable variable) {
        this.variable = variable;
    }

    @Override
    public String toString() {
        return "assign " + lhs + ", " + variable;
    }
}
