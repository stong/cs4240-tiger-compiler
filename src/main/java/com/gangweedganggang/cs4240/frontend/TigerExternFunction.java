package com.gangweedganggang.cs4240.frontend;

public class TigerExternFunction implements ITigerFunction {
    public final String name;
    public final FunctionSymbolType type;

    public TigerExternFunction(String name, FunctionSymbolType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public FunctionSymbolType getType() {
        return type;
    }

    @Override
    public String toString() {
        return name;
    }
}
