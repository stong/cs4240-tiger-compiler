package com.gangweedganggang.cs4240.backend;

import com.gangweedganggang.cs4240.frontend.PrimitiveSymbolType;
import com.gangweedganggang.cs4240.frontend.SymbolType;

public class RegType extends SymbolType {
    private RegType(String name) {
        super(name);
    }

    @Override
    public PrimitiveSymbolType getPrimitiveType() {
        throw new UnsupportedOperationException("jump die");
    }

    public static final RegType INT = new RegType("int");
    public static final RegType FLOAT = new RegType("float");
    public static final RegType SPECIAL = new RegType("special");
}
