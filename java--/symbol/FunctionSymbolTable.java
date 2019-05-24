package symbol;

import java.util.LinkedHashMap;

public class FunctionSymbolTable {

    // Key is the parameter name
    private LinkedHashMap<String, Symbol> parameters;
    // Key is the variable name
    private LinkedHashMap<String, Symbol> local_variables;

    private Symbol returnSymbol;

    private int num_parameters;

    public FunctionSymbolTable(int num_parameters) {
        this.parameters = new LinkedHashMap<>();
        this.local_variables = new LinkedHashMap<>();
        this.returnSymbol = null;
        this.num_parameters = num_parameters;
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

    public boolean addParameter(String atr, Symbol.SymbolType type){
        if(parameters.containsKey(atr))
            return false;
        Symbol s = new Symbol(atr, type);
        s.setInit(true);
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

    public boolean addLocalVariable(String atr, Symbol.SymbolType type, String identifier_name, int local_value){
        if(local_variables.containsKey(atr))
            return false;
        Symbol s = new Symbol(atr, type, identifier_name, local_value);
        local_variables.put(atr, s);
        return true;
    }

    public boolean setReturnType(Symbol.SymbolType returnType) {
        if(this.returnSymbol != null)
            return false;
        this.returnSymbol = new Symbol(returnType);
        return true;
    }

    public boolean setReturnType(Symbol.SymbolType returnType, String identifier_name) {
        if(this.returnSymbol != null)
            return false;
        this.returnSymbol = new Symbol(returnType);
        this.returnSymbol.setIdentifier_name(identifier_name);
        return true;
    }

    public Symbol.SymbolType getReturnType() {
        return returnSymbol.getType();
    }

    public String getReturnIdentifierType() {
        return this.returnSymbol.getIdentifier_name();
    }

    public void setReturnAttribute(String atr){
        this.returnSymbol.setAttribute(atr);
    }

    public int getNum_parameters() {
        return num_parameters;
    }
}
