package com.gangweedganggang.cs4240.frontend;

public class TypedefSymbolType extends SymbolType {
    public final SymbolType basetype;

    public TypedefSymbolType(String name, SymbolType basetype) {
        super(name);
        this.basetype = basetype;
    }

    @Override
    public PrimitiveSymbolType getPrimitiveType() {
        return basetype.getPrimitiveType();
    }

    @Override
    public String toString() {
        return name + "->" + basetype.toString();
    }
}
