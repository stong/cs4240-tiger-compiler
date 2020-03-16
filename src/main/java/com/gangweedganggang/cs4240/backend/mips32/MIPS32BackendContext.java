package com.gangweedganggang.cs4240.backend.mips32;

import com.gangweedganggang.cs4240.backend.BackendContext;
import com.gangweedganggang.cs4240.backend.mips32.insn.*;
import com.gangweedganggang.cs4240.flowgraph.edges.ConditionalJumpEdge;
import com.gangweedganggang.cs4240.flowgraph.edges.ImmediateEdge;
import com.gangweedganggang.cs4240.flowgraph.edges.UnconditionalJumpEdge;
import com.gangweedganggang.cs4240.frontend.*;

import static com.gangweedganggang.cs4240.backend.mips32.MIPS32Reg.*;

public class MIPS32BackendContext extends BackendContext<MIPS32Reg, MIPS32Insn, MIPS32BasicBlock, MIPS32ControlFlowGraph, MIPS32Target, MIPS32Function> {
    public MIPS32BackendContext(MIPS32Target isa) {
        super(isa);
        addBuiltins();
        addIntrinsics();
    }

    void addBuiltins() {
        genPrinti();
        genPrintf();
    }

    void addIntrinsics() {
        genMemset();
        genPow();
        genPowf();
        genAssert();
    }

    private void genMemset() {
        /*
        lw $t0,8($fp)
        lw $t1,12($fp)
        lw $t2,16($fp)
        li $t3,1
        loop:
        bge $t3,$t2,epilogue
        body:
        sw $t1,t0(0)
        addi $t3,$t3,1
        addi $t0,$t0,4
        j loop
         */
        MIPS32Function memset = getIsa().newFunction("_memset", 3);
        memset.getPrologue().add(new LoadInsn(T0, FP, 8));
        memset.getPrologue().add(new LoadInsn(T1, FP, 12));
        memset.getPrologue().add(new LoadInsn(T2, FP, 16));
        memset.getPrologue().add(new LoadImmInsn(T3, 1));
        MIPS32BasicBlock loop = memset.getCfg().newBlock();
        memset.getCfg().addEdge(new ImmediateEdge<>(memset.getPrologue(), loop));
        loop.add(new CondBrInsn(memset.getEpilogue(), T3, T2, CondBrInsn.ComparisonOperator.GE));
        MIPS32BasicBlock body = memset.getCfg().newBlock();
        memset.getCfg().addEdge(new ImmediateEdge<>(loop, body));
        memset.getCfg().addEdge(new ConditionalJumpEdge<>(loop, memset.getEpilogue()));
        body.add(new StoreInsn(T1, T0, 0));
        body.add(new AddiInsn(T3, T3, 1));
        body.add(new AddiInsn(T0, T0, 4));
        body.add(new JumpInsn(loop));
        memset.getCfg().addEdge(new UnconditionalJumpEdge<>(body, loop));
        getFunctionMap().put(Intrinsics.MEMSET, memset);
    }

    private void genPow() {
        /*
        lw $t0,8($fp) -- base
        lw $t1,12($fp) -- power
        li $t2,1 -- counter
        move $v0, $t0 -- result
        loop:
        bge $t2,$t1,epilogue
        body:
        mul $v0, $v0, $t0
        addi $t2,$t2,1
        j loop
         */
        MIPS32Function pow = getIsa().newFunction("_pow", 3);
        pow.getPrologue().add(new LoadInsn(T0, FP, 8));
        pow.getPrologue().add(new LoadInsn(T1, FP, 12));
        pow.getPrologue().add(new LoadImmInsn(T2, 1));
        pow.getPrologue().add(new MoveInsn(V0, T0));
        MIPS32BasicBlock loop = pow.getCfg().newBlock();
        pow.getCfg().addEdge(new ImmediateEdge<>(pow.getPrologue(), loop));
        loop.add(new CondBrInsn(pow.getEpilogue(), T2, T1, CondBrInsn.ComparisonOperator.GE));
        MIPS32BasicBlock body = pow.getCfg().newBlock();
        pow.getCfg().addEdge(new ImmediateEdge<>(loop, body));
        pow.getCfg().addEdge(new ConditionalJumpEdge<>(loop, pow.getEpilogue()));
        body.add(new MulInsn(V0, V0, T0));
        body.add(new AddiInsn(T2, T2, 1));
        body.add(new JumpInsn(loop));
        pow.getCfg().addEdge(new UnconditionalJumpEdge<>(body, loop));
        getFunctionMap().put(Intrinsics.POW, pow);
    }

