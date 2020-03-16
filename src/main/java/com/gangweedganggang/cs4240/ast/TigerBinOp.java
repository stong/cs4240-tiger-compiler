package com.gangweedganggang.cs4240.ast;

public enum TigerBinOp {
    Add, Sub, Mul, Div, Pow,
    Eq, Ne, Lt, Gt, Le, Ge,
    And, Or;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    public boolean isComparison() {
        switch (this) {
        case Eq:
        case Ne:
        case Lt:
        case Gt:
        case Le:
        case Ge:
            return true;
        default:
            return false;
        }
    }
}
