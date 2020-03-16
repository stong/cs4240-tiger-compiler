package com.gangweedganggang.cs4240.frontend;

public class VoidSymbolType extends SymbolType {
    public static final VoidSymbolType VOID = new VoidSymbolType();

    private VoidSymbolType() {
        super("void");
    }

    @Override
    public String toString() {
        return "void";
    }

    @Override
    public PrimitiveSymbolType getPrimitiveType() {
        throw new UnsupportedOperationException("jump die");
    }
}
