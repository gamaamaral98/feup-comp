package symbol;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ClassSymbolTable {

    private final String className;
    private final String extendedClassName;
    // Key is function name
    private LinkedHashMap<String, List<FunctionSymbolTable>> functions;
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

    public boolean addFunction(String name, int num_parameters){
        if(functions.containsKey(name)){
            for (int i = 0; i < functions.get(name).size(); i++) {
                if(functions.get(name).get(i).getNum_parameters() == num_parameters)
                    return false;
            }
            functions.get(name).add(new FunctionSymbolTable(num_parameters));
            return true;
        } else{
            List<FunctionSymbolTable> list = new ArrayList<>();
            list.add(new FunctionSymbolTable(num_parameters));
            functions.put(name, list);
            return true;
        }
    }

    public boolean addFunctionParameter(String functionName, String atr, Symbol.SymbolType type, int num_parameters){
        if(!functions.containsKey(functionName)){
            return false;
        }
        for(int i = 0; i < functions.get(functionName).size(); i++){
            if(functions.get(functionName).get(i).getNum_parameters() == num_parameters){
                functions.get(functionName).get(i).addParameter(atr, type);
                return true;
            }
        }
        return false;
    }

    public boolean addFunctionParameter(String functionName, String atr, Symbol.SymbolType type, String identifier_name, int num_parameters){
        if(!functions.containsKey(functionName)){
            return false;
        }
        for(int i = 0; i < functions.get(functionName).size(); i++){
            if(functions.get(functionName).get(i).getNum_parameters() == num_parameters){
                functions.get(functionName).get(i).addParameter(atr, type, identifier_name);
                return true;
            }
        }
        return false;
    }

    public boolean setFunctionReturnType(String functionName, Symbol.SymbolType type, int num_parameters){
        if(!functions.containsKey(functionName)){
            return false;
        }
        for(int i = 0; i < functions.get(functionName).size(); i++){
            if(functions.get(functionName).get(i).getNum_parameters() == num_parameters){
                functions.get(functionName).get(i).setReturnType(type);
                return true;
            }
        }
        return false;
    }

    public boolean setFunctionReturnType(String functionName, Symbol.SymbolType type, String identifier_name, int num_parameters){
        if(!functions.containsKey(functionName)){
            return false;
        }
        for(int i = 0; i < functions.get(functionName).size(); i++){
            if(functions.get(functionName).get(i).getNum_parameters() == num_parameters){
                functions.get(functionName).get(i).setReturnType(type, identifier_name);
                return true;
            }
        }
        return false;
    }

    public boolean setFunctionReturnAttribute(String functionName, String atr, int num_parameters){
        if(!functions.containsKey(functionName)){
            return false;
        }
        for(int i = 0; i < functions.get(functionName).size(); i++){
            if(functions.get(functionName).get(i).getNum_parameters() == num_parameters){
                functions.get(functionName).get(i).setReturnAttribute(atr);
                return true;
            }
        }
        return false;
    }

    public Symbol.SymbolType getFunctionsReturnType(String functionName, int num_parameters){
        for(int i = 0; i < functions.get(functionName).size(); i++){
            if(functions.get(functionName).get(i).getNum_parameters() == num_parameters){
                return functions.get(functionName).get(i).getReturnType();
            }
        }
        return null;
    }

    public String getFunctionsReturnIdentifierType(String functionName, int num_parameters){
        for(int i = 0; i < functions.get(functionName).size(); i++){
            if(functions.get(functionName).get(i).getNum_parameters() == num_parameters){
                return functions.get(functionName).get(i).getReturnIdentifierType();
            }
        }
        return null;
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

    public LinkedHashMap<String, List<FunctionSymbolTable>> getFunctions() {
        return functions;
    }

    public FunctionSymbolTable getFunction(String functionName, int num_parameters){
        for(int i = 0; i < functions.get(functionName).size(); i++){
            if(functions.get(functionName).get(i).getNum_parameters() == num_parameters){
                return functions.get(functionName).get(i);
            }
        }
        return null;
    }

    public LinkedHashMap<String, Symbol> getGlobal_variables() {
        return global_variables;
    }

    public boolean hasVariable(String functionName, String variableName, int num_parameters){
        for(int i = 0; i < functions.get(functionName).size(); i++){
            if(functions.get(functionName).get(i).getNum_parameters() == num_parameters){
                if(functions.get(functionName).get(i).getLocalVariables().containsKey(variableName)){
                    return true;
                }
                if(functions.get(functionName).get(i).getParameters().containsKey(variableName)){
                    return true;
                }
                break;
            }
        }
        return global_variables.containsKey(variableName);
    }

    public boolean hasVariableBeenInitialized(String functionName, String variableName, int num_parameters){
        for(int i = 0; i < functions.get(functionName).size(); i++){
            if(functions.get(functionName).get(i).getNum_parameters() == num_parameters){
                if(functions.get(functionName).get(i).getLocalVariables().containsKey(variableName)){
                    return functions.get(functionName).get(i).getLocalVariables().get(variableName).isInit();
                }
                break;
            }
        }

        return global_variables.get(variableName).isInit();
    }

    public void setInitVariable(String functionName, String variableName, int num_parameters){
        for(int i = 0; i < functions.get(functionName).size(); i++){
            if(functions.get(functionName).get(i).getNum_parameters() == num_parameters){
                if(functions.get(functionName).get(i).getLocalVariables().containsKey(variableName)){
                    functions.get(functionName).get(i).getLocalVariables().get(variableName).setInit(true);
                }
                else if (global_variables.containsKey(variableName))
                    global_variables.get(variableName).setInit(true);
                break;
            }
        }
    }

    public String getVariableIdentifierType(String functionName, String variableName, int num_parameters){
        for(int i = 0; i < functions.get(functionName).size(); i++){
            if(functions.get(functionName).get(i).getNum_parameters() == num_parameters){
                if(functions.get(functionName).get(i).getLocalVariables().containsKey(variableName))
                    return functions.get(functionName).get(i).getLocalVariables().get(variableName).getIdentifier_name();
                else
                    return global_variables.get(variableName).getIdentifier_name();
            }
        }
        return null;
    }

    public Symbol.SymbolType getVariableType(String functionName, String variableName, int num_parameters){
        for(int i = 0; i < functions.get(functionName).size(); i++){
            if(functions.get(functionName).get(i).getNum_parameters() == num_parameters){
                if(functions.get(functionName).get(i).getLocalVariables().containsKey(variableName))
                    return functions.get(functionName).get(i).getLocalVariables().get(variableName).getType();
                else if(functions.get(functionName).get(i).getParameters().containsKey(variableName))
                    return functions.get(functionName).get(i).getParameters().get(variableName).getType();
                break;
            }
        }
        return global_variables.get(variableName).getType();
    }

    public String getClassName() {
        return className;
    }

    public String getExtendedClassName() {
        return extendedClassName;
    }
}