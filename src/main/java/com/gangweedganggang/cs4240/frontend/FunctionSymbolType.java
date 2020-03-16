package com.gangweedganggang.cs4240.frontend;

import java.util.List;

public class FunctionSymbolType extends SymbolType {
    public final List<SymbolType> paramTypes;
    public final SymbolType retType;

    public FunctionSymbolType(SymbolType retType, List<SymbolType> paramTypes) {
        super("func()");
        this.paramTypes = paramTypes;
        this.retType = retType;
    }

    @Override
    public PrimitiveSymbolType getPrimitiveType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "(" + retType.toString() + ")(" + paramTypes + ")";
    }
}
