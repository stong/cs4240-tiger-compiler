package com.gangweedganggang.cs4240.frontend;

import com.gangweedganggang.cs4240.ir.IRBasicBlock;
import com.gangweedganggang.cs4240.ir.IRControlFlowGraph;
import com.gangweedganggang.cs4240.ir.IRLocal;
import com.gangweedganggang.cs4240.ir.stmts.ParamStmt;

import java.util.*;

public class TigerFunction implements ITigerFunction {
    public final IScope parent;
    public final String name;
    private final List<TigerVariable> params;
    public final FunctionSymbolType type;
    private final IRControlFlowGraph cfg;
    private final LocalsPool localsPool;
    public final Map<TigerVariable, IRLocal> paramLocals;

    public TigerFunction(IScope parent, String name, List<TigerVariable> params, FunctionSymbolType type) {
        this.parent = parent;
        this.name = name;
        this.params = params;
        this.type = type;
        cfg = new IRControlFlowGraph();
        localsPool = new LocalsPool();

        IRBasicBlock entry = cfg.newBlock();
        cfg.getEntries().add(entry);

        paramLocals = new HashMap<>();
        // Add synthetic copies
        for (int i = 0; i < params.size(); i++) {
            IRLocal paramLocal = localsPool.getNextFree(type.paramTypes.get(i));
            paramLocals.put(params.get(i), paramLocal);
            entry.add(new ParamStmt(paramLocal, params.get(i), i));
        }
    }

    public IRControlFlowGraph getCfg() {
        return cfg;
    }

    public List<TigerVariable> getParams() {
        return Collections.unmodifiableList(params);
    }

    public LocalsPool getLocals() {
        return localsPool;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(type.retType + " " + parent.getID() + "$" + name + "(");
        for (int i = 0; i < params.size(); i++) {
            result.append(type.paramTypes.get(i)).append(" ").append(params.get(i));
            if (i < params.size() - 1) result.append(", ");
        }
        result.append(")");
        return result.toString();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public FunctionSymbolType getType() {
        return type;
    }
}
