package com.gangweedganggang.cs4240.backend;

import com.gangweedganggang.cs4240.flowgraph.BasicBlock;
import com.gangweedganggang.cs4240.flowgraph.Stmt;
import com.gangweedganggang.cs4240.frontend.TigerFrontend;
import com.gangweedganggang.cs4240.frontend.TigerFunction;
import com.gangweedganggang.cs4240.frontend.TigerVariable;
import com.gangweedganggang.cs4240.stdlib.util.TabbedStringWriter;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.List;

public class TigerBackend {
    private final TigerFrontend frontend;
    private final BackendContext ctx;

    public static boolean useBriggsAllocator = false;

    public TigerBackend(TigerFrontend frontend, BackendContext ctx) {
        this.frontend = frontend;
        this.ctx = ctx;
    }

    public Collection<TargetFunction> getFuncs() {
        return ctx.functionMap.values();
    }

    public void run(OutputStream stream) {
        for (TigerVariable globalVar : frontend.getVars()) {
            ctx.addGlobal(globalVar);
        }

        for (TigerFunction irFunc : frontend.getFuncs()) {
            ctx.functionMap.put(irFunc, ctx.isa.newFunction(irFunc.name, irFunc.getParams().size()));
        }

        for (TigerFunction irFunc : frontend.getFuncs()) {
            TargetFunction asmFunc = (TargetFunction) ctx.functionMap.get(irFunc);
            InstructionSelection isel = new InstructionSelection(ctx, irFunc, asmFunc);
            isel.run();
            System.out.println(asmFunc.getCfg());
            // BriggsRegisterAllocator regalloc = new BriggsRegisterAllocator(ctx, asmFunc);
            // regalloc.run();

            useBriggsAllocator = false;
            if (useBriggsAllocator) {
                new BriggsRegisterAllocator(ctx, asmFunc).run();
            } else {
                new NaiveRegisterAllocator(ctx, asmFunc).run();
            }

            System.out.println(asmFunc.getCfg());
        }

        // System.out.println("\n\n\nOUTPUT:\n\n\n");

        TabbedStringWriter sw = new TabbedStringWriter();
        sw.print(".text\n");
        sw.print(".globl main\n");
        for (Object func : ctx.getFunctionMap().values()) {
            printFunction(sw, (TargetFunction) func);
        }
        sw.print(".data\n");
        for (TigerVariable var : frontend.getVars()) {
            sw.print("_" + var.name + ": " + ".word 0").newline();
        }

        PrintStream ps = new PrintStream(stream);
        ps.print(sw.toString());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void printFunction(TabbedStringWriter sw, TargetFunction func) {
        sw.print(func.name + ":").newline();
        for (BasicBlock<Stmt<?>> bb : (List<BasicBlock<Stmt<?>>>)func.getCfg().verticesInOrder()) {
            sw.print(bb.getDisplayName() + ":").tab();
            for (Stmt<?> stmt : bb) {
                sw.newline().print(stmt.toString());
            }
            sw.untab().newline();
        }
        sw.newline();
    }
}
