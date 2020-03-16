package com.gangweedganggang.cs4240.ir.stmts;

import com.gangweedganggang.cs4240.ast.TigerBinOp;
import com.gangweedganggang.cs4240.ir.IRBasicBlock;
import com.gangweedganggang.cs4240.ir.IRLocal;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CondBranchStmt extends IRStmt {
    public enum ComparisonOperator {
        EQ,
        NEQ,
        LT,
        GT,
        GEQ,
        LEQ;

        @Override
        public String toString() {
            return name().toLowerCase();
        }

        public static ComparisonOperator valueOf(TigerBinOp op) {
            switch(op) {
            case Eq:
                return EQ;
            case Ne:
                return NEQ;
            case Lt:
                return LT;
            case Gt:
                return GT;
            case Le:
                return LEQ;
            case Ge:
                return GEQ;
            default:
                throw new IllegalArgumentException("oh god think of the CHILDREN!!!!");
            }
        }
    }

    private IRBasicBlock trueBlock;

    private IRBasicBlock falseBlock;
    private IRLocal a, b;

    private ComparisonOperator op;

    public CondBranchStmt(IRBasicBlock trueBlock, IRBasicBlock falseBlock, IRLocal a, IRLocal b, ComparisonOperator op) {
        this.trueBlock = trueBlock;
        this.falseBlock = falseBlock;
        this.a = a;
        this.b = b;
        this.op = op;
        if (!a.getType().getPrimitiveType().equals(b.getType().getPrimitiveType()))
            throw new IllegalArgumentException("incompatible types");
    }

    public IRBasicBlock getTrueBlock() {
        return trueBlock;
    }

    public void setTrueBlock(IRBasicBlock trueBlock) {
        this.trueBlock = trueBlock;
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

    public ComparisonOperator getOp() {
        return op;
    }

    public void setOp(ComparisonOperator op) {
        this.op = op;
    }

    public IRBasicBlock getFalseBlock() {
        return falseBlock;
    }

    public void setFalseBlock(IRBasicBlock falseBlock) {
        this.falseBlock = falseBlock;
    }

    @Override
    public String toString() {
        return "br" + op + ", " + a + ", " + b + ", " + trueBlock;
    }
}
