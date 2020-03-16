package com.gangweedganggang.cs4240.backend.mips32;

import com.gangweedganggang.cs4240.backend.TargetISA;
import com.gangweedganggang.cs4240.backend.ToolsIntegration;
import com.gangweedganggang.cs4240.backend.mips32.insn.*;
import com.gangweedganggang.cs4240.frontend.PrimitiveSymbolType;
import com.gangweedganggang.cs4240.frontend.SymbolType;
import com.gangweedganggang.cs4240.ir.stmts.CondBranchStmt;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.gangweedganggang.cs4240.backend.mips32.MIPS32Reg.*;

public class MIPS32Target implements TargetISA<MIPS32Reg, MIPS32Insn, MIPS32BasicBlock, MIPS32ControlFlowGraph, MIPS32Function, MIPS32Target> {
    @Override
    public MIPS32Function newFunction(String name, int paramCount) {
        return new MIPS32Function(this, name, paramCount);
    }

    @Override
    public int getWordSize() {
        return 4;
    }

    @Override
    public MIPS32Reg getSp() {
        return SP;
    }

    @Override
    public MIPS32Reg getFp() {
        return FP;
    }

    @Override
    public MIPS32Reg getGp() {
        return GP;
    }

    @Override
    public List<MIPS32Insn> load(MIPS32Reg dst, MIPS32Reg src, int offset) {
        return Collections.singletonList(load1(dst, src, offset));
    }

    private LoadInsn load1(MIPS32Reg dst, MIPS32Reg src, int offset) {
        return new LoadInsn(dst, src, offset);
    }

    @Override
    public List<MIPS32Insn> store(MIPS32Reg src, MIPS32Reg dst, int offset) {
        return Collections.singletonList(new StoreInsn(src, dst, offset));
    }

    @Override
    public List<MIPS32Insn> addi(MIPS32Reg dst, MIPS32Reg src, int offset) {
        return Collections.singletonList(addi1(dst, src, offset));
    }

    @Override
    public List<MIPS32Insn> li(MIPS32Reg dst, int immediate) {
        return Collections.singletonList(new LoadImmInsn(dst, immediate));
    }

    @Override
    public List<MIPS32Insn> move(MIPS32Reg dst, MIPS32Reg src) {
        return Collections.singletonList(move1(dst, src));
    }

    @Override
    public List<MIPS32Insn> jmp(MIPS32BasicBlock dst) {
        return Collections.singletonList(new JumpInsn(dst));
    }

    @Override
    public List<MIPS32Insn> jmpReg(MIPS32Reg dst) {
        return Collections.singletonList(new JumpRegInsn(dst));
    }

    @Override
    public List<MIPS32Insn> condBr(MIPS32BasicBlock dst, MIPS32Reg a, MIPS32Reg b, CondBranchStmt.ComparisonOperator op) {
        CondBrInsn.ComparisonOperator insnOp;
        switch(op) {
        case EQ:
            insnOp = CondBrInsn.ComparisonOperator.EQ;
            break;
        case NEQ:
            insnOp = CondBrInsn.ComparisonOperator.NE;
            break;
        case LT:
            insnOp = CondBrInsn.ComparisonOperator.LT;
            break;
        case GT:
            insnOp = CondBrInsn.ComparisonOperator.GT;
            break;
        case LEQ:
            insnOp = CondBrInsn.ComparisonOperator.LE;
            break;
        case GEQ:
            insnOp = CondBrInsn.ComparisonOperator.GE;
            break;
        default:
            throw new IllegalArgumentException("oh god think of the CHILDREN!!!!");
        }
        return Collections.singletonList(new CondBrInsn(dst, a, b, insnOp));
    }

    @Override
    public List<MIPS32Insn> fcondBr(MIPS32BasicBlock dst, MIPS32Reg a, MIPS32Reg b, CondBranchStmt.ComparisonOperator op) {
        FcmpInsn cmpInsn;
        FcondBrInsn brInsn;
        switch(op) {
        case EQ:
            cmpInsn = new FcmpInsn(a, b, FcmpInsn.ComparisonOperator.EQ);
            brInsn = new FcondBrInsn(dst, true);
            break;
        case NEQ:
            cmpInsn = new FcmpInsn(a, b, FcmpInsn.ComparisonOperator.EQ);
            brInsn = new FcondBrInsn(dst, false);
            break;
        case LT:
            cmpInsn = new FcmpInsn(a, b, FcmpInsn.ComparisonOperator.LT);
            brInsn = new FcondBrInsn(dst, true);
            break;
        case GT:
            cmpInsn = new FcmpInsn(a, b, FcmpInsn.ComparisonOperator.LE);
            brInsn = new FcondBrInsn(dst, false);
            break;
        case LEQ:
            cmpInsn = new FcmpInsn(a, b, FcmpInsn.ComparisonOperator.LE);
            brInsn = new FcondBrInsn(dst, true);
            break;
        case GEQ:
            cmpInsn = new FcmpInsn(a, b, FcmpInsn.ComparisonOperator.LT);
            brInsn = new FcondBrInsn(dst, false);
            break;
        default:
            throw new IllegalArgumentException("oh god think of the CHILDREN!!!!");
        }
        return Arrays.asList(
            cmpInsn,
            brInsn
        );
    }

