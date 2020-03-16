package com.gangweedganggang.cs4240.ir.stmts;

import com.gangweedganggang.cs4240.frontend.TigerVariable;
import com.gangweedganggang.cs4240.ir.IRLocal;

import java.util.Collections;
import java.util.Set;

// Copy to global variable, 'cause this language is actually stupid af and you can nest functions (LOL?????)
public class VarStoreStmt extends IRStmt {
    private TigerVariable variable;
    private IRLocal rhs;

    public VarStoreStmt(TigerVariable variable, IRLocal rhs) {
        this.rhs = rhs;
        this.variable = variable;
        if (!rhs.getType().getPrimitiveType().equals(variable.type.getPrimitiveType()))
            throw new IllegalArgumentException("incompatible types");
    }

    public IRLocal getRhs() {
        return rhs;
    }

    public void setRhs(IRLocal rhs) {
        this.rhs = rhs;
    }

    public TigerVariable getVariable() {
        return variable;
    }

    public void setVariable(TigerVariable variable) {
        this.variable = variable;
    }

    @Override
    public String toString() {
        return "assign " + variable + ", " + rhs;
    }
}