    private void genPowf() {
        /*
        l.s $f0,8($fp) -- base
        lw $t1,12($fp) -- power
        li $t2,0 -- counter
        move $f1, $f0 -- result
        loop:
        bge $t2,$t1,epilogue
        body:
        mul.s $f1, $f1, $f0
        addi $t2,$t2,1
        j loop
        epilogue:
        mfc1 $v0, $f1
         */
        MIPS32Reg F0 = MIPS32Reg.newFloat(0);
        MIPS32Reg F1 = MIPS32Reg.newFloat(1);

        MIPS32Function powf = getIsa().newFunction("_powf", 3);
        powf.getPrologue().add(new LoadInsn(F0, FP, 8));
        powf.getPrologue().add(new LoadInsn(T1, FP, 12));
        powf.getPrologue().add(new LoadImmInsn(T2, 0));
        powf.getPrologue().add(new MoveInsn(F1, F0));
        MIPS32BasicBlock loop = powf.getCfg().newBlock();
        powf.getCfg().addEdge(new ImmediateEdge<>(powf.getPrologue(), loop));
        loop.add(new CondBrInsn(powf.getEpilogue(), T2, T1, CondBrInsn.ComparisonOperator.GE));
        MIPS32BasicBlock body = powf.getCfg().newBlock();
        powf.getCfg().addEdge(new ImmediateEdge<>(loop, body));
        powf.getCfg().addEdge(new ConditionalJumpEdge<>(loop, powf.getEpilogue()));
        body.add(new FmulInsn(F1, F1, F0));
        body.add(new AddiInsn(T2, T2, 1));
        body.add(new JumpInsn(loop));
        powf.getCfg().addEdge(new UnconditionalJumpEdge<>(body, loop));
        powf.getEpilogue().add(0, new MoveInsn(V0, F1));
        getFunctionMap().put(Intrinsics.POWF, powf);
    }

    private void genPrinti() {
        MIPS32Function printi = getIsa().newFunction("printi", 1);
        printi.getPrologue().add(new LoadInsn(A0, FP, 8));
        printi.getPrologue().add(new LoadImmInsn(V0, 1));
        printi.getPrologue().add(new SyscallInsn());
        printi.getCfg().addEdge(new ImmediateEdge<>(printi.getPrologue(), printi.getEpilogue()));
        getFunctionMap().put(RootScope.Builtins.PRINTI, printi);
    }

    private void genPrintf() {
        MIPS32Function printf = getIsa().newFunction("printf", 1);
        printf.getPrologue().add(new LoadImmInsn(V0, 2));
        printf.getPrologue().add(new LoadInsn(A0, FP, 8));
        printf.getPrologue().add(new MoveInsn(MIPS32Reg.newFloat(12), A0));
        printf.getPrologue().add(new SyscallInsn());
        printf.getCfg().addEdge(new ImmediateEdge<>(printf.getPrologue(), printf.getEpilogue()));
        getFunctionMap().put(RootScope.Builtins.PRINTF, printf);
    }

    private void genAssert() {
        /*
        lw $a0,8($fp)
        teq $a0, $zero, 23
         */

        MIPS32Function xassert = getIsa().newFunction("_assert", 1);
        xassert.getPrologue().add(new LoadInsn(A0, FP, 8));
        xassert.getCfg().addEdge(new ConditionalJumpEdge<>(xassert.getPrologue(), xassert.getEpilogue()));
        xassert.getPrologue().add(new CondBrInsn(xassert.getEpilogue(), A0, ZERO, CondBrInsn.ComparisonOperator.NE));
        xassert.getPrologue().add(new BreakInsn(7));
        xassert.getCfg().addEdge(new ImmediateEdge<>(xassert.getPrologue(), xassert.getEpilogue()));
        getFunctionMap().put(RootScope.Builtins.ASSERT, xassert);

    }
}
