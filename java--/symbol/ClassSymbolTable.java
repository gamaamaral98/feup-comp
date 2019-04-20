package symbol;

import java.util.LinkedHashMap;

public class ClassSymbolTable {

    private final String className;
    private final String extendedClassName;
    // Key is function name
    private LinkedHashMap<String, FunctionSymbolTable> functions;
    // Key is the variable name
    private LinkedHashMap<String, Symbol> global_variables;

    public ClassSymbolTable(String className){
        this.className = className;
        this.extendedClassName = "";
        this.functions = new LinkedHashMap<>();
        this.global_variables = new LinkedHashMap<>();
    }

    public ClassSymbolTable(String className, String extendedName){
        this.className = className;
        this.extendedClassName = extendedName;
        this.functions = new LinkedHashMap<>();
        this.global_variables = new LinkedHashMap<>();
    }

    public boolean addFunction(String name){
        if(functions.containsKey(name)){
            return false;
        }
        FunctionSymbolTable f = new FunctionSymbolTable();
        functions.put(name, f);
        return true;
    }

    public boolean addFunctionVariable(String functionName, String atr, Symbol.SymbolType type, int local_value){
        if(global_variables.containsKey(atr)){
            System.out.println("Global Variable with the same name already exists");
            return false;
        }
        if(!functions.containsKey(functionName)){
            System.out.println("Function doesn't exist");
            return false;
        }
        if(functions.get(functionName).addLocalVariable(atr, type, local_value)){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean addFunctionParameter(String functionName, String atr, Symbol.SymbolType type){
        if(!functions.containsKey(functionName)){
            return false;
        }
        if(functions.get(functionName).addParameter(atr, type)){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean addFunctionParameter(String functionName, String atr, Symbol.SymbolType type, String identifier_name){
        if(!functions.containsKey(functionName)){
            return false;
        }
        if(functions.get(functionName).addParameter(atr, type, identifier_name)){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean setFunctionReturnSymbol(String functionName, String atr, Symbol.SymbolType type){
        if(!functions.containsKey(functionName)){
            return false;
        }
        if(functions.get(functionName).setReturnSymbol(atr, type)){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean setFunctionReturnType(String functionName, Symbol.SymbolType type){
        if(!functions.containsKey(functionName)){
            return false;
        }
        if(functions.get(functionName).setDeclaratedReturnType(type)){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean setFunctionReturnType(String functionName, Symbol.SymbolType type, String identifier_name){
        if(!functions.containsKey(functionName)){
            return false;
        }
        if(functions.get(functionName).setDeclaratedReturnType(type, identifier_name)){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean addGlobalVariable(String atr, Symbol.SymbolType type){
        if(global_variables.containsKey(atr)){
            return false;
        }
        Symbol s = new Symbol(atr, type);
        global_variables.put(atr, s);
        return true;
    }

    public boolean addGlobalVariable(String atr, Symbol.SymbolType type, String indentifier_name){
        if(global_variables.containsKey(atr)){
            return false;
        }
        Symbol s = new Symbol(atr, type, indentifier_name);
        global_variables.put(atr, s);
        return true;
    }

    public LinkedHashMap<String, FunctionSymbolTable> getFunctions() {
        return functions;
    }

    public LinkedHashMap<String, Symbol> getGlobal_variables() {
        return global_variables;
    }
}