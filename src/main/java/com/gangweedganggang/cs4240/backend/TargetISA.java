package com.gangweedganggang.cs4240.backend;

import com.gangweedganggang.cs4240.flowgraph.BasicBlock;
import com.gangweedganggang.cs4240.flowgraph.ControlFlowGraph;
import com.gangweedganggang.cs4240.frontend.SymbolType;
import com.gangweedganggang.cs4240.ir.stmts.CondBranchStmt;
import com.gangweedganggang.cs4240.stdlib.collections.list.JoinedUnmodifiableList;

import java.util.List;

public interface TargetISA<Reg extends TargetReg, Insn extends TargetInsn<Reg>,
        BB extends BasicBlock<Insn>, CFG extends ControlFlowGraph<BB>,
        Func extends TargetFunction<Reg, Insn, BB, CFG, Func, ISA>,
        ISA extends TargetISA<Reg, Insn, BB, CFG, Func, ISA>> {

    default ToolsIntegration getToolsIntegration() {
        return new ToolsIntegration() {};
    }

    Func newFunction(String name, int paramCount);

    int getWordSize();

    Reg getSp();

    Reg getFp();

    Reg getGp();

    default List<Insn> push(Reg src) {
        return new JoinedUnmodifiableList<>(
            addi(getSp(), getSp(), -getWordSize()),
            store(src, getSp(), 0)
        );
    }

    default List<Insn> pop(Reg dst) {
        return new JoinedUnmodifiableList<>(
            load(dst, getSp(), 0),
            addi(getSp(), getSp(), getWordSize())
        );
    }

    List<Insn> load(Reg dst, Reg src, int offset);

    List<Insn> store(Reg src, Reg dst, int offset);

    List<Insn> addi(Reg dst, Reg src, int offset);

    List<Insn> li(Reg dst, int immediate);

    List<Insn> move(Reg dst, Reg src);

    List<Insn> jmp(BB dst);

    List<Insn> jmpReg(Reg dst);

    List<Insn> condBr(BB dst, Reg a, Reg b, CondBranchStmt.ComparisonOperator op);
    List<Insn> fcondBr(BB dst, Reg a, Reg b, CondBranchStmt.ComparisonOperator op);

    List<Insn> iadd(Reg dst, Reg src1, Reg src2);
    List<Insn> isub(Reg dst, Reg src1, Reg src2);
    List<Insn> imul(Reg dst, Reg src1, Reg src2);
    List<Insn> idiv(Reg dst, Reg src1, Reg src2);

    List<Insn> fadd(Reg dst, Reg src1, Reg src2);
    List<Insn> fsub(Reg dst, Reg src1, Reg src2);
    List<Insn> fmul(Reg dst, Reg src1, Reg src2);
    List<Insn> fdiv(Reg dst, Reg src1, Reg src2);

    List<Insn> and(Reg dst, Reg src1, Reg src2);
    List<Insn> or(Reg dst, Reg src1, Reg src2);

    List<Insn> rcall(Func func, Reg dst, List<Reg> srcs);
    List<Insn> call(Func func, List<Reg> srcs);
    Reg returnValueReg();

    boolean isCall(Insn i); // can control flow leave the function?

    int getTypeSize(SymbolType type);

    int floatToIntBits(float f);

    List<Insn> cvtf2i(Reg dst, Reg src);
    List<Insn> cvti2f(Reg dst, Reg src);

    Insn comment(String text);
}
