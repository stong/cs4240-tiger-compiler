package com.gangweedganggang.cs4240.ir.stmts;

import com.gangweedganggang.cs4240.ir.IRLocal;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ArrayLoadStmt extends IRStmt {
    private IRLocal lhs, array, index;

    public ArrayLoadStmt(IRLocal lhs, IRLocal array, IRLocal index) {
        this.lhs = lhs;
        this.array = array;
        this.index = index;
        if (!array.getType().getPrimitiveType().basetype.equals(lhs.getType().getPrimitiveType().basetype))
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

    public IRLocal getLhs() {
        return lhs;
    }

    public void setLhs(IRLocal b) {
        this.lhs = b;
    }

    @Override
    public String toString() {
        return "load " + lhs + ", " + array  + ", " + index;
    }
}
