package com.gangweedganggang.cs4240.frontend;

import com.gangweedganggang.cs4240.TigerLexer;
import com.gangweedganggang.cs4240.TigerParser;
import com.gangweedganggang.cs4240.ast.ASTRoot;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

public class FrontendParser {
    private static class ThrowingErrorListener extends BaseErrorListener {
        boolean errorFlag = false;
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
                throws ParseCancellationException {
            errorFlag = true;
            throw new ParseCancellationException(e);
        }
    }

    public ASTRoot parse(CharStream stream) {
        ThrowingErrorListener err = new ThrowingErrorListener();
        TigerLexer lexer = new TigerLexer(stream);
        lexer.addErrorListener(err);
        TigerParser parser = new TigerParser(new CommonTokenStream(lexer));
        parser.addErrorListener(err);
        try {
            ASTRoot ast = new TigerParseTreeVisitor().visitTigerProgram(parser.tigerProgram());
            if (err.errorFlag) {
                return null;
            }
            return ast;
        } catch (ParseCancellationException e) {
            return null;
        }
    }
}
