package com.gangweedganggang.cs4240.backend;

import com.gangweedganggang.cs4240.flowgraph.BasicBlock;
import com.gangweedganggang.cs4240.flowgraph.ControlFlowGraph;
import com.gangweedganggang.cs4240.flowgraph.Local;
import com.gangweedganggang.cs4240.flowgraph.edges.ConditionalJumpEdge;
import com.gangweedganggang.cs4240.flowgraph.edges.ImmediateEdge;
import com.gangweedganggang.cs4240.flowgraph.edges.UnconditionalJumpEdge;
import com.gangweedganggang.cs4240.frontend.PrimitiveSymbolType;
import com.gangweedganggang.cs4240.frontend.TigerFunction;
import com.gangweedganggang.cs4240.frontend.TigerVariable;
import com.gangweedganggang.cs4240.ir.IRBasicBlock;
import com.gangweedganggang.cs4240.ir.stmts.*;

import java.util.*;

// during isel we assume we still have unlimited 'registers'
// since regalloc not happened yet
public class InstructionSelection<Reg extends TargetReg, Insn extends TargetInsn<Reg>,
        BB extends BasicBlock<Insn>, CFG extends ControlFlowGraph<BB>,
        ISA extends TargetISA<Reg, Insn, BB, CFG, Func, ISA>,
        Func extends TargetFunction<Reg, Insn, BB, CFG, Func, ISA>> {

    private final BackendContext<Reg, Insn, BB, CFG, ISA, Func> ctx;
    private final TigerFunction irFunc;
    private final Func asmFunc;
    private final Map<IRBasicBlock, BB> blockMap;
    private final Map<Local, Reg> localsMap;
    private final ISA isa;

    private CFG asmCfg;

    public InstructionSelection(BackendContext<Reg, Insn, BB, CFG, ISA, Func> ctx, TigerFunction irFunc, Func asmFunc) {
        this.ctx = ctx;
        this.irFunc = irFunc;
        this.asmFunc = asmFunc;
        blockMap = new HashMap<>();
        localsMap = new HashMap<>();
        this.isa = ctx.isa;
    }

    public void run() {
        // alloc locals
        scanLocals();

        // lowering bb's by dfs
        asmCfg = asmFunc.getCfg();
        BB entry = visitBlock(irFunc.getCfg().getEntry());
        asmCfg.addEdge(new ImmediateEdge<>(asmFunc.getPrologue(), entry)); // immediateedges are REALLY immediate!!!
    }

    private void scanLocals() {
        for (IRBasicBlock bb : irFunc.getCfg().vertices()) {
            for (IRStmt stmt : bb) {
                TigerVariable var = null;
                if (stmt instanceof VarLoadStmt) {
                    var = ((VarLoadStmt) stmt).getVariable();
                } else if (stmt instanceof VarStoreStmt) {
                    var = ((VarStoreStmt) stmt).getVariable();
                }
                if (var != null && var.storage.equals(TigerVariable.StorageClass.LOCAL)) {
                    if (!asmFunc.haveLocal(var)) {
                        asmFunc.allocLocal(var);
                    }
                }
            }
        }
    }

    private BB visitBlock(IRBasicBlock irBlock) {
        BB asmBlock = blockMap.get(irBlock);
        if (asmBlock != null)
            return asmBlock;

        blockMap.put(irBlock, asmBlock = asmFunc.getCfg().newBlock());

        for (IRStmt irStmt : irBlock) {
            selectStmt(irStmt, asmBlock);
        }

        return asmBlock;
    }

    private Reg temp(Local l) {
        Reg r = localsMap.get(l);
        if (r != null)
            return localsMap.get(l);
        if (l.getType().getPrimitiveType().isArray || l.getType().getPrimitiveType().equals(PrimitiveSymbolType.INT)) {
            r = asmFunc.getRegPool().getTemp(RegType.INT);
        } else if (l.getType().getPrimitiveType().equals(PrimitiveSymbolType.FLOAT)) {
            r = asmFunc.getRegPool().getTemp(RegType.FLOAT);
        } else {
            throw new UnsupportedOperationException("jump die");
        }
        localsMap.put(l, r);
        return r;
    }

    private List<Insn> loadLocal(Reg dst, TigerVariable l) {
        if (l.storage.equals(TigerVariable.StorageClass.GLOBAL)) {
            if (l.type.getPrimitiveType().isArray) {
                return isa.addi(dst, isa.getGp(), ctx.globalsOffsetMap.get(l));
            } else {
                return isa.load(dst, isa.getGp(), ctx.globalsOffsetMap.get(l));
            }
        } else {
            if (l.type.getPrimitiveType().isArray) {
                return isa.addi(dst, isa.getFp(), asmFunc.getLocalFrameOffset(l));
            } else {
                return isa.load(dst, isa.getFp(), asmFunc.getLocalFrameOffset(l));
            }
        }
    }

    private List<Insn> storeLocal(TigerVariable l, Reg val) {
        if (l.type.getPrimitiveType().isArray)
            throw new UnsupportedOperationException("jump die");
        if (l.storage.equals(TigerVariable.StorageClass.GLOBAL)) {
            return isa.store(val, isa.getGp(), ctx.globalsOffsetMap.get(l));
        } else {
            return isa.store(val, isa.getFp(), asmFunc.getLocalFrameOffset(l));
        }
    }

    // naive tiling since this is due in like 72hrs
    private void selectStmt(IRStmt irStmt, BB curBlock) {
        if (irStmt.getClass().equals(CommentStmt.class)) {
            CommentStmt s = (CommentStmt) irStmt;
            curBlock.add(isa.comment(s.getComment()));
        } else if (irStmt.getClass().equals(ArrayLoadStmt.class)) {
            ArrayLoadStmt s = (ArrayLoadStmt) irStmt;
            Reg ptr = asmFunc.getRegPool().getTemp(RegType.INT);
            curBlock.addAll(isa.li(ptr, isa.getWordSize()));
            curBlock.addAll(isa.imul(ptr, ptr, temp(s.getIndex())));
            curBlock.addAll(isa.iadd(ptr, ptr, temp(s.getArray())));
            curBlock.addAll(isa.load(temp(s.getLhs()), ptr, 0));
        } else if (irStmt.getClass().equals(ArraySetStmt.class)) {
            ArraySetStmt s = (ArraySetStmt) irStmt;
            Reg size = asmFunc.getRegPool().getTemp(RegType.INT);
            curBlock.addAll(isa.li(size, s.getSize()));
            curBlock.addAll(isa.call(ctx.getFunctionMap().get(BackendContext.Intrinsics.MEMSET), Arrays.asList(temp(s.getArray()), temp(s.getValue()), size)));
        } else if (irStmt.getClass().equals(ArrayStoreStmt.class)) {
            ArrayStoreStmt s = (ArrayStoreStmt) irStmt;
            Reg ptr = asmFunc.getRegPool().getTemp(RegType.INT);
            curBlock.addAll(isa.li(ptr, isa.getWordSize()));
            curBlock.addAll(isa.imul(ptr, ptr, temp(s.getIndex())));
            curBlock.addAll(isa.iadd(ptr, ptr, temp(s.getArray())));
            curBlock.addAll(isa.store(temp(s.getRhs()), ptr, 0));
        } else if (irStmt.getClass().equals(BinOpStmt.class)) {
            BinOpStmt s = (BinOpStmt) irStmt;
            if (s.getDst().getType().getPrimitiveType().equals(PrimitiveSymbolType.INT)) {
                switch (s.getOp()) {
                case ADD:
                    curBlock.addAll(isa.iadd(temp(s.getDst()), temp(s.getLeftOperand()), temp(s.getRightOperand())));
                    break;
                case SUB:
                    curBlock.addAll(isa.isub(temp(s.getDst()), temp(s.getLeftOperand()), temp(s.getRightOperand())));
                    break;
                case MUL:
                    curBlock.addAll(isa.imul(temp(s.getDst()), temp(s.getLeftOperand()), temp(s.getRightOperand())));
                    break;
                case DIV:
                    curBlock.addAll(isa.idiv(temp(s.getDst()), temp(s.getLeftOperand()), temp(s.getRightOperand())));
                    break;
                case AND:
                    curBlock.addAll(isa.and(temp(s.getDst()), temp(s.getLeftOperand()), temp(s.getRightOperand())));
                    break;
                case OR:
                    curBlock.addAll(isa.or(temp(s.getDst()), temp(s.getLeftOperand()), temp(s.getRightOperand())));
                    break;
                case POW:
                    curBlock.addAll(isa.rcall(ctx.getFunctionMap().get(BackendContext.Intrinsics.POW), temp(s.getDst()), Arrays.asList(temp(s.getLeftOperand()), temp(s.getRightOperand()))));
                    break;
                }
            } else if (s.getDst().getType().getPrimitiveType().equals(PrimitiveSymbolType.FLOAT)) {
                switch (s.getOp()) {
                case ADD:
                    curBlock.addAll(isa.fadd(temp(s.getDst()), temp(s.getLeftOperand()), temp(s.getRightOperand())));
                    break;
                case SUB:
                    curBlock.addAll(isa.fsub(temp(s.getDst()), temp(s.getLeftOperand()), temp(s.getRightOperand())));
                    break;
                case MUL:
                    curBlock.addAll(isa.fmul(temp(s.getDst()), temp(s.getLeftOperand()), temp(s.getRightOperand())));
                    break;
                case DIV:
                    curBlock.addAll(isa.fdiv(temp(s.getDst()), temp(s.getLeftOperand()), temp(s.getRightOperand())));
                    break;
                case POW:
                    curBlock.addAll(isa.rcall(ctx.getFunctionMap().get(BackendContext.Intrinsics.POWF), temp(s.getDst()), Arrays.asList(temp(s.getLeftOperand()), temp(s.getRightOperand()))));
                    break;
                }
            } else {
                throw new UnsupportedOperationException("jump die");
            }
        } else if (irStmt.getClass().equals(CallStmt.class)) {
            CallStmt s = (CallStmt) irStmt;
            List<Reg> args = new ArrayList<>();
            for (int i = s.getArgs().size() - 1; i >= 0; i--)
                args.add(temp(s.getArgs().get(i)));
            curBlock.addAll(isa.call(ctx.functionMap.get(s.getFunc()), args));
        } else if (irStmt.getClass().equals(CondBranchStmt.class)) {
            CondBranchStmt s = (CondBranchStmt) irStmt;
            BB asmTrueDst = visitBlock(s.getTrueBlock());
            BB asmFalseDst = visitBlock(s.getFalseBlock());
            if (s.getLeftOperand().getType().getPrimitiveType().equals(PrimitiveSymbolType.INT)) {
                curBlock.addAll(isa.condBr(asmTrueDst, temp(s.getLeftOperand()), temp(s.getRightOperand()), s.getOp()));
            } else if (s.getLeftOperand().getType().getPrimitiveType().equals(PrimitiveSymbolType.FLOAT)) {
                curBlock.addAll(isa.fcondBr(asmTrueDst, temp(s.getLeftOperand()), temp(s.getRightOperand()), s.getOp()));
            } else {
                throw new UnsupportedOperationException("jump die");
            }
            asmCfg.addEdge(new ConditionalJumpEdge<>(curBlock, asmTrueDst));
            asmCfg.addEdge(new ImmediateEdge<>(curBlock, asmFalseDst));
        } else if (irStmt.getClass().equals(ConstLoadStmt.class)) {
            ConstLoadStmt s = (ConstLoadStmt) irStmt;
            int value;
            if (s.getLhs().getType().getPrimitiveType().equals(PrimitiveSymbolType.INT)) {
                value = (int) s.getConstant();
            } else if (s.getLhs().getType().getPrimitiveType().equals(PrimitiveSymbolType.FLOAT)) {
                value = isa.floatToIntBits((float) s.getConstant());
            } else {
                throw new UnsupportedOperationException("jump die");
            }
            curBlock.addAll(isa.li(temp(s.getLhs()), value));
        } else if (irStmt.getClass().equals(GotoStmt.class)) {
            GotoStmt s = (GotoStmt) irStmt;
            BB asmDst = visitBlock(s.getDst());
            curBlock.addAll(isa.jmp(asmDst));
            asmCfg.addEdge(new UnconditionalJumpEdge<>(curBlock, asmDst));
        } else if (irStmt.getClass().equals(MainReturnStmt.class)) {
            MainReturnStmt s = (MainReturnStmt) irStmt;
            curBlock.addAll(isa.jmp(asmFunc.getEpilogue()));
            asmCfg.addEdge(new UnconditionalJumpEdge<>(curBlock, asmFunc.getEpilogue()));
        } else if (irStmt.getClass().equals(ParamStmt.class)) {
            ParamStmt s = (ParamStmt) irStmt;
            curBlock.addAll(isa.load(temp(s.getLhs()), isa.getFp(), asmFunc.getParamFrameOffset(s.getIndex())));
        } else if (irStmt.getClass().equals(RcallStmt.class)) {
            RcallStmt s = (RcallStmt) irStmt;
            List<Reg> args = new ArrayList<>();
            for (int i = s.getArgs().size() - 1; i >= 0; i--)
                args.add(temp(s.getArgs().get(i)));
            curBlock.addAll(isa.rcall(ctx.functionMap.get(s.getFunc()), temp(s.getLhs()), args));
        } else if (irStmt.getClass().equals(ReturnStmt.class)) {
            ReturnStmt s = (ReturnStmt) irStmt;
            curBlock.addAll(isa.move(isa.returnValueReg(), temp(s.getRet())));
            curBlock.addAll(isa.jmp(asmFunc.getEpilogue()));
            asmCfg.addEdge(new UnconditionalJumpEdge<>(curBlock, asmFunc.getEpilogue()));
        } else if (irStmt.getClass().equals(VarLoadStmt.class)) {
            VarLoadStmt s = (VarLoadStmt) irStmt;
            curBlock.addAll(loadLocal(temp(s.getLhs()), s.getVariable()));
        } else if (irStmt.getClass().equals(VarStoreStmt.class)) {
            VarStoreStmt s = (VarStoreStmt) irStmt;
            curBlock.addAll(storeLocal(s.getVariable(), temp(s.getRhs())));
        } else if (irStmt.getClass().equals(CastStmt.class)) {
            CastStmt s = (CastStmt) irStmt;
            if (s.getLhs().getType().getPrimitiveType().equals(PrimitiveSymbolType.INT)) {
                if (s.getRhs().getType().getPrimitiveType().equals(PrimitiveSymbolType.FLOAT)) {
                    Reg tmp = asmFunc.getRegPool().getTemp(RegType.FLOAT);
                    curBlock.addAll(isa.cvtf2i(tmp, temp(s.getRhs())));
                    curBlock.addAll(isa.move(temp(s.getLhs()), tmp));
                } else {
                    throw new UnsupportedOperationException("jump die");
                }
            } else if (s.getLhs().getType().getPrimitiveType().equals(PrimitiveSymbolType.FLOAT)) {
                if (s.getRhs().getType().getPrimitiveType().equals(PrimitiveSymbolType.INT)) {
                    curBlock.addAll(isa.move(temp(s.getLhs()), temp(s.getRhs())));
                    curBlock.addAll(isa.cvti2f(temp(s.getLhs()), temp(s.getLhs())));
                } else {
                    throw new UnsupportedOperationException("jump die");
                }
            } else {
                throw new UnsupportedOperationException("jump die");
            }
        } else {
            throw new UnsupportedOperationException("jump die");
        }
    }

}
