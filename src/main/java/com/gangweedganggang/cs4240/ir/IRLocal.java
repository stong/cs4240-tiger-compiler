package com.gangweedganggang.cs4240.ir;

import com.gangweedganggang.cs4240.flowgraph.Local;
import com.gangweedganggang.cs4240.frontend.PrimitiveSymbolType;
import com.gangweedganggang.cs4240.frontend.SymbolType;

/**
 * Non-SSA (non-versioned) temporary.
 */
public class IRLocal extends Local {
    public IRLocal(int index, SymbolType type) {
        super(index, type.getPrimitiveType());
    }
}