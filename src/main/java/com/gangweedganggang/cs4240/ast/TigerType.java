package com.gangweedganggang.cs4240.ast;

public enum TigerType {
    INT, FLOAT, VOID;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
