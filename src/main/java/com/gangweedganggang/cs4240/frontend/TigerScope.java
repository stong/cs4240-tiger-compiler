package com.gangweedganggang.cs4240.frontend;

import java.util.HashMap;
import java.util.Map;

public class TigerScope implements IScope {
    private final IScope parent;
    private final Map<String, TypedefSymbolType> typedefs = new HashMap<>();
    private final Map<String, TigerVariable> variables = new HashMap<>();
    private final Map<String, TigerFunction> functions = new HashMap<>();
    private final int id; // this ID doesn't mean anything. it's literally just used for pretty printing and thats it.

    public TigerScope(int id, IScope parent) {
        // assert id != 0;
        this.id = id;
        assert parent != null;
        this.parent = parent;
    }

    @Override
    public void addTypedef(TypedefSymbolType definition) {
        if (typedefs.containsKey(definition.name))
            throw new CompilationError(0,0, "multiple definition of " + definition.name);
        typedefs.put(definition.name, definition);
    }

    @Override
    public void addVariable(TigerVariable variable) {
        if (variables.containsKey(variable.name))
            throw new CompilationError(0,0, "multiple definition of " + variable.name);
        variables.put(variable.name, variable);
    }

    @Override
    public void addFunction(TigerFunction func) {
        if (functions.containsKey(func.name))
            throw new CompilationError(0,0, "multiple definition of " + func.name);
        functions.put(func.name, func);
    }

    @Override
    public SymbolType getSymbol(String name) {
        if (typedefs.containsKey(name))
            return typedefs.get(name);
        if (variables.containsKey(name))
            return variables.get(name).type;
        if (functions.containsKey(name))
            return functions.get(name).type;
        return null;
    }

    // lookup symbol in current AND PARENT scopes
    @Override
    public TigerVariable lookupVariable(String name) {
        if (variables.containsKey(name))
            return variables.get(name);
        return parent.lookupVariable(name);
    }

    @Override
    public ITigerFunction lookupFunction(String name) {
        if (functions.containsKey(name))
            return functions.get(name);
        return parent.lookupFunction(name);
    }

    @Override
    public SymbolType resolveType(String name) {
        if (typedefs.containsKey(name))
            return typedefs.get(name);
        return parent.resolveType(name);
    }

    @Override
    public IScope getParent() {
        return parent;
    }

    @Override
    public int getID() {
        return id;
    }
}
