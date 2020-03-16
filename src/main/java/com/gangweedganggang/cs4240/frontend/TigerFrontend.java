package com.gangweedganggang.cs4240.frontend;

import com.gangweedganggang.cs4240.ast.*;
import com.gangweedganggang.cs4240.flowgraph.edges.ConditionalJumpEdge;
import com.gangweedganggang.cs4240.flowgraph.edges.ImmediateEdge;
import com.gangweedganggang.cs4240.flowgraph.edges.UnconditionalJumpEdge;
import com.gangweedganggang.cs4240.ir.IRBasicBlock;
import com.gangweedganggang.cs4240.ir.IRControlFlowGraph;
import com.gangweedganggang.cs4240.ir.IRLocal;
import com.gangweedganggang.cs4240.ir.stmts.*;
import com.gangweedganggang.cs4240.stdlib.util.Pair;
import com.gangweedganggang.cs4240.stdlib.util.TabbedStringWriter;

import java.util.*;

/**
 * Basically recycled gwcc LOL
 * https://github.com/gt-retro-computing/gwcc/blob/master/gwcc/c_frontend.py
 */
public class TigerFrontend {
    private IScope currentScope;

    // bad language gets a bad compiler. NESTING AHAHHAAHAHAH SO CLEVER!!!!!
    private class FunctionContext {
        public TigerFunction function;
        public IRBasicBlock currentBlock;
        public Deque<Pair<IRBasicBlock, IRBasicBlock>> loopStack;

        public FunctionContext(TigerFunction function) {
            this.function = function;
            currentBlock = function.getCfg().getEntry();
            loopStack = new ArrayDeque<>();
        }
    }
    private final ArrayDeque<FunctionContext> ctx;

    // Results
    private final List<TigerFunction> funcs;
    private final List<TigerVariable> vars;

    public TigerFrontend() {
        currentScope = new TigerScope(++scopeNum, new RootScope());
        funcs = new ArrayList<>();
        vars = new ArrayList<>();
        ctx = new ArrayDeque<>();
    }

    private TigerFunction currentFunction() {
        if (ctx.isEmpty())
            throw new IllegalStateException();
        return ctx.peek().function;
    }

    private IRBasicBlock currentBlock() {
        if (ctx.isEmpty())
            throw new IllegalStateException();
        return ctx.peek().currentBlock;
    }

    private void setBlock(IRBasicBlock next) {
        if (ctx.isEmpty())
            throw new IllegalStateException();
        ctx.peek().currentBlock = next;
    }

    private Deque<Pair<IRBasicBlock, IRBasicBlock>> loopStack() {
        if (ctx.isEmpty())
            throw new IllegalStateException();
        return ctx.peek().loopStack;
    }

    // Functions. pair of <scope number, func>
    public List<TigerFunction> getFuncs() {
        return Collections.unmodifiableList(funcs);
    }

    // Global variables. pair of <scope number, var>
    public List<TigerVariable> getVars() {
        return Collections.unmodifiableList(vars);
    }

    public void generateIR(ASTRoot root) {
        sw = new TabbedStringWriter();
        scopeNum = 0;
        TigerFunction mainFunc = new TigerFunction(currentScope, "!main", new ArrayList<>(), new FunctionSymbolType(PrimitiveSymbolType.INT, new ArrayList<>()));
        funcs.add(mainFunc);
        ctx.push(new FunctionContext(mainFunc));
        onLetStmt(root.getRoot());
        addStmt(new MainReturnStmt());
        sw.newline();
    }

    // bad language gets a bad compiler.
    int scopeNum;
    private TabbedStringWriter sw;
    public String dumpSymbolTable() {
        return sw.toString();
    }

    private IRLocal newLocal(SymbolType type) {
        return currentFunction().getLocals().getNextFree(type);
    }

