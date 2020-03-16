package com.gangweedganggang.cs4240.frontend;

import com.gangweedganggang.cs4240.TigerBaseVisitor;
import com.gangweedganggang.cs4240.TigerParser;
import com.gangweedganggang.cs4240.ast.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TigerParseTreeVisitor extends TigerBaseVisitor<Object> {

    /*
     ****************** Expression *********************
     */

    @Override
    public List<ASTExpr> visitExprList(TigerParser.ExprListContext ctx) {
        List<ASTExpr> exprs = new ArrayList<>(ctx.expr().size());
        for (TigerParser.ExprContext expr : ctx.expr()) {
            exprs.add(visitExpr(expr));
        }
        return exprs;
    }

    @Override
    public ASTExpr visitExpr(TigerParser.ExprContext ctx) {
        return visitOrTerm(ctx.orTerm());
    }

    // 1 || 2 || 3
    @Override
    public ASTExpr visitOrTerm(TigerParser.OrTermContext ctx) {
        return leftReassociateArithmetic(ctx, TigerBinOp.And, TigerParser.AndTermContext.class, TigerBinOp.Or);
    }

    @Override
    public ASTExpr visitAndTerm(TigerParser.AndTermContext ctx) {
        return leftReassociateArithmetic(ctx, TigerBinOp.Le, TigerParser.LeTermContext.class, TigerBinOp.And);
    }

    @Override
    public ASTExpr visitLeTerm(TigerParser.LeTermContext ctx) {
        return leftReassociateArithmetic(ctx, TigerBinOp.Ge, TigerParser.GeTermContext.class, TigerBinOp.Le);
    }

    @Override
    public ASTExpr visitGeTerm(TigerParser.GeTermContext ctx) {
        return leftReassociateArithmetic(ctx, TigerBinOp.Lt, TigerParser.LtTermContext.class, TigerBinOp.Ge);
    }

    @Override
    public ASTExpr visitLtTerm(TigerParser.LtTermContext ctx) {
        return leftReassociateArithmetic(ctx, TigerBinOp.Gt, TigerParser.GtTermContext.class, TigerBinOp.Lt);
    }

    @Override
    public ASTExpr visitGtTerm(TigerParser.GtTermContext ctx) {
        return leftReassociateArithmetic(ctx, TigerBinOp.Ne, TigerParser.NeTermContext.class, TigerBinOp.Gt);
    }

    @Override
    public ASTExpr visitNeTerm(TigerParser.NeTermContext ctx) {
        return leftReassociateArithmetic(ctx, TigerBinOp.Eq, TigerParser.EqTermContext.class, TigerBinOp.Ne);
    }

    @Override
    public ASTExpr visitEqTerm(TigerParser.EqTermContext ctx) {
        return leftReassociateArithmetic(ctx, TigerBinOp.Sub, TigerParser.SubTermContext.class, TigerBinOp.Eq);
    }

    @Override
    public ASTExpr visitSubTerm(TigerParser.SubTermContext ctx) {
        return leftReassociateArithmetic(ctx, TigerBinOp.Add, TigerParser.AddTermContext.class, TigerBinOp.Sub);
    }


    @Override
    public ASTExpr visitAddTerm(TigerParser.AddTermContext ctx) {
        return leftReassociateArithmetic(ctx, TigerBinOp.Div, TigerParser.DivTermContext.class, TigerBinOp.Add);
    }

    @Override
    public ASTExpr visitDivTerm(TigerParser.DivTermContext ctx) {
        return leftReassociateArithmetic(ctx, TigerBinOp.Mul, TigerParser.MulTermContext.class, TigerBinOp.Div);
    }

    @Override
    public ASTExpr visitMulTerm(TigerParser.MulTermContext ctx) {
        return leftReassociateArithmetic(ctx, TigerBinOp.Pow, TigerParser.PowTermContext.class, TigerBinOp.Mul);
    }

    @Override
    public ASTExpr visitPowTerm(TigerParser.PowTermContext ctx) {
        List<TigerParser.ParnTermContext> terms = ctx.parnTerm();
        if (terms.size() == 1) {
            return visitParnTerm(terms.get(0));
        } else if (terms.size() > 1) {
            ASTExpr currentNode = visitParnTerm(terms.get(terms.size() - 1));
            for (int i = terms.size() - 2; i >= 0; i--) {
                ASTExpr newNode = visitParnTerm(terms.get(i));
                currentNode = new ASTBinOpExpr(
                        currentNode.row,
                        currentNode.col,
                        TigerBinOp.Pow,
                        newNode,
                        currentNode
                );
            }
            return currentNode;
        }
        throw new IllegalStateException("Unreachable");
    }

    @Override
    public ASTConstExpr visitConstant(TigerParser.ConstantContext ctx) {
        TigerParser.ConstantContext constCtx = ctx;
            if (constCtx.FloatLit() != null) {
                TerminalNode floatLit = constCtx.FloatLit();
                return new ASTConstExpr(
                        floatLit.getSymbol().getLine(),
                        floatLit.getSymbol().getCharPositionInLine(),
                        floatLit.getText(),
                        TigerType.FLOAT);
            } else if (constCtx.IntLit() != null) {
                TerminalNode intLit = constCtx.IntLit();
                return new ASTConstExpr(
                        intLit.getSymbol().getLine(),
                        intLit.getSymbol().getCharPositionInLine(),
                        intLit.getText(),
                        TigerType.INT);
            } else throw new IllegalArgumentException("omg please no not ther children!!!");
    }

    @Override
    public ASTRoot visitTigerProgram(TigerParser.TigerProgramContext ctx) {
        return new ASTRoot(ctx.KeywordMain(), visitDeclSegment(ctx.declSegment()), visitStmtList(ctx.stmtList()));
    }

    @Override
    public ASTExpr visitParnTerm(TigerParser.ParnTermContext ctx) {
        if (ctx.constant() != null) { // Constant Case: TigerASTConstExpr
            TigerParser.ConstantContext constCtx = ctx.constant();
            if (constCtx.FloatLit() != null) {
                TerminalNode floatLit = constCtx.FloatLit();
                return new ASTConstExpr(
                        floatLit.getSymbol().getLine(),
                        floatLit.getSymbol().getCharPositionInLine(),
                        floatLit.getText(),
                        TigerType.FLOAT);
            } else if (constCtx.IntLit() != null) {
                TerminalNode intLit = constCtx.IntLit();
                return new ASTConstExpr(
                        intLit.getSymbol().getLine(),
                        intLit.getSymbol().getCharPositionInLine(),
                        intLit.getText(),
                        TigerType.INT);
            }
        } else if (ctx.expr() != null) {
            return visitExpr(ctx.expr());
        } else if (ctx.lvalue() != null) {
            return new ASTLvalueExpr(visitLvalue(ctx.lvalue()));
        }
        throw new IllegalStateException("Unreachable");
    }

    @Override
    public ASTLvalue visitLvalue(TigerParser.LvalueContext ctx) {
        assert ctx.ID() != null;
        TerminalNode ID = ctx.ID();
        ASTExpr index = null;
        if (ctx.expr() != null) {
            // Array Index Mode
            index = visitExpr(ctx.expr());
            assert index != null;
        }
        return new ASTLvalue(
                ID.getSymbol().getLine(),
                ID.getSymbol().getCharPositionInLine(),
                ID.getText(),
                index
        );
    }

    /*
     ****************** RVALUE *********************
     */

    @Override
    public ASTExpr visitRValue(TigerParser.RValueContext ctx) {
        return visitExpr(ctx.expr());
    }

    /*
     ****************** Statements *********************
     */

    @Override
    public ASTBlock visitStmtList(TigerParser.StmtListContext ctx) {
        if (ctx == null) {
            return null;
        }
        List<AbstractASTStmt> stmts = new ArrayList<>(ctx.stmt().size());
        for (TigerParser.StmtContext stmt : ctx.stmt()) {
            stmts.add(visitStmt(stmt));
        }
        return new ASTBlock(
            0, 0,
            stmts
        );
    }

    @Override
    public ASTWhileStmt visitWhileStmt(TigerParser.WhileStmtContext ctx) {
        TerminalNode kwdWhile = ctx.KeywordWhile();
        return new ASTWhileStmt(
                kwdWhile,
                visitExpr(ctx.expr()),
                visitStmtList(ctx.stmtList())
        );
    }

    @Override
    public ASTFuncRcallStmt visitRcallStmt(TigerParser.RcallStmtContext ctx) {
        TerminalNode lvalueID = ctx.ID(0);
        assert lvalueID != null;
        TerminalNode functionNameID = ctx.ID(1);
        assert functionNameID != null;

        return new ASTFuncRcallStmt(
                functionNameID,
                lvalueID,
                functionNameID.getText(),
                visitExprList(ctx.exprList())
        );
    }

    @Override
    public ASTFuncCallStmt visitCallStmt(TigerParser.CallStmtContext ctx) {
        TerminalNode functionNameID = ctx.ID();
        assert functionNameID != null;

        return new ASTFuncCallStmt(
                functionNameID,
                functionNameID.getText(),
                visitExprList(ctx.exprList())
        );
    }

    @Override
    public TigerType visitTypeId(TigerParser.TypeIdContext ctx) {
        if (ctx.KeywordInt() != null) return TigerType.INT;
        else if (ctx.KeywordFloat() != null) return TigerType.FLOAT;
        else throw new IllegalArgumentException("OH GOD WONT YOU THINK OF THE CHILDREN!>?!?!?!?!?!!!!!");
    }

    @Override
    public ASTTypeDecl visitTypeDecl(TigerParser.TypeDeclContext ctx) {
        if (ctx.type().ID() != null)
            return new ASTTypeDecl(ctx.KeywordType(), ctx.ID().getText(), ctx.type().ID().getText());
        else if (ctx.type().KeywordArray() != null)
            return new ASTTypeDecl(ctx.KeywordType(), ctx.ID().getText(), visitTypeId(ctx.type().typeId()), Integer.parseInt(ctx.type().IntLit().getText()));
        else if (ctx.type().typeId() != null)
            return new ASTTypeDecl(ctx.KeywordType(), ctx.ID().getText(), visitTypeId(ctx.type().typeId()));
        else throw new IllegalArgumentException("OH GOD WONT YOU THINK OF THE CHILDREN2!>?!?!?!?!?!!!!!");
    }

    @Override
    public ASTVarDecl visitVarDecl(TigerParser.VarDeclContext ctx) {
        List<String> ids = new ArrayList<>(ctx.idList().ID().size());
        for (TerminalNode t : ctx.idList().ID()) {
            ids.add(t.getText());
        }

        return new ASTVarDecl(
                ctx.KeywordVar(),
                ids,
                ctx.type().getText(),
                ctx.optionalInit().OpAssign() != null ? visitConstant(ctx.optionalInit().constant()) : null
        );
    }

    @Override
    public ASTTypeDeclList visitTypeDeclList(TigerParser.TypeDeclListContext ctx) {
        if (ctx == null) {
            return null;
        }
        List<ASTTypeDecl> decls = new ArrayList<>(ctx.typeDecl().size());
        for (TigerParser.TypeDeclContext decl : ctx.typeDecl()) {
            decls.add(visitTypeDecl(decl));
        }
        return new ASTTypeDeclList(
            0, 0,
            decls
        );
    }

    @Override
    public ASTVarDeclList visitVarDeclList(TigerParser.VarDeclListContext ctx) {
        if (ctx == null) {
            return null;
        }
        List<ASTVarDecl> decls = new ArrayList<>(ctx.varDecl().size());
        for (TigerParser.VarDeclContext decl : ctx.varDecl()) {
            decls.add(visitVarDecl(decl));
        }
        return new ASTVarDeclList(
            0, 0,
            decls
        );
    }

    @Override
    public ASTFuncDecl visitFuncDecl(TigerParser.FuncDeclContext ctx) {
        List<String> params = new ArrayList<>(ctx.paramList().param().size());
        List<String> paramTypes = new ArrayList<>(ctx.paramList().param().size());
        for (TigerParser.ParamContext param : ctx.paramList().param()) {
            params.add(param.ID().getText());
            paramTypes.add(param.type().getText());
        }
        return new ASTFuncDecl(ctx.KeywordFunction(),
                ctx.retType().type().getText(),
                ctx.ID().getText(),
                params,
                paramTypes,
                visitStmtList(ctx.stmtList())
        );
    }

    @Override
    public ASTFuncDeclList visitFuncDeclList(TigerParser.FuncDeclListContext ctx) {
        if (ctx == null) {
            return null;
        }
        List<ASTFuncDecl> decls = new ArrayList<>(ctx.funcDecl().size());
        for (TigerParser.FuncDeclContext decl : ctx.funcDecl()) {
            decls.add(visitFuncDecl(decl));
        }
        return new ASTFuncDeclList(
            0, 0,
            decls
        );
    }

    @Override
    public ASTDeclSegment visitDeclSegment(TigerParser.DeclSegmentContext ctx) {
        return new ASTDeclSegment(0, 0,
                visitTypeDeclList(ctx.typeDeclList()),
                visitVarDeclList(ctx.varDeclList()),
                visitFuncDeclList(ctx.funcDeclList())
        );
    }

    @Override
    public ASTLetStmt visitLetStmt(TigerParser.LetStmtContext ctx) {
        return new ASTLetStmt(ctx.KeywordLet(), visitDeclSegment(ctx.declSegment()), visitStmtList(ctx.stmtList()));
    }


    @Override
    public AbstractASTStmt visitStmt(TigerParser.StmtContext ctx) {
        if (ctx.ifStmt() != null) { // IF Stmt
            return visitIfStmt(ctx.ifStmt());
        } else if (ctx.assignStmt() != null) { // Assign
            return visitAssignStmt(ctx.assignStmt());
        } else if (ctx.callStmt() != null) { // Call
            return visitCallStmt(ctx.callStmt());
        } else if (ctx.rcallStmt() != null) { // Call
            return visitRcallStmt(ctx.rcallStmt());
        } else if (ctx.whileStmt() != null) { // While
            return visitWhileStmt(ctx.whileStmt());
        } else if (ctx.breakStmt() != null) {
            return visitBreakStmt(ctx.breakStmt());
        } else if (ctx.returnStmt() != null) {
            return visitReturnStmt(ctx.returnStmt());
        } else if (ctx.forStmt() != null) {
            return visitForStmt(ctx.forStmt());
        } else if (ctx.letStmt() != null)
            return visitLetStmt(ctx.letStmt());
        throw new IllegalStateException("Unreachable STMT LMAO");
    }

    @Override
    public ASTForStmt visitForStmt(TigerParser.ForStmtContext ctx) {
        TerminalNode forKwd = ctx.KeywordFor();
        TerminalNode assignID = ctx.ID();
        return new ASTForStmt(
                forKwd,
                new ASTAssignStmt(assignID,
                        visitExpr(ctx.expr(0))
                ),
                visitExpr(ctx.expr(1)),
                visitStmtList(ctx.stmtList())
        );
    }

    @Override
    public ASTReturnStmt visitReturnStmt(TigerParser.ReturnStmtContext ctx) {
        return new ASTReturnStmt(ctx.KeywordReturn(), visitExpr(ctx.expr()));
    }

    @Override
    public ASTBreakStmt visitBreakStmt(TigerParser.BreakStmtContext ctx) {
        return new ASTBreakStmt(ctx.KeywordBreak());
    }

    @Override
    public ASTAssignStmt visitAssignStmt(TigerParser.AssignStmtContext ctx) {
        TerminalNode opAssign = ctx.OpAssign();
        return new ASTAssignStmt(
                opAssign,
                visitLvalue(ctx.lvalue()),
                visitRValue(ctx.rValue())
        );
    }

    @Override
    public ASTIfStmt visitIfStmt(TigerParser.IfStmtContext ctx) {
        TigerParser.IfStmtTailContext tail = ctx.ifStmtTail();
        ASTExpr cond = visitExpr(ctx.expr());
        ASTBlock ifBlock = visitStmtList(ctx.stmtList());
        ASTBlock elseBlock = visitStmtList(tail.stmtList());
        TerminalNode keyWrdIf = ctx.KeywordIf();

        return new ASTIfStmt(
                keyWrdIf,
                cond,
                ifBlock,
                elseBlock
        );
    }

    @SuppressWarnings("unchecked")
    public ASTExpr leftReassociateArithmetic(ParserRuleContext ctx, TigerBinOp binOp, Class nextCtx, TigerBinOp currentOp) {
        String op = binOp.toString();
        Class clazz = ctx.getClass();
        try {
            Method visitMethod =
                    TigerParseTreeVisitor.class
                            .getDeclaredMethod("visit" + binOp.name() + "Term", nextCtx);
            Method getTermsMethod = clazz.getDeclaredMethod(op + "Term");
            Object yeet = getTermsMethod.invoke(ctx);
            if (yeet instanceof List) {
                List<Object> terms = (List<Object>) yeet;
                if (terms.size() == 1) {
                    return (ASTExpr) visitMethod.invoke(this, terms.get(0));
                } else if (terms.size() > 1) {
                    ASTExpr currentNode = (ASTExpr) visitMethod.invoke(this, terms.get(0));
                    for (int i = 1; i < terms.size(); i++) {
                        ASTExpr node = (ASTExpr) visitMethod.invoke(this, terms.get(i));
                        currentNode = new ASTBinOpExpr(
                                currentNode.row,
                                currentNode.col,
                                currentOp,
                                currentNode,
                                node);
                    }
                    return currentNode;
                }
            }
        } catch (ReflectiveOperationException e) {
            System.out.print(e.getMessage());
            e.printStackTrace();
            throw new IllegalStateException("Whoops");
        }
        throw new IllegalStateException("Whoops");
    }

}
