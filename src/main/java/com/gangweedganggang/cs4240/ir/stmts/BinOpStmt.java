package com.gangweedganggang.cs4240.ir.stmts;

import com.gangweedganggang.cs4240.ast.TigerBinOp;
import com.gangweedganggang.cs4240.frontend.PrimitiveSymbolType;
import com.gangweedganggang.cs4240.ir.IRLocal;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BinOpStmt extends IRStmt {
    public enum BinaryOperator {
        ADD,
        SUB,
        MUL,
        DIV,
        AND,
        OR,
        POW;

        @Override
        public String toString() {
            return name().toLowerCase();
        }

        public static BinaryOperator valueOf(TigerBinOp op) {
            switch(op) {
            case Add:
                return ADD;
            case Sub:
                return SUB;
            case Mul:
                return MUL;
            case Div:
                return DIV;
            case Pow:
                return POW;
            case And:
                return AND;
            case Or:
                return OR;
            default:
                throw new IllegalArgumentException("oh god think of the CHILDREN!!!!");
            }
        }
    }

    private IRLocal dst, a, b;
    private BinaryOperator op;

    public BinOpStmt(IRLocal dst, IRLocal a, IRLocal b, BinaryOperator op) {
        this.dst = dst;
        this.a = a;
        this.b = b;
        this.op = op;
        if (op == BinaryOperator.POW) {
            if (!b.getType().getPrimitiveType().equals(PrimitiveSymbolType.INT))
                throw new IllegalArgumentException("incompatible types");
        } else if (!dst.getType().getPrimitiveType().equals(a.getType().getPrimitiveType()) || !dst.getType().getPrimitiveType().equals(b.getType().getPrimitiveType()))
            throw new IllegalArgumentException("incompatible types");
    }

    public IRLocal getDst() {
        return dst;
    }

    public void setDst(IRLocal dst) {
        this.dst = dst;
    }

    public IRLocal getLeftOperand() {
        return a;
    }

    public void setLeftOperand(IRLocal a) {
        this.a = a;
    }

    public IRLocal getRightOperand() {
        return b;
    }

    public void setRightOperand(IRLocal b) {
        this.b = b;
    }

    public BinaryOperator getOp() {
        return op;
    }

    public void setOp(BinaryOperator op) {
        this.op = op;
    }

    @Override
    public String toString() {
        return "" + op  + " " + a + ", " + b + ", " + dst;
    }
}
