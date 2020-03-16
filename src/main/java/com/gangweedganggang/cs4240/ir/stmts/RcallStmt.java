package com.gangweedganggang.cs4240.ir.stmts;

import com.gangweedganggang.cs4240.frontend.ITigerFunction;
import com.gangweedganggang.cs4240.frontend.TigerFunction;
import com.gangweedganggang.cs4240.ir.IRLocal;

import java.util.Collections;
import java.util.List;
import java.util.Set;

// Call with return value ... lmao
public class RcallStmt extends CallStmt {
    private IRLocal lhs;

    public RcallStmt(IRLocal lhs, ITigerFunction func, List<IRLocal> args) {
        super(func, args);
        this.lhs = lhs;
        if (!lhs.getType().getPrimitiveType().equals(func.getType().retType.getPrimitiveType()))
            throw new IllegalArgumentException("incompatible types");
        for (int i = 0; i < args.size(); i++)
            if (!args.get(i).getType().getPrimitiveType().equals(func.getType().paramTypes.get(i).getPrimitiveType()))
                throw new IllegalArgumentException("incompatible types");
    }

    public IRLocal getLhs() {
        return lhs;
    }

    public void setLhs(IRLocal lhs) {
        this.lhs = lhs;
    }

    @Override
    public String toString() {
        return "callr " + lhs + ", " + func + ", " + args;
    }
}
