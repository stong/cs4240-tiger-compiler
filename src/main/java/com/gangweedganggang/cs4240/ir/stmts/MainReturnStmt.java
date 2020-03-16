package com.gangweedganggang.cs4240.ir.stmts;

import com.gangweedganggang.cs4240.ir.IRLocal;

import java.util.Collections;
import java.util.Set;

// Return void from main
public class MainReturnStmt extends IRStmt {
    public MainReturnStmt() {
    }

    @Override
    public String toString() {
        return "return";
    }
}
