package com.gangweedganggang.cs4240.frontend;

import com.gangweedganggang.cs4240.ast.TigerType;

import java.util.Objects;

// builtin primitive type
public class PrimitiveSymbolType extends SymbolType {
    public final TigerType basetype;
    public final boolean isArray;
    public final int arraysize;

    public static final PrimitiveSymbolType FLOAT = new PrimitiveSymbolType("float", TigerType.FLOAT);
    public static final PrimitiveSymbolType INT = new PrimitiveSymbolType("int", TigerType.INT);

    private PrimitiveSymbolType(String name, TigerType basetype) {
        super(name);
        this.basetype = basetype;
        this.isArray = false;
        this.arraysize = 0;
    }

    private PrimitiveSymbolType(String name, TigerType basetype, int arraysize) {
        super(name);
        this.basetype = basetype;
        this.isArray = true;
        this.arraysize = arraysize;
    }

    public PrimitiveSymbolType arrayOfSize(int arraysize) {
        return new PrimitiveSymbolType("array[" + arraysize + "]of" + basetype, basetype, arraysize);
    }

    public PrimitiveSymbolType getArrayType() {
        if (!isArray) throw new UnsupportedOperationException("oh god please think of the children!");
        return valueOf(basetype);
    }

    @Override
    public String toString() {
        String result = basetype.toString();
        if (isArray) {
            result += "[" + arraysize + "]";
        }
        return result;
    }

    @Override
    public PrimitiveSymbolType getPrimitiveType() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (super.equals(o))
            return true;
        if (!(o instanceof PrimitiveSymbolType))
            return false;
        PrimitiveSymbolType t = (PrimitiveSymbolType) o;
        return basetype == t.basetype && isArray == t.isArray && arraysize == t.arraysize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(basetype, isArray, arraysize);
    }

    public static PrimitiveSymbolType valueOf(TigerType tigerType) {
        switch (tigerType) {
        case FLOAT:
            return FLOAT;
        case INT:
            return INT;
        default:
            throw new UnsupportedOperationException("oh god please think of the children!");
        }
    }

    public static PrimitiveSymbolType valueOf(String name) {
        if (name.startsWith("array[")) {
            String lol = name.split("\\[")[1];
            String[] lols = lol.split("]");
            int size = Integer.parseInt(lols[0]);
            String basename = lols[1].substring(2);
            return new PrimitiveSymbolType(name, valueOf(basename).basetype, size);
        }
        switch(name) {
        case "float":
            return PrimitiveSymbolType.FLOAT;
        case "int":
            return PrimitiveSymbolType.INT;
        default:
            return null;
        }
    }
}
