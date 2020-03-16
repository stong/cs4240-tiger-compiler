package com.gangweedganggang.cs4240.frontend;

public interface IScope {
    void addVariable(TigerVariable variable);

    void addFunction(TigerFunction func);

    void addTypedef(TypedefSymbolType definition);

    // Lookup symbol in CURRENT scope
    SymbolType getSymbol(String name);

    // lookup symbol in current AND PARENT scopes
    TigerVariable lookupVariable(String name);

    ITigerFunction lookupFunction(String name);

    // return SymbolType given name of the type.
    SymbolType resolveType(String name);

    IScope getParent();

    int getID();
}