    private void addStmt(IRStmt stmt) {
        currentBlock().add(stmt);
        if (stmt instanceof GotoStmt) {
            currentFunction().getCfg().addEdge(new UnconditionalJumpEdge<>(currentBlock(), ((GotoStmt) stmt).getDst()));
        } else if (stmt instanceof CondBranchStmt) {
            final CondBranchStmt condbr = (CondBranchStmt) stmt;
            currentFunction().getCfg().addEdge(new ConditionalJumpEdge<>(currentBlock(), condbr.getTrueBlock()));
            currentFunction().getCfg().addEdge(new ImmediateEdge<>(currentBlock(), condbr.getFalseBlock()));
        } else if (stmt instanceof ReturnStmt) {
            setBlock(currentFunction().getCfg().newBlock());
        }
    }

    private IRLocal onLvalue(ASTLvalue lvalue) {
        TigerVariable var = currentScope.lookupVariable(lvalue.getIdentifier());
        if (var == null)
            throw new CompilationError(lvalue.row, lvalue.col, "undefined variable " + lvalue.getIdentifier());
        PrimitiveSymbolType primVarType = var.type.getPrimitiveType();
        IRLocal base;
        if (currentFunction().paramLocals.containsKey(var)) {
            base = currentFunction().paramLocals.get(var);
        } else {
            base = newLocal(var.type);
            addStmt(new VarLoadStmt(base, var));
        }
        if (lvalue.getIndex() != null) { // array
            if (!primVarType.isArray) {
                throw new CompilationError(lvalue.row, lvalue.col, "trying to subscript variable of non-array type");
            }
            IRLocal elem = newLocal(primVarType.getArrayType());
            IRLocal index = onExpr(lvalue.getIndex());
            if (!index.getType().equals(PrimitiveSymbolType.INT))
                throw new CompilationError(lvalue.row, lvalue.col, "you have to index the array as int :)");
            addStmt(new ArrayLoadStmt(elem, base, index));
            return elem;
        } else {
            return base;
        }
    }

    private boolean canCoerceTypes(SymbolType lhs, SymbolType rhs) {
        PrimitiveSymbolType lhsPrim = lhs.getPrimitiveType();
        PrimitiveSymbolType rhsPrim = rhs.getPrimitiveType();
        boolean lhsIsPrim = lhs == lhsPrim;
        boolean rhsIsPrim = rhs == rhsPrim;

        if (lhs == PrimitiveSymbolType.FLOAT && rhs == PrimitiveSymbolType.INT)
            return true; // PRIMITIVE int can promote to PRIMITIVE float
        else if (lhsIsPrim || rhsIsPrim) // ??? you're allowed to coerce primitive with derived types?
            return lhsPrim.equals(rhsPrim);
        else
            return lhs.equals(rhs);
    }

    private IRLocal checkCast(AbstractASTNode loc, SymbolType lhs, IRLocal rhs) {
        // if (!canCoerceTypes(lhs, rhs.getType()))
            // throw new CompilationError(loc.row, loc.col, "incompatible types: got " + rhs.getType() + ", expected " + lhs);
        if (!lhs.getPrimitiveType().equals(rhs.getType().getPrimitiveType())) {
            IRLocal casted = newLocal(lhs);
            addStmt(new CastStmt(casted, rhs));
            return casted;
        }
        return rhs;
    }

    private void assignLvalue(ASTLvalue lvalue, IRLocal rhs) {
        TigerVariable var = currentScope.lookupVariable(lvalue.getIdentifier());
        if (var == null)
            throw new CompilationError(lvalue.row, lvalue.col, "undefined variable " + lvalue.getIdentifier());
        PrimitiveSymbolType primVarType = var.type.getPrimitiveType();
        if (primVarType.isArray) {
            if (lvalue.getIndex() != null) { // arrayyyyyyyyyyy
                PrimitiveSymbolType elemType = primVarType.getArrayType();
                rhs = checkCast(lvalue, elemType, rhs);
                IRLocal index = onExpr(lvalue.getIndex());
                IRLocal arr = newLocal(var.type);
                addStmt(new VarLoadStmt(arr, var));
                addStmt(new ArrayStoreStmt(arr, index, rhs));
            } else {
                throw new CompilationError(lvalue.row, lvalue.col, "the pdf says you can't assign arrays");
            }
        } else {
            rhs = checkCast(lvalue, var.type, rhs);
            addStmt(new VarStoreStmt(var, rhs));
        }
    }

