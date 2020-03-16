package com.gangweedganggang.cs4240.optimization;

import com.gangweedganggang.cs4240.flowgraph.edges.FlowEdge;
import com.gangweedganggang.cs4240.flowgraph.edges.ImmediateEdge;
import com.gangweedganggang.cs4240.flowgraph.edges.UnconditionalJumpEdge;
import com.gangweedganggang.cs4240.ir.IRBasicBlock;
import com.gangweedganggang.cs4240.ir.IRControlFlowGraph;
import com.gangweedganggang.cs4240.ir.stmts.CondBranchStmt;
import com.gangweedganggang.cs4240.ir.stmts.GotoStmt;
import com.gangweedganggang.cs4240.flowgraph.Stmt;

import java.util.HashSet;

/**
 * The purpose of the CFG naturalization pass is to apply simple, trivial reductions to the CFG
 * to simplify it before later, heavier passes.
 * https://github.com/gt-retro-computing/gwcc/blob/master/gwcc/optimization/naturalization_pass.py
 */
public class NaturalizationPass {
    private final IRControlFlowGraph cfg;

    public NaturalizationPass(IRControlFlowGraph cfg) {
        this.cfg = cfg;
    }

    // merge two blocks into one.
    private void merge(IRBasicBlock bb, IRBasicBlock succ) {
        assert bb.get(bb.size() - 1) instanceof GotoStmt && ((GotoStmt) bb.get(bb.size() - 1)).getDst() == succ;

        // drop flow statement
        bb.remove(bb.size() - 1);

        // xfer statements
        succ.transferUpto(bb, succ.size());

        // copy edges
        for (FlowEdge<IRBasicBlock> e : cfg.getEdges(succ))
            cfg.addEdge(e.clone(bb, e.dst()));

        // drop merged block and all its incident edges
        cfg.removeVertex(succ);
    }

    // replace all references of bb_to_inline with bb_inline_as, and drop bb_to_inline.
    // this is good for cleaning up blocks that are just a single jump.
    private void inline(IRBasicBlock bbToInline, IRBasicBlock bbInlineAs) {
        for (FlowEdge<IRBasicBlock> e : cfg.getReverseEdges(bbToInline)) {
            IRBasicBlock pred = e.src();

            // copy edge
            cfg.addEdge(e.clone(pred, bbInlineAs));

            // rewrite flow statement
            rewriteFlowStatement(pred, bbToInline, bbInlineAs);
        }
        if (cfg.getEntry().equals(bbToInline))
            cfg.getEntries().add(bbInlineAs);
        cfg.removeVertex(bbToInline);
    }

    private void rewriteFlowStatement(IRBasicBlock pred, IRBasicBlock oldBlock, IRBasicBlock newBlock) {
        Stmt flowStmt = pred.get(pred.size() - 1);
        if (flowStmt instanceof GotoStmt) {
            GotoStmt gotoStmt = (GotoStmt) flowStmt;
            assert gotoStmt.getDst() == oldBlock;
            gotoStmt.setDst(newBlock);
        } else if (flowStmt instanceof CondBranchStmt) {
            CondBranchStmt branchStmt = (CondBranchStmt) flowStmt;
            assert branchStmt.getTrueBlock() == oldBlock || branchStmt.getFalseBlock() == oldBlock;
            if (branchStmt.getTrueBlock() == oldBlock)
                branchStmt.setTrueBlock(newBlock);
            if (branchStmt.getFalseBlock() == oldBlock)
                branchStmt.setFalseBlock(newBlock);
        } else {
            throw new IllegalStateException("invalid flow statement at end of block: " + flowStmt);
        }
    }

    // replace conditional jumps where both branches are the same with just a goto
    private void killTrivialConditional(IRBasicBlock bb) {
        assert cfg.getEdges(bb).size() == 1 && bb.get(bb.size() - 1) instanceof CondBranchStmt;
        CondBranchStmt branchStmt = (CondBranchStmt) bb.get(bb.size() - 1);
        assert branchStmt.getTrueBlock() == branchStmt.getFalseBlock();
        IRBasicBlock target = branchStmt.getTrueBlock();
        bb.set(bb.size() - 1, new GotoStmt(target));
        for (FlowEdge<IRBasicBlock> e : new HashSet<>(cfg.getEdges(bb)))
            cfg.removeEdge(e);
        cfg.addEdge(new UnconditionalJumpEdge<>(bb, target));
    }

    // this is a really, really ugly kludge.
    private void fixMultipleImmediates() {
        for (IRBasicBlock bb : cfg.verticesInOrder()) {
            int count = 0;
            for (FlowEdge<IRBasicBlock> e : cfg.getReverseEdges(bb)) {
                if (e instanceof ImmediateEdge) {
                    count++;
                    if (count > 1) {
                        IRBasicBlock jumpStub = cfg.newBlock();
                        jumpStub.add(new GotoStmt(e.dst()));
                        cfg.addEdge(new UnconditionalJumpEdge<>(jumpStub, e.dst()));

                        cfg.removeEdge(e);
                        cfg.addEdge(e.clone(e.src(), jumpStub));
                        rewriteFlowStatement(e.src(), e.dst(), jumpStub);
                    }
                }
            }
        }
    }

    public void run() {
        // iteratively clean up the graph and stop when no more modifications are made.
        while (true) loop:{
            for (IRBasicBlock bb : cfg.vertices()) {
                // kill empty blocks
                if (bb.size() == 0) {
                    if (cfg.getEdges(bb).size() > 0)
                        throw new IllegalStateException("empty block has outgoing edges");
                    else if (cfg.getReverseEdges(bb).size() > 0)
                        throw new IllegalStateException("empty block has incoming edges");
                    // System.out.println("kill empty " + bb);
                    cfg.removeVertex(bb);
                    break loop;
                }

                // kill unreachable blocks
                if (cfg.getReverseEdges(bb).size() == 0 && !cfg.getEntries().contains(bb)) {
                    // System.out.println("kill unreachable " + bb);
                    cfg.removeVertex(bb);
                    break loop;
                }

                // replace conditional jumps where both branches are the same with just a goto
                if (bb.get(bb.size() - 1) instanceof CondBranchStmt) {
                    CondBranchStmt branchStmt = (CondBranchStmt) bb.get(bb.size() - 1);
                    if (branchStmt.getTrueBlock() == branchStmt.getFalseBlock()) {
                        // System.out.println("simplify trivial conditional " + bb);
                        killTrivialConditional(bb);
                        break loop;
                    }
                }

                // merge singleton immediate flow siblings
                if (cfg.getEdges(bb).size() == 1) {
                    IRBasicBlock succ = cfg.getEdges(bb).iterator().next().dst();
                    if (cfg.getReverseEdges(succ).size() == 1 && bb.get(bb.size() - 1) instanceof GotoStmt) {
                        // System.out.println("merge " + succ + " upto " + bb);
                        merge(bb, succ);
                        break loop;
                    }
                }

                // blocks that are a single jump may be inlined
                if (bb.size() == 1 && bb.get(0) instanceof GotoStmt) {
                    IRBasicBlock dst = ((GotoStmt) bb.get(0)).getDst();
                    if (dst != bb) { // don't inline jumps to self...lol
                        // System.out.println("inline " + bb + " " + CFGUtils.printBlock(bb));
                        inline(bb, dst);
                        break loop;
                    }
                }
            }
            break;
        }
        fixMultipleImmediates();
    }
}
