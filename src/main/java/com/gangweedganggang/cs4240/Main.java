package com.gangweedganggang.cs4240;

import com.gangweedganggang.cs4240.ast.ASTRoot;
import com.gangweedganggang.cs4240.backend.TargetFunction;
import com.gangweedganggang.cs4240.backend.TigerBackend;
import com.gangweedganggang.cs4240.backend.ToolsIntegration;
import com.gangweedganggang.cs4240.backend.mips32.MIPS32BackendContext;
import com.gangweedganggang.cs4240.backend.mips32.MIPS32Target;
import com.gangweedganggang.cs4240.frontend.*;
import com.gangweedganggang.cs4240.optimization.NaturalizationPass;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.*;

public class Main {

    public static String runResult = null;

    private static Namespace parseArgs(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("java -jar cs4240.jar").build()
                .defaultHelp(true)
                .description("Compile tiger to mips assembly.");

        parser.addArgument("--out", "-o").help("output file");

        parser.addArgument("--run").action(Arguments.storeTrue()).help("Execute assembled MIPS");

        parser.addArgument("file").help("File to compile");

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        if (ns.getString("out") == null) {
            String file = ns.getString("file");
            if (file.endsWith(".tiger")) {
                file = file.substring(0, file.length() - ".tiger".length());
            }
            ns.getAttrs().put("out", file + ".s");
        }

        return ns;
    }

    public static int internalMain(String[] args) {
        Namespace ns = parseArgs(args);

        CharStream stream = null;
        try {
            stream = CharStreams.fromFileName(ns.getString("file"));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to read input file.");
            return 1;
        }

        FrontendParser parser = new FrontendParser(); // lex/parse
        ASTRoot ast = parser.parse(stream);
        if (ast == null) {
            System.err.println("Parse error");
            return 1;
        }
        // System.out.println(ast);
        TigerFrontend frontend = new TigerFrontend(); // semantic crap
        try {
            frontend.generateIR(ast);
        } catch (CompilationError e) {
            System.err.println("Error at line " + e.row + ":" + e.column + ": " + e.getMessage());
            return 1;
        }
        System.out.println("successful compile");
        System.out.println(frontend.dumpSymbolTable());

        for (TigerFunction func : frontend.getFuncs()) {
            new NaturalizationPass(func.getCfg()).run();
            func.getCfg().verify();
        }

        for (TigerVariable var : frontend.getVars()) {
            System.out.println("var " + var);
        }
        System.out.println();
        for (TigerFunction func : frontend.getFuncs()) {
            System.out.println("func " + func);
            System.out.println(func.getCfg());
        }

        MIPS32Target target = new MIPS32Target();
        TigerBackend backend = new TigerBackend(frontend, new MIPS32BackendContext(target));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        backend.run(baos);

        for (TargetFunction func : backend.getFuncs()) {
            func.getCfg().verify();
        }

        File outFile = new File(ns.getString("out"));
        try(FileOutputStream fos = new FileOutputStream(outFile)) {
            fos.write(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }

        if (ns.getBoolean("run")) {
            ToolsIntegration tools = target.getToolsIntegration();

            System.out.println("=== RUN ===\n");

            String result = tools.executeAssemblyFile(outFile);
            if (result == null) {
                result = "Failed to execute assembled code.";
            }
            System.out.println(result);
            runResult = result;
        }

        return 0;
    }

    public static void main(String[] args) {
        System.exit(internalMain(args));
    }

}

