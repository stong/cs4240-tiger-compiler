package com.gangweedganggang.cs4240.frontend;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RootScope implements IScope {
    private Map<String, ITigerFunction> builtinFuncs;

    public static class Builtins {
        public static TigerExternFunction PRINTI = new TigerExternFunction("printi", new FunctionSymbolType(VoidSymbolType.VOID, Arrays.asList(PrimitiveSymbolType.INT)));
        public static TigerExternFunction PRINTF = new TigerExternFunction("printf", new FunctionSymbolType(VoidSymbolType.VOID, Arrays.asList(PrimitiveSymbolType.FLOAT)));
        public static TigerExternFunction ASSERT = new TigerExternFunction("xassert", new FunctionSymbolType(VoidSymbolType.VOID, Arrays.asList(PrimitiveSymbolType.INT)));
    }

    public RootScope() {
        builtinFuncs = new HashMap<>();
        builtinFuncs.put("printi", Builtins.PRINTI);
        builtinFuncs.put("printf", Builtins.PRINTF);
        builtinFuncs.put("xassert", Builtins.ASSERT);
    }

    public SymbolType resolveType(String name) {
        return PrimitiveSymbolType.valueOf(name);
    }

    @Override
    public IScope getParent() {
        throw new UnsupportedOperationException("the root scope is like batman, because it has no parents.");
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public void addTypedef(TypedefSymbolType typedef) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SymbolType getSymbol(String name) {
        return null;
    }

    @Override
    public TigerVariable lookupVariable(String name) {
        return null;
    }

    @Override
    public ITigerFunction lookupFunction(String name) {
        if (builtinFuncs.containsKey(name))
            return builtinFuncs.get(name);
        return null;
    }

    @Override
    public void addVariable(TigerVariable variable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addFunction(TigerFunction func) {
        throw new UnsupportedOperationException();
    }
}