    private void onCompound(ASTBlock block) {
        for (AbstractASTNode x : block.getStmts()) {
            AbstractASTStmt stmt = (AbstractASTStmt) x;
            onStmt(stmt);
        }
    }

    private void onStmt(AbstractASTStmt stmt) {
        if (stmt.getCode() != null && !stmt.getCode().isEmpty())
            addStmt(new CommentStmt(stmt.getCode()));
        if (stmt instanceof ASTLetStmt) {
            onLetStmt((ASTLetStmt) stmt);
        } else if (stmt instanceof ASTWhileStmt) {
            onWhileStmt((ASTWhileStmt) stmt);
        } else if (stmt instanceof ASTReturnStmt) {
            onReturnStmt((ASTReturnStmt) stmt);
        } else if (stmt instanceof ASTAssignStmt) {
            onAssignStmt((ASTAssignStmt) stmt);
        } else if (stmt instanceof ASTIfStmt) {
            onIfStmt((ASTIfStmt) stmt);
        } else if (stmt instanceof ASTBreakStmt) {
            onBreakStmt((ASTBreakStmt) stmt);
        } else if (stmt instanceof ASTForStmt) {
            onForStmt((ASTForStmt) stmt);
        } else if (stmt instanceof ASTFuncRcallStmt) {
            onRcallStmt((ASTFuncRcallStmt) stmt);
        } else if (stmt instanceof ASTFuncCallStmt) {
            onCallStmt((ASTFuncCallStmt) stmt);
        } else {
            throw new IllegalStateException("how could this happen to me!");
        }
    }

    private void onBreakStmt(ASTBreakStmt stmt) {
        if (currentFunction() == null)
            throw new CompilationError(stmt.row, stmt.col, "break outside of function");
        if (loopStack().isEmpty())
            throw new CompilationError(stmt.row, stmt.col, "break outside of loop");
        Pair<IRBasicBlock, IRBasicBlock> startEnd = loopStack().peek();
        IRBasicBlock start = startEnd.getKey();
        IRBasicBlock end = startEnd.getValue();
        addStmt(new GotoStmt(end));
        setBlock(currentFunction().getCfg().newBlock());
    }

    // Direct translation of gwcc on_while_node
    // https://github.com/gt-retro-computing/gwcc/blob/master/gwcc/c_frontend.py#L394
    private void onWhileStmt(ASTWhileStmt whileStmt) {
        IRBasicBlock condBlock = currentFunction().getCfg().newBlock(); // block holding loop conditional
        IRBasicBlock stmtBlock = currentFunction().getCfg().newBlock(); // block holding loop body
        IRBasicBlock endBlock = currentFunction().getCfg().newBlock(); // next block after loop
        addStmt(new GotoStmt(condBlock));

        // handle cond
        setBlock(condBlock);
        IRLocal condVar = onExpr(whileStmt.getCondition());
        if (condVar.getType() != PrimitiveSymbolType.INT)
            throw new CompilationError(whileStmt.getCondition().row, whileStmt.getCondition().col, "illegal type " + condVar.getType() + " in while-condition");
        IRLocal constZero = loadConst(PrimitiveSymbolType.INT, 0);
        addStmt(new CondBranchStmt(stmtBlock, endBlock, condVar, constZero, CondBranchStmt.ComparisonOperator.NEQ));

        // handle stmt
        setBlock(stmtBlock);
        loopStack().push(new Pair<>(condBlock, endBlock));
        onCompound(whileStmt.getBlock());
        addStmt(new GotoStmt(condBlock));

        loopStack().pop();
        setBlock(endBlock);
    }

