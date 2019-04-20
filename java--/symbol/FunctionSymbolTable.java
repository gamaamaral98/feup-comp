package symbol;

import java.util.LinkedHashMap;

public class FunctionSymbolTable {

    // Key is the parameter name
    private LinkedHashMap<String, Symbol> parameters;
    // Key is the variable name
    private LinkedHashMap<String, Symbol> local_variables;

    private Symbol.SymbolType declaratedReturnType;
    // If declaratedReturnType is IDENTIFIER
    private String identifier_name;

    private Symbol returnSymbol;

    public FunctionSymbolTable() {
        this.parameters = new LinkedHashMap<>();
        this.local_variables = new LinkedHashMap<>();
        this.returnSymbol = null;
        this.declaratedReturnType = null;
    }

    public LinkedHashMap<String, Symbol> getParameters() {
        return parameters;
    }

    public LinkedHashMap<String, Symbol> getLocalVariables() {
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

    public boolean addParameter(String atr, Symbol.SymbolType type, String identifier_name){
        if(parameters.containsKey(atr))
            return false;
        Symbol s = new Symbol(atr, type, identifier_name);
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

    public boolean setDeclaratedReturnType(Symbol.SymbolType declaratedReturnType) {
        if(this.declaratedReturnType != null)
            return false;
        this.declaratedReturnType = declaratedReturnType;
        return true;
    }

    public boolean setDeclaratedReturnType(Symbol.SymbolType declaratedReturnType, String identifier_name) {
        if(this.declaratedReturnType != null)
            return false;
        this.declaratedReturnType = declaratedReturnType;
        this.identifier_name = identifier_name;
        return true;
    }

    public Symbol.SymbolType getDeclaratedReturnType() {
        return declaratedReturnType;
    }

    public String getIdentifier_name() {
        return identifier_name;
    }
}
