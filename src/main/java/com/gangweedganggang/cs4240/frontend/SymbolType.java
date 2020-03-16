package com.gangweedganggang.cs4240.frontend;

public abstract class SymbolType {
    public final String name;

    public SymbolType(String name) {
        this.name = name;
    }

    public abstract PrimitiveSymbolType getPrimitiveType();

    // bad language gets a bad compiler.
    @Override
    public boolean equals(Object other) {
        return this == other;
    }
}