    private void onReturnStmt(ASTReturnStmt ret) {
        IRLocal retval = onExpr(ret.getExpr());
        retval = checkCast(ret, currentFunction().type.retType, retval);
        addStmt(new ReturnStmt(retval));
    }

    private void onAssignStmt(ASTAssignStmt assign) {
        IRLocal rhs = onExpr(assign.getRvalue());
        assignLvalue(assign.getLvalue(), rhs);
    }

    // Direct translation of on_if_node
    // https://github.com/gt-retro-computing/gwcc/blob/master/gwcc/c_frontend.py#L233
    private void onIfStmt(ASTIfStmt ifStmt) {
        // handle cond
        IRLocal condVal = onExpr(ifStmt.getCondition());
        if (condVal.getType() != PrimitiveSymbolType.INT)
            throw new CompilationError(ifStmt.getCondition().row, ifStmt.getCondition().col, "illegal type " + condVal.getType() + " in if-condition");

        // generate control flow
        IRBasicBlock trueBlock = currentFunction().getCfg().newBlock();
        IRBasicBlock endBlock = currentFunction().getCfg().newBlock();
        IRBasicBlock falseBlock;
        if (ifStmt.getElseBlock() != null)
            falseBlock = currentFunction().getCfg().newBlock();
        else
            falseBlock = endBlock;
        addStmt(new CondBranchStmt(trueBlock, falseBlock, condVal, loadConst(PrimitiveSymbolType.INT, 0), CondBranchStmt.ComparisonOperator.NEQ));

        // handle iftrue
        setBlock(trueBlock);
        onCompound(ifStmt.getIfBlock());
        addStmt(new GotoStmt(endBlock));

        // handle iffalse
        if (ifStmt.getElseBlock() != null) {
            setBlock(falseBlock);
            onCompound(ifStmt.getElseBlock());
            addStmt(new GotoStmt(endBlock));
        }

        setBlock(endBlock);
    }

    private void onForStmt(ASTForStmt forStmt) {
        // handle bounds
        onAssignStmt(forStmt.getInitial());
        ASTLvalue lvalue = forStmt.getInitial().getLvalue();
        IRLocal upperBound = onExpr(forStmt.getUpperBound());

        IRBasicBlock condBlock = currentFunction().getCfg().newBlock(); // block holding loop conditional
        IRBasicBlock stmtBlock = currentFunction().getCfg().newBlock(); // block holding loop body
        IRBasicBlock endBlock = currentFunction().getCfg().newBlock(); // next block after loop
        addStmt(new GotoStmt(condBlock));

        // handle cond
        setBlock(condBlock);
        addStmt(new CondBranchStmt(stmtBlock, endBlock, onLvalue(lvalue), upperBound, CondBranchStmt.ComparisonOperator.LEQ));

        // handle stmt
        setBlock(stmtBlock);
        loopStack().push(new Pair<>(condBlock, endBlock));
        onCompound(forStmt.getBlock());
        IRLocal iterVar = onLvalue(lvalue);
        if (iterVar.getType() != PrimitiveSymbolType.INT)
            throw new CompilationError(forStmt.getInitial().row, forStmt.getInitial().col, "illegal type " + iterVar.getType() + " in loop conditional");
        addStmt(new BinOpStmt(iterVar, iterVar, loadConst(PrimitiveSymbolType.INT ,1), BinOpStmt.BinaryOperator.ADD));
        assignLvalue(lvalue, iterVar);
        addStmt(new GotoStmt(condBlock));

        loopStack().pop();
        setBlock(endBlock);
    }

    private void onCallStmt(ASTFuncCallStmt call) {
        ITigerFunction destFunc = currentScope.lookupFunction(call.funcID);
        if (destFunc == null)
            throw new CompilationError(call.row, call.col, "undefined function " + call.funcID);
        FunctionSymbolType funcType = destFunc.getType();
        List<IRLocal> args = new ArrayList<>();
        if (call.args.size() != funcType.paramTypes.size())
            throw new  CompilationError(call.row, call.col, "mismatched argument counts");
        for (int i = 0; i < call.args.size(); i++)
            args.add(checkCast(call, funcType.paramTypes.get(i), onExpr(call.args.get(i))));
        addStmt(new CallStmt(destFunc, args));
    }

