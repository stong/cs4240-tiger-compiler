package com.gangweedganggang.cs4240.flowgraph;

import java.util.List;
import java.util.Set;

// Base abstract IR statement class.
public abstract class Stmt<L extends Local> {
    private BasicBlock block;
    private final List<L> operands;

    public Stmt(List<L> operands) {
        this.operands = operands;
    }

    public L getOperand(int i) {
        return operands.get(i);
    }

    public void setOperand(int i, L l) {
        operands.set(i, l);
    }

    public BasicBlock getBlock() {
        return block;
    }

    public void setBlock(BasicBlock b) {
        block = b;
    }

    public abstract Set<Integer> defOperands();

    public abstract Set<Integer> useOperands();

    public Iterable<L> defs() {
        return defOperands().stream().map(this::getOperand)::iterator;
    }

    public Iterable<L> uses() {
        return useOperands().stream().map(this::getOperand)::iterator;
    }
}
