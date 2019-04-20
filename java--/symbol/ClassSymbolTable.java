package symbol;

import java.util.HashMap;

public class ClassSymbolTable {

    private String className;
    // Key is function name
    private HashMap<String, FunctionSymbolTable> functions;
    // Key is the variable name
    private HashMap<String, Symbol> global_variables;

    public ClassSymbolTable(String className){
        this.className = className;
        this.functions = new HashMap<>();
        this.global_variables = new HashMap<>();
    }

    public boolean addFunction(String name){
        if(functions.containsKey(name)){
            System.out.println("Function with the same name already exists");
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
            System.out.println("Function doesn't exist");
            return false;
        }
        if(functions.get(functionName).addParameter(atr, type)){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean setFunctionReturnSymbol(String functionName, String atr, Symbol.SymbolType type){
        if(!functions.containsKey(functionName)){
            System.out.println("Function doesn't exist");
            return false;
        }
        if(functions.get(functionName).setReturnSymbol(atr, type)){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean addGlobalVariable(String atr, Symbol.SymbolType type){
        if(global_variables.containsKey(atr)){
            System.out.println("Global Variable already declared");
            return false;
        }
        Symbol s = new Symbol(atr, type);
        global_variables.put(atr, s);
        return true;
    }

    public HashMap<String, FunctionSymbolTable> getFunctions() {
        return functions;
    }

    public HashMap<String, Symbol> getGlobal_variables() {
        return global_variables;
    }
}