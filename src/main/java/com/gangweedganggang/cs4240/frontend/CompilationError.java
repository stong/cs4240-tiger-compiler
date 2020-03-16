package com.gangweedganggang.cs4240.frontend;

public class CompilationError extends RuntimeException {
    public final int row, column;

    public CompilationError(int row, int column, String message) {
        super(message);
        this.row = row;
        this.column = column;
    }
}
