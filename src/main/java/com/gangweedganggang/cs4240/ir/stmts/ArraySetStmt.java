package com.gangweedganggang.cs4240.ir.stmts;

import com.gangweedganggang.cs4240.ir.IRLocal;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// memset ?????!!!!!! WTF IS THIS LANGUAGE!
public class ArraySetStmt extends IRStmt {
    private IRLocal array;
    private int size;
    private IRLocal value;

    public ArraySetStmt(IRLocal array, int size, IRLocal value) {
        this.array = array;
        this.size = size;
        this.value = value;
        if (!array.getType().getPrimitiveType().basetype.equals(value.getType().getPrimitiveType().basetype))
            throw new IllegalArgumentException("incompatible types");
    }

    public IRLocal getArray() {
        return array;
    }

    public void setArray(IRLocal array) {
        this.array = array;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int a) {
        this.size = a;
    }

    public IRLocal getValue() {
        return value;
    }

    public void setValue(IRLocal b) {
        this.value = b;
    }

    @Override
    public String toString() {
        return "assign " + array  + ", " + size + ", " + value;
    }
}