    private void onRcallStmt(ASTFuncRcallStmt call) {
        ITigerFunction destFunc = currentScope.lookupFunction(call.funcID);
        if (destFunc == null)
            throw new CompilationError(call.row, call.col, "undefined function " + call.funcID);
        FunctionSymbolType funcType = destFunc.getType();
        List<IRLocal> args = new ArrayList<>();
        if (call.args.size() != funcType.paramTypes.size())
            throw new  CompilationError(call.row, call.col, "mismatched argument counts");
        for (int i = 0; i < call.args.size(); i++)
            args.add(checkCast(call, funcType.paramTypes.get(i), onExpr(call.args.get(i))));
        IRLocal retLocal = newLocal(funcType.retType);
        addStmt(new RcallStmt(retLocal, destFunc, args));
        assignLvalue(call.getLvalue(), retLocal);
    }

    private IRLocal onExpr(ASTExpr expr) {
        if (expr instanceof ASTLvalueExpr) {
            return onLvalue(((ASTLvalueExpr) expr).getLvalue());
        } else if (expr instanceof ASTConstExpr) {
            return onConstExpr((ASTConstExpr) expr);
        } else if (expr instanceof ASTBinOpExpr) {
            return onBinOpExpr((ASTBinOpExpr) expr);
        } else {
            throw new IllegalArgumentException("oh my god children are suffering because of you");
        }
    }

    private IRLocal onBinOpExpr(ASTBinOpExpr binop) {
        if (binop.operation.isComparison()) {
            if (binop.getOperand1() instanceof ASTBinOpExpr && ((ASTBinOpExpr) binop.getOperand1()).operation.isComparison()
                    || binop.getOperand2() instanceof ASTBinOpExpr && ((ASTBinOpExpr) binop.getOperand2()).operation.isComparison())
                throw new CompilationError(binop.row, binop.col, "syntax error: comparison operators cannot associate");
        }

        // ummmm, order of execution is UB?? :joy:
        IRLocal a = onExpr(binop.getOperand1());
        IRLocal b = onExpr(binop.getOperand2());

        PrimitiveSymbolType primA = a.getType().getPrimitiveType();
        PrimitiveSymbolType primB = b.getType().getPrimitiveType();
        boolean aIsPrim = a.getType() == primA;
        boolean bIsPrim = b.getType() == primB;

        if (primA.isArray || primB.isArray)
            throw new CompilationError(binop.row, binop.col, "illegal array operand to binary operator");

        SymbolType resultType;
        switch (binop.operation) {
        case Eq:
        case Ne:
        case Lt:
        case Gt:
        case Le:
        case Ge:
            if (!a.getType().equals(b.getType()))
                throw new CompilationError(binop.row, binop.col, "incompatible types " + a.getType() + " and " + b.getType());
            resultType = PrimitiveSymbolType.INT;
            break;
        case Pow:
            if (primB != PrimitiveSymbolType.INT)
                throw new CompilationError(binop.row, binop.col, "power must be integer");
            if (!aIsPrim || !bIsPrim)
                if (!a.getType().equals(b.getType()))
                    throw new CompilationError(binop.row, binop.col, "incompatible types " + a.getType() + " and " + b.getType());
            resultType = a.getType();
            break;
        case And:
        case Or:
            if (primA == PrimitiveSymbolType.FLOAT || primB == PrimitiveSymbolType.FLOAT)
                throw new CompilationError(binop.row, binop.col, "invalid operand to comparison operator");
            // fallthrough
        default:
            if (a.getType() == PrimitiveSymbolType.FLOAT && b.getType() == PrimitiveSymbolType.INT
                    || b.getType() == PrimitiveSymbolType.FLOAT && a.getType() == PrimitiveSymbolType.INT)
                resultType = PrimitiveSymbolType.FLOAT; // promote
            else
                resultType = a.getType();
            b = checkCast(binop, resultType, b);
            a = checkCast(binop, resultType, a);
            break;
        }

        IRLocal result = newLocal(resultType);

        switch (binop.operation) {
            case Add:
            case Sub:
            case Mul:
            case Div:
            case Pow:
            case And:
            case Or:
                // arithmetical
                addStmt(new BinOpStmt(result, a, b, BinOpStmt.BinaryOperator.valueOf(binop.operation)));
                break;
            case Eq:
            case Ne:
            case Lt:
            case Gt:
            case Le:
            case Ge:
                // Lmao
                IRControlFlowGraph cfg = currentFunction().getCfg();
                IRBasicBlock trueBlock = cfg.newBlock();
                IRBasicBlock falseBlock = cfg.newBlock();
                IRBasicBlock endBlock = cfg.newBlock();
                addStmt(new CondBranchStmt(trueBlock, falseBlock, a, b, CondBranchStmt.ComparisonOperator.valueOf(binop.operation)));
                setBlock(trueBlock);
                addStmt(new ConstLoadStmt(result, 1));
                addStmt(new GotoStmt(endBlock));
                setBlock(falseBlock);
                addStmt(new ConstLoadStmt(result, 0));
                addStmt(new GotoStmt(endBlock));
                setBlock(endBlock);
                break;
        }

        return result;
    }

