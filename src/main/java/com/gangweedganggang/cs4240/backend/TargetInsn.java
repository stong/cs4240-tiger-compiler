package com.gangweedganggang.cs4240.backend;

import com.gangweedganggang.cs4240.flowgraph.Stmt;
import com.gangweedganggang.cs4240.stdlib.collections.itertools.FilterIterable;
import com.gangweedganggang.cs4240.stdlib.functional.Predicates;

import java.util.List;

public abstract class TargetInsn<L extends TargetReg> extends Stmt<L> {
    public TargetInsn(List<L> operands) {
        super(operands);
    }

    public abstract String emit();

    @Override
    public String toString() {
        return emit();
    }

    @Override
   public Iterable<L> defs() {
        return new FilterIterable<>(super.defs(), Predicates.not(L::isZeroRegister));
    }

    public Iterable<L> uses() {
        return new FilterIterable<>(super.uses(), Predicates.not(L::isZeroRegister));
    }
}
