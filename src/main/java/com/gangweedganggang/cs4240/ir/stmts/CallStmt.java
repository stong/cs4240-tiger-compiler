package com.gangweedganggang.cs4240.ir.stmts;

import com.gangweedganggang.cs4240.frontend.ITigerFunction;
import com.gangweedganggang.cs4240.ir.IRLocal;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CallStmt extends IRStmt {
    protected ITigerFunction func;
    protected final List<IRLocal> args;

    public CallStmt(ITigerFunction func, List<IRLocal> args) {
        this.func = func;
        this.args = args;
    }

    public List<IRLocal> getArgs() {
        return args;
    }

   public ITigerFunction getFunc() {
        return func;
    }

    public void setFunc(ITigerFunction func) {
        this.func = func;
    }

    @Override
    public String toString() {
        return "call " + func + ", " + args;
    }
}