    private IRLocal onConstExpr(ASTConstExpr constExpr) {
        return loadConst(PrimitiveSymbolType.valueOf(constExpr.type), constExpr.get());
    }

    private IRLocal loadConst(PrimitiveSymbolType type, Object value) {
        if (type.equals(PrimitiveSymbolType.INT)) // java is a GOOD LANGUAGE
            if (value instanceof Float)
                value = (int)(float)(Float)value;
        if (type.equals(PrimitiveSymbolType.FLOAT))
            if (value instanceof Integer)
                value = (float)(int)value;
        IRLocal result = newLocal(type);
        addStmt(new ConstLoadStmt(result, value));
        return result;
    }

    private void enterScope() {
        currentScope = new TigerScope(++scopeNum, currentScope);
        sw.newline().print("Scope " + currentScope.getID()).tab();
    }

    private void leaveScope() {
        sw.untab();
        currentScope = currentScope.getParent();
    }

    private void onTypeDecl(ASTTypeDecl decl) {
        String name = decl.identifier;
        if (currentScope.getSymbol(name) != null) {
            throw new CompilationError(decl.row, decl.col, "redefinition of name " + name);
        }

        SymbolType basetype;
        if (decl.definition != null) {
            basetype = currentScope.resolveType(decl.definition);
        } else if (decl.basetype != null) {
            basetype = PrimitiveSymbolType.valueOf(decl.basetype);
            if (decl.isArray)
                basetype = ((PrimitiveSymbolType) basetype).arrayOfSize(decl.arraysize);
        } else {
            throw new IllegalStateException("how could this happen to me");
        }
        if (basetype == null)
            throw new CompilationError(decl.row, decl.col, "undefined type " + decl.definition);

        sw.newline().print(name + ", type, " + basetype);
        TypedefSymbolType typedef = new TypedefSymbolType(name, basetype);
        currentScope.addTypedef(typedef);
    }

