package com.gangweedganggang.cs4240.ir.stmts;

import com.gangweedganggang.cs4240.ir.IRBasicBlock;
import com.gangweedganggang.cs4240.ir.IRLocal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GotoStmt extends IRStmt {
    private IRBasicBlock dst;

    public GotoStmt(IRBasicBlock dst) {
        this.dst = dst;
    }

    public IRBasicBlock getDst() {
        return dst;
    }

    public void setDst(IRBasicBlock dst) {
        this.dst = dst;
    }

    @Override
    public String toString() {
        return "goto " + dst;
    }
}
