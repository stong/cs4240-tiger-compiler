package com.gangweedganggang.cs4240.frontend;

public class TigerVariable {
    public enum StorageClass {
        GLOBAL, LOCAL
    }

    public final IScope parent;
    public final String name;
    public final SymbolType type;
    public final StorageClass storage;

    public TigerVariable(IScope parent, String name, SymbolType type, StorageClass storage) {
        this.parent = parent;
        this.name = name;
        this.type = type;
        this.storage = storage;
    }

    @Override
    public String toString() {
        return type.toString() + " " + parent.getID() + "$" + name;
    }
}