    @Override
    public List<MIPS32Insn> iadd(MIPS32Reg dst, MIPS32Reg src1, MIPS32Reg src2) {
        return Collections.singletonList(new AddInsn(dst, src1, src2));
    }

    @Override
    public List<MIPS32Insn> isub(MIPS32Reg dst, MIPS32Reg src1, MIPS32Reg src2) {
        return Collections.singletonList(new SubInsn(dst, src1, src2));
    }

    @Override
    public List<MIPS32Insn> imul(MIPS32Reg dst, MIPS32Reg src1, MIPS32Reg src2) {
        return Collections.singletonList(new MulInsn(dst, src1, src2));
    }

    @Override
    public List<MIPS32Insn> idiv(MIPS32Reg dst, MIPS32Reg src1, MIPS32Reg src2) {
        return Arrays.asList(
            new DivInsn(src1, src2),
            new MoveInsn(dst, LO)
        );
    }

    @Override
    public List<MIPS32Insn> fadd(MIPS32Reg dst, MIPS32Reg src1, MIPS32Reg src2) {
        return Collections.singletonList(new FaddInsn(dst, src1, src2));
    }

    @Override
    public List<MIPS32Insn> fsub(MIPS32Reg dst, MIPS32Reg src1, MIPS32Reg src2) {
        return Collections.singletonList(new FsubInsn(dst, src1, src2));
    }

    @Override
    public List<MIPS32Insn> fmul(MIPS32Reg dst, MIPS32Reg src1, MIPS32Reg src2) {
        return Collections.singletonList(new FmulInsn(dst, src1, src2));
    }

    @Override
    public List<MIPS32Insn> fdiv(MIPS32Reg dst, MIPS32Reg src1, MIPS32Reg src2) {
        return Collections.singletonList(new FdivInsn(dst, src1, src2));
    }

    @Override
    public List<MIPS32Insn> and(MIPS32Reg dst, MIPS32Reg src1, MIPS32Reg src2) {
        return Collections.singletonList(new AndInsn(dst, src1, src2));
    }

    @Override
    public List<MIPS32Insn> or(MIPS32Reg dst, MIPS32Reg src1, MIPS32Reg src2) {
        return Collections.singletonList(new OrInsn(dst, src1, src2));
    }

    @Override
    public List<MIPS32Insn> rcall(MIPS32Function callee, MIPS32Reg dst, List<MIPS32Reg> src) {
        return Collections.singletonList(new CallPseudoInsn(callee, dst, src));
    }

    @Override
    public List<MIPS32Insn> call(MIPS32Function callee, List<MIPS32Reg> src) {
        return rcall(callee, ZERO, src);
    }

    @Override
    public MIPS32Reg returnValueReg() {
        return V0;
    }

    @Override
    public boolean isCall(MIPS32Insn i) {
        return i instanceof JalInsn || i instanceof CallPseudoInsn;
    }

    @Override
    public int getTypeSize(SymbolType type) {
        PrimitiveSymbolType primType = type.getPrimitiveType();
        return primType.isArray ? getWordSize() * primType.arraysize : getWordSize();
    }

    @Override
    public int floatToIntBits(float f) {
        return Float.floatToIntBits(f);
    }

    @Override
    public List<MIPS32Insn> cvtf2i(MIPS32Reg dst, MIPS32Reg src) {
        return Collections.singletonList(new CvtWSInsn(dst, src));
    }

    @Override
    public List<MIPS32Insn> cvti2f(MIPS32Reg dst, MIPS32Reg src) {
        return Collections.singletonList(new CvtSWInsn(dst, src));
    }

    @Override
    public MIPS32Insn comment(String text) {
        return new MIPS32CommentInsn(text);
    }

    public MoveInsn move1(MIPS32Reg dst, MIPS32Reg src) {
        return new MoveInsn(dst, src);
    }

    public AddiInsn addi1(MIPS32Reg dst, MIPS32Reg src, int immediate) {
        return new AddiInsn(dst, src, immediate);
    }

    public AddiInsn adjust(MIPS32Reg dst, int words) {
        return addi1(dst, dst, getWordSize() *words);
    }


    @Override
    public ToolsIntegration getToolsIntegration() {
        return new MIPS32ToolsIntegration();
    }

}
