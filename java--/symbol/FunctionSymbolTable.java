package symbol;

import java.util.HashMap;

public class FunctionSymbolTable {

    // Key is the parameter name
    private HashMap<String, Symbol> parameters;
    // Key is the variable name
    private HashMap<String, Symbol> local_variables;
    private Symbol returnSymbol;

    public FunctionSymbolTable() {
        this.parameters = new HashMap<>();
        this.local_variables = new HashMap<>();
        this.returnSymbol = null;
    }

    public HashMap<String, Symbol> getParameters() {
        return parameters;
    }

    public HashMap<String, Symbol> getLocalVariables() {
        return local_variables;
    }

    public Symbol getReturnSymbol() {
        return returnSymbol;
    }

    public boolean setReturnSymbol(String atr, Symbol.SymbolType type) {
        if(returnSymbol != null)
            return false;

        Symbol s = new Symbol(atr, type);
        this.returnSymbol = s;
        return true;
    }

    public boolean addParameter(String atr, Symbol.SymbolType type){
        if(parameters.containsKey(atr))
            return false;
        Symbol s = new Symbol(atr, type);
        parameters.put(atr, s);
        return true;
    }

    public boolean addLocalVariable(String atr, Symbol.SymbolType type, int local_value){
        if(local_variables.containsKey(atr))
            return false;
        Symbol s = new Symbol(atr, type, local_value);
        local_variables.put(atr, s);
        return true;
    }
}
