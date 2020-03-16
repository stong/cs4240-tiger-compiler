package com.gangweedganggang.cs4240;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.*;

import java.io.IOException;
import java.util.ArrayList;

public class Checkpoint1 {

    private static class ThrowingErrorListener extends BaseErrorListener {
        boolean errorFlag = false;
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
                throws ParseCancellationException {
            errorFlag = true;
//            throw new ParseCancellationException("Line " + line + ":" + charPositionInLine + " " + msg);
        }
    }
    private static class TreeDumper implements ParseTreeListener {
        ArrayList<String> yeet = new ArrayList<>();

        @Override
        public void visitTerminal(TerminalNode node) {
            String dispName = TigerLexer.VOCABULARY.getSymbolicName(node.getSymbol().getType());
            System.out.println("<" + dispName + ", " + node.getSymbol().getText() + ">");
            yeet.add(dispName + " ");
        }

        @Override
        public void visitErrorNode(ErrorNode node) {

        }

        @Override
        public void enterEveryRule(ParserRuleContext ctx) {

        }

        @Override
        public void exitEveryRule(ParserRuleContext ctx) {

        }
    }


    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java -jar parser.jar <file>");
            System.exit(1);
        }

        CharStream stream = null;
        try {
            stream = CharStreams.fromFileName(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to read input file.");
            System.exit(1);
        }

        ThrowingErrorListener err = new ThrowingErrorListener();

        TigerLexer lexer = new TigerLexer(stream);
        lexer.addErrorListener(err);
        // for (Token t : lexer.getAllTokens()) {
        //     System.out.printf("<%s:%s> ", TigerLexer.VOCABULARY.getDisplayName(t.getType()), t.getText());
        // }
        // System.out.println();
        // System.out.exit();

        TokenStream tokenStream = new CommonTokenStream(lexer);
        TigerParser parser = new TigerParser(tokenStream);
        parser.addErrorListener(err);

        TreeDumper d = new TreeDumper();
        ParseTreeWalker.DEFAULT.walk(d, parser.tigerProgram());

        if (err.errorFlag) {
            System.err.println("syntax error");
            System.exit(1);
        } else {
            System.out.println("successful parse");
        }

        d.yeet.forEach(System.out::print);

       // ParserRuleContext expr = parser.stmt();
       // dumpParsetree(expr, 0);
       // System.out.println();
       // System.out.println();
       // System.out.println();
       //
       // fixExprTails(expr);
       //
       // prettyPrint(expr);
    }

    private static ParserRuleContext leftRotateMerge(ParserRuleContext node) {
        ParserRuleContext parent = node.getParent();
        int selfIndex = parent.children.indexOf(node);

        parent.children.remove(selfIndex);
        node.setParent(null);

        if (node.getChildCount() > 0) {
            for (int i = 0; i < node.getChildCount(); i++) {
                ParseTree child = node.getChild(i);
                parent.children.add(selfIndex + i, child);
                child.setParent(parent);
            }
            node.children.clear();
        }

        return parent;
    }

    private static void fixExprTails(ParseTree node) {
        if (node instanceof ParserRuleContext) {
            ParserRuleContext rule = (ParserRuleContext) node;
            String name = rule.getClass().getSimpleName();
            for (int i = 0; i < rule.getChildCount(); i++) {
                fixExprTails(rule.getChild(i));
            }
            if (name.startsWith("Expr") && name.endsWith("TailContext")) {
                leftRotateMerge(rule);
            }
        }
    }

    private static void dumpParsetree(ParseTree node, int level) {
        String indent = repeat(" ", level);
        if (node instanceof TerminalNode) {
            if (node instanceof TerminalNodeImpl) {
                TerminalNodeImpl terminal = (TerminalNodeImpl) node;
                System.out.println(indent + terminal.getText());
            }
        } else {
            System.out.println(indent + "(" + node.getClass().getSimpleName());
            for (int i = 0; i < node.getChildCount(); i++) {
                dumpParsetree(node.getChild(i), level + 1);
            }
            System.out.println(indent + ")");
        }
    }

    static void prettyPrint(ParseTree node) {
        if (node instanceof TerminalNode) {
            if (node instanceof TerminalNodeImpl) {
                TerminalNodeImpl terminal = (TerminalNodeImpl) node;
                System.out.print(terminal.getText());
            }
        } else {
            String name = node.getClass().getSimpleName();
            boolean print = !name.equals("Expr0Context") && !name.equals("ConstantContext") && !name.equals("LvalueContext") && node.getChildCount() > 1;
            if (print)
                System.out.print("(");
            for (int i = 0; i < node.getChildCount(); i++) {
                prettyPrint(node.getChild(i));
            }
            if (print)
                System.out.print(")");
        }
    }

    private static String repeat(String s, int n) {
        return new String(new char[n]).replace("\0", s);
    }
}
