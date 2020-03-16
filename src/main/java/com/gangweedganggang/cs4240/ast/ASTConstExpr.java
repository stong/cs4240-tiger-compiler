package com.gangweedganggang.cs4240.ast;

public class ASTConstExpr extends ASTExpr {

    Object value;

    public ASTConstExpr(int line, int col, String token, TigerType type) {
        super(line, col, 0);
        this.type = type;
        switch (type) {
            case INT:
                value = Integer.parseInt(token);
                break;
            case FLOAT:
                value = Float.parseFloat(token);
                break;
        }
    }

    public int getAsInt() {
        return (int)value;
    }

    public float getAsFloat() {
        return (float)value;
    }

    public Object get() {
        return value;
    }

    @Override
    public String toString() {
        switch (this.type) {
            case INT:
                return getAsInt() + "i";
            case FLOAT:
                return getAsFloat() + "f";
        }
        throw new IllegalStateException("Invalid type must've been hit");
    }
}