    private void onVarDecl(ASTVarDecl decl) {
        SymbolType resolvedType = currentScope.resolveType(decl.type);
        if (resolvedType == null)
            throw new CompilationError(decl.row, decl.col, "undefined type " + decl.type);
        if (resolvedType instanceof PrimitiveSymbolType && ((PrimitiveSymbolType) resolvedType).isArray)
            throw new CompilationError(decl.row, decl.col, "in section 4.9, the pdf says you can't declare arrays without defining an array type first.");
        for (String name : decl.identifiers) {
            sw.newline().print(name + ", var, " + resolvedType);
            TigerVariable var;
            if (isInMain()) {
                // only vars declared in main are global (lol)
                var = new TigerVariable(currentScope, name, resolvedType, TigerVariable.StorageClass.GLOBAL);
                vars.add(var);
            } else {
                var = new TigerVariable(currentScope, name, resolvedType, TigerVariable.StorageClass.LOCAL);
            }
            if (decl.getInitializer() != null) {
                Object initialValue = decl.getInitializer().get();
                PrimitiveSymbolType primType = resolvedType.getPrimitiveType();
                if (primType.isArray) {
                    // array assignment to all elements initializer
                    IRLocal arr = newLocal(var.type);
                    addStmt(new VarLoadStmt(arr, var));
                    IRLocal constVal = loadConst(primType.getArrayType(), initialValue);
                    addStmt(new ArraySetStmt(arr, primType.arraysize, constVal));
                } else {
                    addStmt(new VarStoreStmt(var, loadConst(primType, initialValue)));
                }
            }
            currentScope.addVariable(var);
        }
    }

    private boolean isInMain() {
        assert currentFunction().name.equals("!main") == (ctx.size() == 1);
        return ctx.size() == 1;
    }

    private void onFuncDecl(ASTFuncDecl decl) {
        if (!isInMain())
            throw new CompilationError(decl.row, decl.col, "You promised on piazza question number 237 that you wouldn't nest functions like this!!!!");
        List<SymbolType> paramTypes = new ArrayList<>(decl.paramTypes.size());
        for (String paramType : decl.paramTypes) {
            SymbolType resolvedType = currentScope.resolveType(paramType);
            if (resolvedType == null)
                throw new CompilationError(decl.row, decl.col, "undefined type " + paramType);
            paramTypes.add(resolvedType);
        }

        List<TigerVariable> params = new ArrayList<>(decl.params.size());
        for (int i = 0; i < decl.params.size(); i++) {
            params.add(new TigerVariable(currentScope, decl.params.get(i), paramTypes.get(i), TigerVariable.StorageClass.LOCAL));
        }

        SymbolType returnType = currentScope.resolveType(decl.retType);
        if (returnType == null)
            throw new CompilationError(decl.row, decl.col, "undefined type " + decl.retType);
        sw.newline().print(decl.name + ", func, " + returnType);

        FunctionSymbolType prototype = new FunctionSymbolType(returnType, paramTypes);
        TigerFunction func = new TigerFunction(currentScope, decl.name, params, prototype);
        currentScope.addFunction(func);
        funcs.add(func);

        currentScope = new TigerScope(0, currentScope);
        for (int i = 0; i < decl.params.size(); i++) {
            currentScope.addVariable(params.get(i));
        }
        ctx.push(new FunctionContext(func));
        onCompound(decl.getBody());
        ctx.pop();
        currentScope = currentScope.getParent();

        for (IRBasicBlock bb : func.getCfg().verticesInOrder()) {
            if (func.getCfg().getEdges(bb).size() == 0) {
                if (bb.isEmpty() || !(bb.get(bb.size() - 1) instanceof ReturnStmt)) {
                    throw new CompilationError(decl.row, decl.col, "control flow falls off the end of the function");
                }
            }
        }
    }

    private void onLetStmt(ASTLetStmt scopeyBoi) {
        enterScope();
        Arrays.stream(scopeyBoi.getDecls().getTypeDecls().getDecls()).map(ASTTypeDecl.class::cast).forEach(this::onTypeDecl);
        Arrays.stream(scopeyBoi.getDecls().getVarDecls().getDecls()).map(ASTVarDecl.class::cast).forEach(this::onVarDecl);
        Arrays.stream(scopeyBoi.getDecls().getFuncDecls().getDecls()).map(ASTFuncDecl.class::cast).forEach(this::onFuncDecl);
        onCompound(scopeyBoi.getBody());
        leaveScope();
    }
}
