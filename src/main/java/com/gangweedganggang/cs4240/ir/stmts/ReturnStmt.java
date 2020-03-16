package com.gangweedganggang.cs4240.ir.stmts;

import com.gangweedganggang.cs4240.ir.IRLocal;

import java.util.Collections;
import java.util.Set;

public class ReturnStmt extends IRStmt {
    private IRLocal ret;

    public ReturnStmt(IRLocal ret) {
        this.ret = ret;
    }

    public IRLocal getRet() {
        return ret;
    }

    public void setRet(IRLocal ret) {
        this.ret = ret;
    }

    @Override
    public String toString() {
        return "return " + ret;
    }
}
