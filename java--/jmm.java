import symbol.ClassSymbolTable;
import symbol.FunctionSymbolTable;
import symbol.Symbol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class jmm{

    private static int optRN = -1;
    private static boolean optO = false;
    private static FileInputStream fileStream;
    private int number_errors = 0;

    private ClassSymbolTable symbolTables;
    
    public static void main(String args []) throws ParseException, IOException{

        if(readArgs(args) == false){
            return;
        }
    
        Parser parser = new Parser(fileStream);

        new jmm(parser);
    }

    public jmm(Parser parser) throws ParseException, IOException{
        SimpleNode node = parser.Program();
        System.out.println("\n---- TREE ----");
        node.dump("");

        System.out.println("\n---- SEMANTIC ERRORS ----");
        createSymbolTables(node);

        System.out.println("\n---- SYMBOL TABLES ----");
        printSymbolTables();
    }

    public void createSymbolTables(SimpleNode node){
        if(node != null && node instanceof ASTProgram){
            int i = 1;
            int line;
            if(node.jjtGetChild(0) instanceof ASTCLASS){
                ASTCLASS root_class = (ASTCLASS) node.jjtGetChild(0);
                ASTCLASS_NAME class_name = (ASTCLASS_NAME) root_class.jjtGetChild(0);
                this.symbolTables = new ClassSymbolTable(class_name.name);
            }
            else if(node.jjtGetChild(0) instanceof ASTCLASS_EXTENDS){
                ASTCLASS_EXTENDS root_class = (ASTCLASS_EXTENDS) node.jjtGetChild(0);
                ASTCLASS_NAME class_name = (ASTCLASS_NAME) root_class.jjtGetChild(0);
                ASTEXTENDED_CLASS extended_class_name = (ASTEXTENDED_CLASS) root_class.jjtGetChild(1);
                this.symbolTables = new ClassSymbolTable(class_name.name, extended_class_name.name);
                i = 2;
            }
            else{
                // DELETE
                System.out.println("WTF IS THIS?");
            }

            for( ; i < node.jjtGetChild(0).jjtGetNumChildren(); i++){

                //GLOBAL VARIABLES
                if(node.jjtGetChild(0).jjtGetChild(i) instanceof ASTVAR_DECLS){
                    ASTVAR_DECLS var_declarations = (ASTVAR_DECLS) node.jjtGetChild(0).jjtGetChild(i);
                    for(int j = 0; j < var_declarations.jjtGetNumChildren(); j++){
                        String variable_name = (String) ((ASTIDENTIFIER) var_declarations.jjtGetChild(j).jjtGetChild(1)).name;
                        line = ((ASTIDENTIFIER) var_declarations.jjtGetChild(j).jjtGetChild(1)).line;
                        if(var_declarations.jjtGetChild(j).jjtGetChild(0) instanceof ASTINT_ARRAY){
                            if(!this.symbolTables.addGlobalVariable(variable_name, Symbol.SymbolType.INT_ARRAY)){
                                semanticError("Redefinition of global variable.", variable_name, line);
                            }
                        }else if (var_declarations.jjtGetChild(j).jjtGetChild(0) instanceof ASTINT){
                            if(!this.symbolTables.addGlobalVariable(variable_name, Symbol.SymbolType.INT)){
                                semanticError("Redefinition of global variable.", variable_name, line);
                            }
                        }else if(var_declarations.jjtGetChild(j).jjtGetChild(0) instanceof ASTBOOLEAN){
                            if(!this.symbolTables.addGlobalVariable(variable_name, Symbol.SymbolType.BOOLEAN)){
                                semanticError("Redefinition of global variable.", variable_name, line);
                            }
                        }else if(var_declarations.jjtGetChild(j).jjtGetChild(0) instanceof ASTIDENTIFIER){
                            String identifier_name = ((ASTIDENTIFIER) var_declarations.jjtGetChild(j).jjtGetChild(0)).name;
                            if(!this.symbolTables.addGlobalVariable(variable_name, Symbol.SymbolType.IDENTIFIER, identifier_name)){
                                semanticError("Redefinition of global variable.", variable_name, line);
                            }
                        }
                        else{
                            // DELETE
                            System.out.println("WTF IS THIS?");
                        }
                    }
                }

                //FUNCTIONS
                else if(node.jjtGetChild(0).jjtGetChild(i) instanceof ASTMETHODS){
                    ASTMETHODS functions = (ASTMETHODS) node.jjtGetChild(0).jjtGetChild(i);
                    for(int j = 0; j < functions.jjtGetNumChildren(); j++){
                        // NORMAL FUNCTION
                        if(functions.jjtGetChild(j) instanceof ASTMETHOD){
                            ASTMETHOD function = (ASTMETHOD) functions.jjtGetChild(j);

                            // FUNCTION NAME
                            String function_name = ((ASTNAME) function.jjtGetChild(1)).name;
                            line = ((ASTNAME) function.jjtGetChild(1)).line;
                            if(!this.symbolTables.addFunction(function_name)){
                                semanticError("Duplicated function definition", function_name, line);
                            }

                            // FUNCTION RETURN TYPE
                            if(function.jjtGetChild(0) instanceof ASTINT_ARRAY){
                                if(!this.symbolTables.setFunctionReturnType(function_name, Symbol.SymbolType.INT_ARRAY)){
                                    semanticError("Duplicated function return type definition", function_name, line);
                                }
                            } else if(function.jjtGetChild(0) instanceof ASTINT){
                                if(!this.symbolTables.setFunctionReturnType(function_name, Symbol.SymbolType.INT)){
                                    semanticError("Duplicated function return type definition", function_name, line);
                                }
                            } else if(function.jjtGetChild(0) instanceof ASTBOOLEAN){
                                if(!this.symbolTables.setFunctionReturnType(function_name, Symbol.SymbolType.BOOLEAN)){
                                    semanticError("Duplicated function return type definition", function_name, line);
                                }
                            } else if(function.jjtGetChild(0) instanceof ASTIDENTIFIER){
                                String identifier_name = ((ASTIDENTIFIER) function.jjtGetChild(0)).name;
                                if(!this.symbolTables.setFunctionReturnType(function_name, Symbol.SymbolType.IDENTIFIER, identifier_name)){
                                    semanticError("Duplicated function return type definition", function_name, line);
                                }
                            }
                            else{
                                // DELETE
                                System.out.println("WTF IS THIS?");
                            }

                            // FUNCTION PARAMETERS
                            ASTMETHOD_ARGS function_args = (ASTMETHOD_ARGS) function.jjtGetChild(2);
                            for(int m = 0; m < function_args.jjtGetNumChildren(); m++){
                                String parameter_name = ((ASTIDENTIFIER) function_args.jjtGetChild(m).jjtGetChild(1)).name;
                                line = ((ASTIDENTIFIER) function_args.jjtGetChild(m).jjtGetChild(1)).line;
                                if(function_args.jjtGetChild(m).jjtGetChild(0) instanceof ASTINT_ARRAY){
                                    if(!this.symbolTables.addFunctionParameter(function_name, parameter_name, Symbol.SymbolType.INT_ARRAY)){
                                        semanticError("Duplicated parameter in function arguments", function_name, line);
                                    }
                                } else if(function_args.jjtGetChild(m).jjtGetChild(0) instanceof ASTINT){
                                    if(!this.symbolTables.addFunctionParameter(function_name, parameter_name, Symbol.SymbolType.INT)){
                                        semanticError("Duplicated parameter in function arguments", function_name, line);
                                    }

                                } else if(function_args.jjtGetChild(m).jjtGetChild(0) instanceof ASTBOOLEAN){
                                    if(!this.symbolTables.addFunctionParameter(function_name, parameter_name, Symbol.SymbolType.BOOLEAN)){
                                        semanticError("Duplicated parameter in function arguments", function_name, line);
                                    }

                                } else if(function_args.jjtGetChild(m).jjtGetChild(0) instanceof ASTIDENTIFIER){
                                    String identifier_name = ((ASTIDENTIFIER) function_args.jjtGetChild(m).jjtGetChild(0)).name;
                                    if(!this.symbolTables.addFunctionParameter(function_name, parameter_name, Symbol.SymbolType.IDENTIFIER, identifier_name)){
                                        semanticError("Duplicated parameter in function arguments", function_name, line);
                                    }
                                }
                                else{
                                    // DELETE
                                    System.out.println("WTF IS THIS?");
                                }

                            }

                            // FUNCTION BODY
                            ASTMETHOD_BODY function_body = (ASTMETHOD_BODY) function.jjtGetChild(3);
                            handleMethodBody(function_name, function_body);

                            // FUNCTION RETURN EXPRESSION
                            ASTRETURN_EXPRESSION return_expression = (ASTRETURN_EXPRESSION) function.jjtGetChild(4);
                            handleReturnExpression(function_name, return_expression);
                        }

                        // MAIN FUNCTION
                        else if(functions.jjtGetChild(j) instanceof ASTMAIN){
                            String function_name = "main";
                            ASTMAIN function = (ASTMAIN) functions.jjtGetChild(j);
                            String parameter = ((ASTARGV) function.jjtGetChild(0)).name;
                            line = ((ASTARGV) function.jjtGetChild(0)).line;

                            // ADDING MAIN FUNCTION
                            if(!this.symbolTables.addFunction("main")){
                                semanticError("Duplicated function definition", function_name, line);
                            }

                            // ADDING MAIN PARAMETERS
                            if(!this.symbolTables.addFunctionParameter(function_name, parameter, Symbol.SymbolType.STRING_ARRAY)){
                                semanticError("Parameter already defined", parameter, line);
                            }

                            // MAIN BODY
                            ASTMETHOD_BODY function_body = (ASTMETHOD_BODY) function.jjtGetChild(1);
                            handleMethodBody(function_name, function_body);
                        }
                        else{
                            // DELETE
                            System.out.println("WTF IS THIS?");
                        }
                    }
                }
                else{
                    // DELETE
                    System.out.println("WTF IS THIS?");
                }
            }
        }
    }

    public void handleMethodBody(String function_name, ASTMETHOD_BODY body){
        // TODO: FAZER ISTO

        int local = 1;

        for(int n = 0; n < body.jjtGetNumChildren(); n++){
            //System.out.println(function_body.jjtGetChild(n));
            if(body.jjtGetChild(n) instanceof ASTVAR_DECL){
                if(body.jjtGetChild(n).jjtGetChild(0) instanceof ASTIDENTIFIER){
                    if(!this.symbolTables.getFunctions().get(function_name).addLocalVariable(((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).name, Symbol.SymbolType.IDENTIFIER, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name, local));
               
                } else if (body.jjtGetChild(n).jjtGetChild(0) instanceof ASTBOOLEAN){
                    if(!this.symbolTables.getFunctions().get(function_name).addLocalVariable(((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).name, Symbol.SymbolType.BOOLEAN, local));
               
                } else if (body.jjtGetChild(n).jjtGetChild(0) instanceof ASTINT){
                    if(!this.symbolTables.getFunctions().get(function_name).addLocalVariable(((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).name, Symbol.SymbolType.INT, local));
               
                }else if (body.jjtGetChild(n).jjtGetChild(0) instanceof ASTINT_ARRAY){}
                    if(!this.symbolTables.getFunctions().get(function_name).addLocalVariable(((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).name, Symbol.SymbolType.INT_ARRAY, local));
            



            } else if(body.jjtGetChild(n) instanceof ASTASSIGN){

            } else if(body.jjtGetChild(n) instanceof ASTASSIGN_ARRAY){

            } else if(body.jjtGetChild(n) instanceof ASTWHILE){

            } else if(body.jjtGetChild(n) instanceof ASTIF_ELSE_STATEMENT){

            } else if(body.jjtGetChild(n) instanceof ASTCALL_FUNCTION){

            }
            // FALTA { Statement }
            // FALTA Expression ;

        }
    }

    public void handleReturnExpression(String function_name, ASTRETURN_EXPRESSION return_expression){
        int line = return_expression.line;
        if(return_expression.jjtGetChild(0) instanceof ASTNOT){
            if(this.symbolTables.getFunctionsReturnType(function_name) != Symbol.SymbolType.BOOLEAN){
                semanticError("Incompatible return types", function_name, line);
            }
            //TODO: NOT INT, NOT IDENTIFIER ... NOT EXPRESSION
        } else if (return_expression.jjtGetChild(0) instanceof ASTIDENTIFIER){
            String name = ((ASTIDENTIFIER) return_expression.jjtGetChild(0)).name;
            if(!this.symbolTables.hasVariable(function_name, name)){
                semanticError("Cannot find symbol", name, line);
            } else if(this.symbolTables.getFunctionsReturnType(function_name) != Symbol.SymbolType.IDENTIFIER
                    || !this.symbolTables.getFunctionsReturnIdentifierType(function_name).equals(this.symbolTables.getVariableIdentifierType(function_name, name))){
                semanticError("Incompatible return types", function_name, line);
            } else if(!this.symbolTables.hasVariableBeenInitialized(function_name, name)){
                semanticError("Variable might not have been initialized", name, line);
            }
            this.symbolTables.setFunctionReturnAttribute(function_name, name);
        } else if (return_expression.jjtGetChild(0) instanceof ASTTRUE){
            if(this.symbolTables.getFunctionsReturnType(function_name) != Symbol.SymbolType.BOOLEAN){
                semanticError("Incompatible return types", function_name, line);
            }
        } else if (return_expression.jjtGetChild(0) instanceof ASTFALSE){
            if(this.symbolTables.getFunctionsReturnType(function_name) != Symbol.SymbolType.BOOLEAN){
                semanticError("Incompatible return types", function_name, line);
            }
        } else if (return_expression.jjtGetChild(0) instanceof ASTINT){
            if(this.symbolTables.getFunctionsReturnType(function_name) != Symbol.SymbolType.INT){
                semanticError("Incompatible return types", function_name, line);
            }
        } else if (return_expression.jjtGetChild(0) instanceof ASTACCESS_ARRAY){
            //TODO:
        } else if (return_expression.jjtGetChild(0) instanceof ASTADD){
            //TODO:
        } else if (return_expression.jjtGetChild(0) instanceof ASTAND){
            //TODO:
        } else if (return_expression.jjtGetChild(0) instanceof ASTLT){
            //TODO:
        } else if (return_expression.jjtGetChild(0) instanceof ASTSUB){
            //TODO:
        } else if (return_expression.jjtGetChild(0) instanceof ASTMUL){
            //TODO:
        } else if (return_expression.jjtGetChild(0) instanceof ASTDIV){
            //TODO:
        } else if (return_expression.jjtGetChild(0) instanceof ASTLENGTH){
            //TODO:
        } else if (return_expression.jjtGetChild(0) instanceof ASTNEW_CLASS){
            //TODO:
        } else if (return_expression.jjtGetChild(0) instanceof ASTNEW_INT_ARRAY){
            //TODO:
        } else if (return_expression.jjtGetChild(0) instanceof ASTCALL_FUNCTION){
            //TODO:
        }
        else{
            // DELETE
            System.out.println("WTF IS THIS?");
        }

    }

    public void printSymbolTables(){
        if(this.symbolTables.getExtendedClassName().equals("")){
            System.out.println("> Class name: " + this.symbolTables.getClassName());
        }else{
            System.out.println("> Class name: " + this.symbolTables.getClassName() + "\t> Extends: " + this.symbolTables.getExtendedClassName());
        }

        System.out.println("> Global variables:");
        for (Map.Entry<String, Symbol> entry : this.symbolTables.getGlobal_variables().entrySet()) {
            System.out.println("\t>Name: " + entry.getKey() + "\t>Type: " + entry.getValue().getTypeString());
        }

        System.out.println("> Functions:");
        for (Map.Entry<String, FunctionSymbolTable> entry : this.symbolTables.getFunctions().entrySet()) {
            System.out.println("\t> Function name: " + entry.getKey());

            System.out.println("\t\t> Parameters:");
            for (Map.Entry<String, Symbol> parameter_entry : entry.getValue().getParameters().entrySet()){
                System.out.println("\t\t\t>Name: " + parameter_entry.getValue().getAttribute() + "\tType: " + parameter_entry.getValue().getTypeString());
            }

            System.out.println("\t\t> Local Variables:");
            for (Map.Entry<String, Symbol> variable_entry : entry.getValue().getLocalVariables().entrySet()){
                System.out.print("\t\t\t>Name: " + variable_entry.getValue().getAttribute() + "\tType: " + variable_entry.getValue().getTypeString() + "\tLocal: ");
                int n = 1;
                for(int i = 1; i < n; i++){
                    System.out.print("1.");
                }
                System.out.println("1");
            }

            if(entry.getKey().equals("main")){
                System.out.println("\t\t> Return: void");
            } else{
                System.out.println("\t\t> Return: " + entry.getValue().getReturnSymbol().getTypeString());
            }
        }
    }

    public void semanticError(String error, String name, int line_number){
        System.out.println("> " + ++number_errors + "º Semantic Error (line "+ line_number + "): " + error + " -> "+ name);
    }

    public static void openFile(String filename){
        File file = new File(filename);
            
        try {
            fileStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("Error in file stream constructor: ");
            System.out.println("Usage: java -cp bin jmm <filePath> [-r=<n>] [-o]");
            e.printStackTrace();
            return;
        }
    }

    public static boolean readArgs(String args[]){
        if(args.length < 1){
            System.out.println("Usage: java -cp bin jmm <filePath> [-r=<n>] [-o]");
            return false;
        }

        openFile(args[0]);
        if(fileStream == null){
            System.out.println("File not found!");
            return false;
        }

        for(int i = 1; i < args.length; i++){
            if(validArgs(args[i]) == false)
                return false;
        }
        return true;
    }

    public static boolean validArgs(String arg){
        if(arg.equals("-o")) { 
            if(optO) {
                System.out.println("ERROR: Option O has already been defined.");
                System.out.println("Usage: java -cp bin jmm <filePath> [-r=<n>] [-o]");
                return false;
            }
            optO = true;
        }
        else if(arg.length() < 3){
            System.out.println("ERROR: Non valid argument");
            System.out.println("Usage: java -cp bin jmm <filePath> [-r=<n>] [-o]");
            return false;
        }
        else if(arg.substring(0, 3).equals("-r=")) {
            if(optRN >= 0) {
                System.out.println("ERROR: Option R has already been defined.");
                System.out.println("Usage: java -cp bin jmm <filePath> [-r=<n>] [-o]");
                return false;
            }
            if(arg.substring(3).matches("[0-9]+")) {
                optRN = Integer.parseInt(arg.substring(3));
                if(optRN < 0) {
                    System.out.println("ERROR: The number in option R must be an integer greater or equal to 0.");
                    System.out.println("Usage: java -cp bin jmm <filePath> [-r=<n>] [-o]");
                    return false;
                }
            }
            else {
                System.out.println("ERROR: The number in option R must be an integer.");
                System.out.println("Usage: java -cp bin jmm <filePath> [-r=<n>] [-o]");
                return false;
            }
        }
        else {
            System.out.println("ERROR: Non valid argument");
            System.out.println("Usage: java -cp bin jmm <filePath> [-r=<n>] [-o]");
            return false;
        }
        return true;	
    }
}