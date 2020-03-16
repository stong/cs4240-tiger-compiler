package com.gangweedganggang.cs4240.ir.stmts;

import com.gangweedganggang.cs4240.frontend.TigerVariable;
import com.gangweedganggang.cs4240.ir.IRLocal;

// Synthetic parameter copy statement
public class ParamStmt extends IRStmt {
    private IRLocal lhs;
    private TigerVariable param;
    private int index;

    public ParamStmt(IRLocal lhs, TigerVariable param, int i) {
        this.lhs = lhs;
        this.param = param;
        this.index = i;
    }

    public IRLocal getLhs() {
        return lhs;
    }

    public void setLhs(IRLocal lhs) {
        this.lhs = lhs;
    }

    public TigerVariable getParam() {
        return param;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "assign " + lhs + ", " + param;
    }
}
