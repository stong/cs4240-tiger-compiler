package com.gangweedganggang.cs4240.ir.stmts;

import com.gangweedganggang.cs4240.ir.IRLocal;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ArrayStoreStmt extends IRStmt {
    private IRLocal array, index, rhs;

    public ArrayStoreStmt(IRLocal array, IRLocal index, IRLocal rhs) {
        this.array = array;
        this.index = index;
        this.rhs = rhs;
        if (!array.getType().getPrimitiveType().basetype.equals(rhs.getType().getPrimitiveType().basetype))
            throw new IllegalArgumentException("incompatible types");
    }

    public IRLocal getArray() {
        return array;
    }

    public void setArray(IRLocal array) {
        this.array = array;
    }

    public IRLocal getIndex() {
        return index;
    }

    public void setIndex(IRLocal a) {
        this.index = a;
    }

    public IRLocal getRhs() {
        return rhs;
    }

    public void setRhs(IRLocal b) {
        this.rhs = b;
    }

    @Override
    public String toString() {
        return "store " + array  + ", " + index + ", " + rhs;
    }
}
