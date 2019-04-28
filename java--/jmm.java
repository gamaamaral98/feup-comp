import parser.*;
import symbol.ClassSymbolTable;
import symbol.FunctionSymbolTable;
import symbol.Symbol;
import codeGen.JasminGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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

        JasminGenerator jasminGenerator = new JasminGenerator(symbolTables, node);
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
                    }
                }

                //FUNCTIONS
                else if(node.jjtGetChild(0).jjtGetChild(i) instanceof ASTMETHODS){
                    ASTMETHODS functions = (ASTMETHODS) node.jjtGetChild(0).jjtGetChild(i);

                    // LOOP TO ADD ALL FUNCTIONS STRUCTURE (FUNCTION NAME, PARAMETERS AND RETURN TYPE)
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
                            }
                        } else if(functions.jjtGetChild(j) instanceof ASTMAIN){
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
                        }
                    }

                    // LOOP TO HANDLE FUNCTIONS BODY AND RETURN EXPRESSION
                    for(int j = 0; j < functions.jjtGetNumChildren(); j++){
                        // NORMAL FUNCTION
                        if(functions.jjtGetChild(j) instanceof ASTMETHOD){
                            ASTMETHOD function = (ASTMETHOD) functions.jjtGetChild(j);

                            // FUNCTION NAME
                            String function_name = ((ASTNAME) function.jjtGetChild(1)).name;

                            // FUNCTION BODY
                            ASTMETHOD_BODY function_body = (ASTMETHOD_BODY) function.jjtGetChild(3);
                            handleMethodBody(function_name, function_body);

                            // FUNCTION RETURN EXPRESSION
                            ASTRETURN_EXPRESSION return_expression = (ASTRETURN_EXPRESSION) function.jjtGetChild(4);
                            int line_return = return_expression.line;
                            handleReturnExpression(function_name, return_expression.jjtGetChild(0), line_return);
                        }

                        // MAIN FUNCTION
                        else if(functions.jjtGetChild(j) instanceof ASTMAIN){
                            String function_name = "main";
                            ASTMAIN function = (ASTMAIN) functions.jjtGetChild(j);

                            // MAIN BODY
                            ASTMETHOD_BODY function_body = (ASTMETHOD_BODY) function.jjtGetChild(1);
                            handleMethodBody(function_name, function_body);
                        }
                    }
                }
            }
        }
    }

    private void handleMethodBody(String function_name, ASTMETHOD_BODY body){
        // TODO: FAZER ISTO
        for(int n = 0; n < body.jjtGetNumChildren(); n++){
            int local = 1;
            //System.out.println(function_body.jjtGetChild(n));
            if(body.jjtGetChild(n) instanceof ASTVAR_DECL){
                if(body.jjtGetChild(n).jjtGetChild(0) instanceof ASTIDENTIFIER){
                    if (this.symbolTables.getGlobal_variables().containsKey(((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).name)) {
                        semanticError("Redefinition of global variable", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).line);
                    }
                    else if(!this.symbolTables.getFunctions().get(function_name).addLocalVariable(((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).name, Symbol.SymbolType.IDENTIFIER, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name, local)){
                        semanticError("Redefinition of local variable", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).line);
                    }
               
                } else if (body.jjtGetChild(n).jjtGetChild(0) instanceof ASTBOOLEAN){
                    if (this.symbolTables.getGlobal_variables().containsKey(((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).name)) {
                        semanticError("Redefinition of global variable", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).line);
                    }
                    else if(!this.symbolTables.getFunctions().get(function_name).addLocalVariable(((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).name, Symbol.SymbolType.BOOLEAN, local)){
                        semanticError("Redefinition of local variable", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).line);
                    }
               
                } else if (body.jjtGetChild(n).jjtGetChild(0) instanceof ASTINT){
                    if (this.symbolTables.getGlobal_variables().containsKey(((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).name)) {
                        semanticError("Redefinition of global variable", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).line);
                    }
                    else if(!this.symbolTables.getFunctions().get(function_name).addLocalVariable(((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).name, Symbol.SymbolType.INT, local)){
                        semanticError("Redefinition of local variable", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).line);
                    }
               
                }else if (body.jjtGetChild(n).jjtGetChild(0) instanceof ASTINT_ARRAY){
                    if (this.symbolTables.getGlobal_variables().containsKey(((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).name)) {
                        semanticError("Redefinition of global variable", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).line);
                    }
                    else if(!this.symbolTables.getFunctions().get(function_name).addLocalVariable(((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).name, Symbol.SymbolType.INT_ARRAY, local)){
                        semanticError("Redefinition of local variable", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).line);
                    }
                }

            // USAR FUNÇOES DO NUNO NAS COMPARAÇOES, MUST CHANGE
            } else if(body.jjtGetChild(n) instanceof ASTASSIGN){

                //Primeiro filho é garantido ser identifier no entanto verificamos se foi inicializado ou não
                if(body.jjtGetChild(n).jjtGetChild(0) instanceof ASTIDENTIFIER){
                    String name = ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name;
                    int line = ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line;
                    
                    if(!this.symbolTables.hasVariable(function_name, name)){
                        semanticError("Cannot find symbol", name, line);
                        return;
                    }
                }

                if(body.jjtGetChild(n).jjtGetChild(1) instanceof ASTIDENTIFIER){
                    String name = ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).name;
                    int line = ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1)).line;
                    if(!this.symbolTables.hasVariable(function_name, name)){
                        semanticError("Cannot find symbol", name, line);
                    }
                    else if(!this.symbolTables.hasVariableBeenInitialized(function_name, name)){
                        semanticError("Variable might not have been initialized", name, line);
                    }
                    if(this.symbolTables.getVariableType(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name) != this.symbolTables.getVariableType(function_name, name)){
                        semanticError("Incompatible assign type between variables", name, line);
                    }
                }

                // | Expression, ".", Identifier, "(", [ Expression { ",", Expression } ], ")"
                else if(body.jjtGetChild(n).jjtGetChild(1) instanceof ASTCALL_FUNCTION){
                    
                    if(body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0) instanceof ASTTHIS){
                        if(!this.symbolTables.getFunctions().containsKey(((ASTFUNCTION)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(1)).name)){
                            semanticError("Function not declared for class", ((ASTFUNCTION)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(1)).name, ((ASTFUNCTION)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(1)).line);
                        }
                        else if(this.symbolTables.getFunctions().get(((ASTFUNCTION)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(1)).name).getReturnType() != this.symbolTables.getVariableType(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name)){
                            semanticError("Incompatible return types", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line);
                        }
                        else{
                            //TODO CHAMAR FUNCAO DO NUNO;
                            continue;
                        }
                    }

                    if(body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0) instanceof ASTIDENTIFIER){
                        if(!this.symbolTables.hasVariable(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0)).name)){
                            semanticError("Argument not declared for class", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0)).name, ((ASTFUNCTION)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(1)).line);
                        }
                        else if(this.symbolTables.getFunctions().get(((ASTFUNCTION)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(1)).name).getReturnType() != this.symbolTables.getVariableType(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name)){
                            semanticError("Incompatible return types", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line);
                        }
                        else{
                            //TODO CHAMAR FUNCAO DO NUNO
                            continue;
                        }
                    }else{
                        semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line);
                    }
                }

                // Expression, "[", Expression, "]"
                else if(body.jjtGetChild(n).jjtGetChild(1) instanceof ASTACCESS_ARRAY){
                    if(this.symbolTables.getVariableType(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name) != Symbol.SymbolType.INT){
                        semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line);
                    }else if(!(body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(1) instanceof ASTINT)){
                        semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0)).line);                       
                    }
                }

                else if(body.jjtGetChild(n).jjtGetChild(1) instanceof ASTAND){
                    if(this.symbolTables.getVariableType(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name) != Symbol.SymbolType.BOOLEAN){
                        semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line);
                    }

                    if(!(body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0) instanceof ASTBOOLEAN) && !(body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0) instanceof ASTTRUE) && !(body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0) instanceof ASTFALSE)){
                        if(!(body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0) instanceof ASTIDENTIFIER)){
                            semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line);
                        }else if(this.symbolTables.getVariableType(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0)).name) != Symbol.SymbolType.BOOLEAN){
                            semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line);
                        }
                    }

                    if(!(body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(1) instanceof ASTBOOLEAN) && !(body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0) instanceof ASTTRUE) && !(body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0) instanceof ASTFALSE)){
                        if(!(body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(1) instanceof ASTIDENTIFIER)){
                            semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line); 
                        }else if(this.symbolTables.getVariableType(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(1)).name) != Symbol.SymbolType.BOOLEAN){
                            semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(1)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line);
                        }
                    }
                }

                else if(body.jjtGetChild(n).jjtGetChild(1) instanceof ASTLT){
                    if(this.symbolTables.getVariableType(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name) != Symbol.SymbolType.BOOLEAN){
                        semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line);
                    }

                    if(!(body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0) instanceof ASTINT)){
                        if(!(body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0) instanceof ASTIDENTIFIER)){
                            semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0)).line); 
                        }else if(this.symbolTables.getVariableType(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0)).name) != Symbol.SymbolType.INT){
                            semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line);
                        }
                    }

                    if(!(body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(1) instanceof ASTINT)){
                        if(!(body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(1) instanceof ASTIDENTIFIER)){
                            semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line); 
                        }else if(this.symbolTables.getVariableType(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(1)).name) != Symbol.SymbolType.INT){
                            semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(1)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line);
                        }
                    }
                }

                else if(body.jjtGetChild(n).jjtGetChild(1) instanceof ASTADD || body.jjtGetChild(n).jjtGetChild(1) instanceof ASTSUB || body.jjtGetChild(n).jjtGetChild(1) instanceof ASTMUL || body.jjtGetChild(n).jjtGetChild(1) instanceof ASTDIV){
                    if(this.symbolTables.getVariableType(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name) != Symbol.SymbolType.INT){
                        semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line);
                    }

                    if(!(body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0) instanceof ASTINT)){
                        if(!(body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0) instanceof ASTIDENTIFIER)){
                            semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0)).line); 
                        }else if(this.symbolTables.getVariableType(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0)).name) != Symbol.SymbolType.INT){
                            semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line);
                        }
                    }

                    if(!(body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(1) instanceof ASTINT)){
                        if(!(body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(1) instanceof ASTIDENTIFIER)){
                            semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line); 
                        }else if(this.symbolTables.getVariableType(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(1)).name) != Symbol.SymbolType.INT){
                            semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(1)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line);
                        }
                    }
                }

                else if(body.jjtGetChild(n).jjtGetChild(1) instanceof ASTLENGTH){
                    if(this.symbolTables.getVariableType(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0)).name) != Symbol.SymbolType.INT_ARRAY){
                        semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0)).line);
                    }
                }

                else if(body.jjtGetChild(n).jjtGetChild(1) instanceof ASTINT){
                    if(this.symbolTables.getVariableType(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name) != Symbol.SymbolType.INT){
                        semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line);
                    }
                }

                else if(body.jjtGetChild(n).jjtGetChild(1) instanceof ASTTHIS){
                    
                    if(!this.symbolTables.getVariableIdentifierType(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name).equals(this.symbolTables.getClassName())){
                        semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line);
                    }  
                }

                else if(body.jjtGetChild(n).jjtGetChild(1) instanceof ASTTRUE){
                    if(this.symbolTables.getVariableType(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name) != Symbol.SymbolType.BOOLEAN){
                        semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line);
                    }
                }

                else if(body.jjtGetChild(n).jjtGetChild(1) instanceof ASTFALSE){
                    if(this.symbolTables.getVariableType(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name) != Symbol.SymbolType.BOOLEAN){
                        semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line);
                    }
                }

                else if(body.jjtGetChild(n).jjtGetChild(1) instanceof ASTNEW_CLASS){
                    String class_name = ((ASTCLASS)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0)).name;
                    int line = ((ASTCLASS)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0)).line;
                    if(!this.symbolTables.getClassName().equals(class_name)){
                        semanticError("Class not found", class_name, line);
                    }
                }

                else if(body.jjtGetChild(n).jjtGetChild(1) instanceof ASTNEW_INT_ARRAY){
                    if(this.symbolTables.getVariableType(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name) != Symbol.SymbolType.INT_ARRAY){
                        semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line);
                    } 

                    if(!(body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0) instanceof ASTINT)){
                        semanticError("Incompatible types: cannot be converted to int", body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0).toString(), ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line);
                    }
                }

                else if(body.jjtGetChild(n).jjtGetChild(1) instanceof ASTNOT){
                    if(this.symbolTables.getVariableType(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name) != this.symbolTables.getVariableType(function_name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(1).jjtGetChild(0)).name)){
                        semanticError("Incompatible assign type", ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).name, ((ASTIDENTIFIER)body.jjtGetChild(n).jjtGetChild(0)).line);
                    }
                }

            } else if(body.jjtGetChild(n) instanceof ASTASSIGN_ARRAY){
                 continue;
            } else if(body.jjtGetChild(n) instanceof ASTWHILE){
                continue;
            } else if(body.jjtGetChild(n) instanceof ASTIF_ELSE_STATEMENT){
                continue;
            } else if(body.jjtGetChild(n) instanceof ASTCALL_FUNCTION){
                continue;
            }
            // FALTA { Statement }
            // FALTA Expression ;
        }
    }

    public void handleReturnExpression(String function_name, Node expression, int line){
        if(expression instanceof ASTNOT){
            if(this.symbolTables.getFunctionsReturnType(function_name) != Symbol.SymbolType.BOOLEAN){
                semanticError("Incompatible return types: cannot be converted to boolean", function_name, line);
            }
            handleNOT(function_name, line, expression);
        } else if (expression instanceof ASTIDENTIFIER){
            String name = ((ASTIDENTIFIER) expression).name;
            if(this.symbolTables.getFunctions().get(function_name).getParameters().containsKey(name)){
                if(this.symbolTables.getFunctionsReturnType(function_name) != this.symbolTables.getFunctions().get(function_name).getParameters().get(name).getType()){
                    semanticError("Incompatible return types", function_name, line);
                }
            } else if(this.symbolTables.hasVariable(function_name, name)){
                if(this.symbolTables.getFunctionsReturnType(function_name) != this.symbolTables.getVariableType(function_name, name)){
                    semanticError("Incompatible return types", function_name, line);
                }
                else if(!this.symbolTables.hasVariableBeenInitialized(function_name, name)){
                    semanticError("Variable might not have been initialized", name, line);
                }

            } else {
                semanticError("Cannot find symbol", name, line);
            }
            this.symbolTables.setFunctionReturnAttribute(function_name, name);
        } else if (expression instanceof ASTTRUE){
            if(this.symbolTables.getFunctionsReturnType(function_name) != Symbol.SymbolType.BOOLEAN){
                semanticError("Incompatible return types: cannot be converted to boolean", function_name, line);
            }
        } else if (expression instanceof ASTFALSE){
            if(this.symbolTables.getFunctionsReturnType(function_name) != Symbol.SymbolType.BOOLEAN){
                semanticError("Incompatible return types: cannot be converted to boolean", function_name, line);
            }
        } else if (expression instanceof ASTINT){
            if(this.symbolTables.getFunctionsReturnType(function_name) != Symbol.SymbolType.INT){
                semanticError("Incompatible return types: cannot be converted to int", function_name, line);
            }
        } else if (expression instanceof ASTACCESS_ARRAY){
            if(this.symbolTables.getFunctionsReturnType(function_name) != Symbol.SymbolType.INT){
                semanticError("Incompatible return types: cannot be converted to int", function_name, line);
            }
            ArrayList<Symbol.SymbolType> symbols = new ArrayList<>();
            symbols.add(Symbol.SymbolType.INT_ARRAY);
            handleIdentifier(function_name, line, expression.jjtGetChild(0), symbols);
        } else if (expression instanceof ASTADD){
            if(this.symbolTables.getFunctionsReturnType(function_name) != Symbol.SymbolType.INT){
                semanticError("Incompatible return types: cannot be converted to int", function_name, line);
            }
            handleMathOperationsReturnExpression(function_name, line, expression);
        } else if (expression instanceof ASTAND){
            if(this.symbolTables.getFunctionsReturnType(function_name) != Symbol.SymbolType.BOOLEAN){
                semanticError("Incompatible return types: cannot be converted to boolean", function_name, line);
            }
            handleAND(function_name, line, expression);
        } else if (expression instanceof ASTLT){
            if(this.symbolTables.getFunctionsReturnType(function_name) != Symbol.SymbolType.BOOLEAN){
                semanticError("Incompatible return types: cannot be converted to boolean", function_name, line);
            }
            handleLT(function_name, line, expression);
        } else if (expression instanceof ASTSUB){
            if(this.symbolTables.getFunctionsReturnType(function_name) != Symbol.SymbolType.INT){
                semanticError("Incompatible return types: cannot be converted to int", function_name, line);
            }
            handleMathOperationsReturnExpression(function_name, line, expression);
        } else if (expression instanceof ASTMUL){
            if(this.symbolTables.getFunctionsReturnType(function_name) != Symbol.SymbolType.INT){
                semanticError("Incompatible return types: cannot be converted to int", function_name, line);
            }
            handleMathOperationsReturnExpression(function_name, line, expression);
        } else if (expression instanceof ASTDIV){
            if(this.symbolTables.getFunctionsReturnType(function_name) != Symbol.SymbolType.INT){
                semanticError("Incompatible return types: cannot be converted to int", function_name, line);
            }
            handleMathOperationsReturnExpression(function_name, line, expression);
        } else if (expression instanceof ASTLENGTH){
            if(this.symbolTables.getFunctionsReturnType(function_name) != Symbol.SymbolType.INT){
                semanticError("Incompatible return types: cannot be converted to int", function_name, line);
            }
            ArrayList<Symbol.SymbolType> symbols = new ArrayList<>();
            symbols.add(Symbol.SymbolType.IDENTIFIER);
            symbols.add(Symbol.SymbolType.INT_ARRAY);
            if(expression.jjtGetChild(0) instanceof ASTIDENTIFIER){
                handleIdentifier(function_name, line, expression.jjtGetChild(0), symbols);
            }
        } else if (expression instanceof ASTNEW_CLASS){
            if(this.symbolTables.getFunctionsReturnType(function_name) != Symbol.SymbolType.IDENTIFIER){
                semanticError("Incompatible return types", function_name, line);
            }
            else if(!this.symbolTables.getFunctionsReturnIdentifierType(function_name).equals(((ASTCLASS) expression.jjtGetChild(0)).name)){
                semanticError("Incompatible return types", function_name, line);
            }
        } else if (expression instanceof ASTNEW_INT_ARRAY){
            if(this.symbolTables.getFunctionsReturnType(function_name) != Symbol.SymbolType.INT_ARRAY){
                semanticError("Incompatible return types: cannot be converted to int[]", function_name, line);
            }
            if(expression.jjtGetChild(0) instanceof ASTIDENTIFIER){
                ArrayList<Symbol.SymbolType> symbols = new ArrayList<>();
                symbols.add(Symbol.SymbolType.INT);
                handleIdentifier(function_name, line, expression.jjtGetChild(0), symbols);
            } else if(!(expression.jjtGetChild(0) instanceof ASTINT)){
                semanticError("Incompatible types: cannot be converted to int", expression.jjtGetChild(0).toString(), line);
            }
        } else if (expression instanceof ASTCALL_FUNCTION){
            if(expression.jjtGetChild(0) instanceof ASTTHIS){
                String function_call_name = ((ASTFUNCTION) expression.jjtGetChild(1)).name;
                if(!this.symbolTables.getFunctions().containsKey(function_call_name)){
                    semanticError("Function not found", function_call_name, line);
                } else if(this.symbolTables.getFunctionsReturnType(function_name) != this.symbolTables.getFunctions().get(function_call_name).getReturnType()){
                    semanticError("Incompatible return types for called function", function_name, line);
                } else{
                    handleFunctionArguments(function_name, function_call_name, line, expression.jjtGetChild(2));
                }
            } else if(expression.jjtGetChild(0) instanceof ASTIDENTIFIER){
                String name = ((ASTIDENTIFIER) expression.jjtGetChild(0)).name;
                String function_call_name = ((ASTFUNCTION) expression.jjtGetChild(1)).name;
                if(this.symbolTables.hasVariable(function_name, name)
                        && this.symbolTables.getVariableType(function_name, name) == Symbol.SymbolType.IDENTIFIER
                        && this.symbolTables.getVariableIdentifierType(function_name, name).equals(this.symbolTables.getClassName())){
                    if(!this.symbolTables.getFunctions().containsKey(function_call_name)){
                        semanticError("Function not found", function_call_name, line);
                    } else if(this.symbolTables.getFunctionsReturnType(function_name) != this.symbolTables.getFunctions().get(function_call_name).getReturnType()){
                        semanticError("Incompatible return types for called function", function_name, line);
                    } else{
                        handleFunctionArguments(function_name, function_call_name, line, expression.jjtGetChild(2));
                    }
                }
            }
        } else if(expression instanceof ASTTHIS){
            if((this.symbolTables.getFunctionsReturnType(function_name) != Symbol.SymbolType.IDENTIFIER) || (!this.symbolTables.getFunctionsReturnIdentifierType(function_name).equals(this.symbolTables.getClassName()))){
                semanticError("Incompatible return types", function_name, line);
            }
        }
        else{
            // DELETE
            System.out.println("WTF IS THIS?");
        }

    }

    private void handleParameterExpression(String function_name, Node expression, Symbol symbol, int line){
        if(expression instanceof ASTNOT){
            if(symbol.getType() != Symbol.SymbolType.BOOLEAN){
                semanticError("Incompatible types: cannot be converted to boolean", function_name, line);
            }
            handleNOT(function_name, line, expression);
        } else if (expression instanceof ASTIDENTIFIER){
            String name = ((ASTIDENTIFIER) expression).name;
            if(this.symbolTables.getFunctions().get(function_name).getParameters().containsKey(name)){
                if(symbol.getType() != this.symbolTables.getFunctions().get(function_name).getParameters().get(name).getType()){
                    semanticError("Incompatible types: cannot be converted to " + symbol.getTypeString(), function_name, line);
                }
            } else if(this.symbolTables.hasVariable(function_name, name)){
                if(this.symbolTables.getFunctionsReturnType(function_name) != this.symbolTables.getVariableType(function_name, name)){
                    semanticError("Incompatible types: cannot be converted to " + symbol.getTypeString(), function_name, line);
                }
                else if(!this.symbolTables.hasVariableBeenInitialized(function_name, name)){
                    semanticError("Variable might not have been initialized", name, line);
                }

            } else {
                semanticError("Cannot find symbol", name, line);
            }
        } else if (expression instanceof ASTTRUE){
            if(symbol.getType() != Symbol.SymbolType.BOOLEAN){
                semanticError("Incompatible types: cannot be converted to " + symbol.getTypeString(), function_name, line);
            }
        } else if (expression instanceof ASTFALSE){
            if(symbol.getType() != Symbol.SymbolType.BOOLEAN){
                semanticError("Incompatible types: cannot be converted to " + symbol.getTypeString(), function_name, line);
            }
        } else if (expression instanceof ASTINT){
            if(symbol.getType() != Symbol.SymbolType.INT){
                semanticError("Incompatible types: cannot be converted to " + symbol.getTypeString(), function_name, line);
            }
        } else if (expression instanceof ASTACCESS_ARRAY){
            if(symbol.getType() != Symbol.SymbolType.INT){
                semanticError("Incompatible types: cannot be converted to " + symbol.getTypeString(), function_name, line);
            }
            ArrayList<Symbol.SymbolType> symbols = new ArrayList<>();
            symbols.add(Symbol.SymbolType.INT_ARRAY);
            handleIdentifier(function_name, line, expression.jjtGetChild(0), symbols);
        } else if (expression instanceof ASTADD){
            if(symbol.getType() != Symbol.SymbolType.INT){
                semanticError("Incompatible types: cannot be converted to " + symbol.getTypeString(), function_name, line);
            }
            handleMathOperationsReturnExpression(function_name, line, expression);
        } else if (expression instanceof ASTAND){
            if(symbol.getType() != Symbol.SymbolType.BOOLEAN){
                semanticError("Incompatible types: cannot be converted to " + symbol.getTypeString(), function_name, line);
            }
            handleAND(function_name, line, expression);
        } else if (expression instanceof ASTLT){
            if(symbol.getType() != Symbol.SymbolType.BOOLEAN){
                semanticError("Incompatible types: cannot be converted to " + symbol.getTypeString(), function_name, line);
            }
            handleLT(function_name, line, expression);
        } else if (expression instanceof ASTSUB){
            if(symbol.getType() != Symbol.SymbolType.INT){
                semanticError("Incompatible types: cannot be converted to " + symbol.getTypeString(), function_name, line);
            }
            handleMathOperationsReturnExpression(function_name, line, expression);
        } else if (expression instanceof ASTMUL){
            if(symbol.getType() != Symbol.SymbolType.INT){
                semanticError("Incompatible types: cannot be converted to " + symbol.getTypeString(), function_name, line);
            }
            handleMathOperationsReturnExpression(function_name, line, expression);
        } else if (expression instanceof ASTDIV){
            if(symbol.getType() != Symbol.SymbolType.INT){
                semanticError("Incompatible types: cannot be converted to " + symbol.getTypeString(), function_name, line);
            }
            handleMathOperationsReturnExpression(function_name, line, expression);
        } else if (expression instanceof ASTLENGTH){
            if(symbol.getType() != Symbol.SymbolType.INT){
                semanticError("Incompatible types: cannot be converted to " + symbol.getTypeString(), function_name, line);
            }
            ArrayList<Symbol.SymbolType> symbols = new ArrayList<>();
            symbols.add(Symbol.SymbolType.IDENTIFIER);
            symbols.add(Symbol.SymbolType.INT_ARRAY);
            if(expression.jjtGetChild(0) instanceof ASTIDENTIFIER){
                handleIdentifier(function_name, line, expression.jjtGetChild(0), symbols);
            }
        } else if (expression instanceof ASTNEW_CLASS){
            if((symbol.getType() != Symbol.SymbolType.IDENTIFIER) || (!symbol.getIdentifier_name().equals(((ASTCLASS) expression.jjtGetChild(0)).name))){
                semanticError("Incompatible types: cannot be converted to " + symbol.getTypeString(), function_name, line);
            }
        } else if (expression instanceof ASTNEW_INT_ARRAY){
            if(symbol.getType() != Symbol.SymbolType.INT_ARRAY){
                semanticError("Incompatible types: cannot be converted to " + symbol.getTypeString(), function_name, line);
            }
            if(expression.jjtGetChild(0) instanceof ASTIDENTIFIER){
                ArrayList<Symbol.SymbolType> symbols = new ArrayList<>();
                symbols.add(Symbol.SymbolType.INT);
                handleIdentifier(function_name, line, expression.jjtGetChild(0), symbols);
            } else if(!(expression.jjtGetChild(0) instanceof ASTINT)){
                semanticError("Incompatible types: cannot be converted to int", expression.jjtGetChild(0).toString(), line);
            }
        } else if (expression instanceof ASTCALL_FUNCTION){
            if(expression.jjtGetChild(0) instanceof ASTTHIS){
                if(!this.symbolTables.getFunctions().containsKey(((ASTFUNCTION) expression.jjtGetChild(1)).name)){
                    semanticError("Function not found", ((ASTFUNCTION) expression.jjtGetChild(1)).name, line);
                } else if(symbol.getType() != this.symbolTables.getFunctions().get(((ASTFUNCTION) expression.jjtGetChild(1)).name).getReturnType()){
                    semanticError("Incompatible return types: cannot be converted to " + symbol.getTypeString(), ((ASTFUNCTION) expression.jjtGetChild(1)).name, line);
                } else{
                    handleFunctionArguments(function_name, ((ASTFUNCTION) expression.jjtGetChild(1)).name, line, expression.jjtGetChild(2));
                }
            } else if(expression.jjtGetChild(0) instanceof ASTIDENTIFIER){
                String name = ((ASTIDENTIFIER) expression.jjtGetChild(0)).name;
                String function_call_name = ((ASTFUNCTION) expression.jjtGetChild(1)).name;
                if(this.symbolTables.hasVariable(function_name, name)
                        && this.symbolTables.getVariableType(function_name, name) == Symbol.SymbolType.IDENTIFIER
                        && this.symbolTables.getVariableIdentifierType(function_name, name).equals(this.symbolTables.getClassName())){
                    if(!this.symbolTables.getFunctions().containsKey(function_call_name)){
                        semanticError("Function not found", function_call_name, line);
                    } else if(this.symbolTables.getFunctionsReturnType(function_name) != this.symbolTables.getFunctions().get(function_call_name).getReturnType()){
                        semanticError("Incompatible return types for called function", function_name, line);
                    } else{
                        handleFunctionArguments(function_name, function_call_name, line, expression.jjtGetChild(2));
                    }
                }
            }

        } else if(expression instanceof ASTTHIS){
            if((symbol.getType() != Symbol.SymbolType.IDENTIFIER) || (!symbol.getIdentifier_name().equals(this.symbolTables.getClassName()))){
                semanticError("Incompatible types: cannot be converted to " + symbol.getTypeString(), function_name, line);
            }
        }
        else{
            // DELETE
            System.out.println("WTF IS THIS?");
        }

    }

    private void handleFunctionArguments(String function_name, String function_called_name, int line, Node node) {
        if(this.symbolTables.getFunctions().get(function_called_name).getParameters().entrySet().size() != node.jjtGetNumChildren()){
            semanticError("Missing parameters", function_name, line);
        }

        int i = 0;
        for (Map.Entry<String, Symbol> entry : this.symbolTables.getFunctions().get(function_called_name).getParameters().entrySet()){
            handleParameterExpression(function_name, node.jjtGetChild(i), entry.getValue(), line);
            i++;
        }
    }

    private void handleNOT(String function_name, int line, Node node){
        if(node.jjtGetChild(0) instanceof ASTNOT){
            handleNOT(function_name, line, node.jjtGetChild(0));
        } else if(node.jjtGetChild(0) instanceof ASTIDENTIFIER){
            ArrayList<Symbol.SymbolType> symbols = new ArrayList<>();
            symbols.add(Symbol.SymbolType.BOOLEAN);
            handleIdentifier(function_name, line, node.jjtGetChild(0), symbols);
        } else if(node.jjtGetChild(0) instanceof ASTAND){
            handleAND(function_name, line, node.jjtGetChild(0));
        } else if(node.jjtGetChild(0) instanceof ASTLT){
            handleLT(function_name, line, node.jjtGetChild(0));
        } else if (!(node.jjtGetChild(0) instanceof ASTTRUE || node.jjtGetChild(0) instanceof ASTFALSE)){
            semanticError("Bad operand type for unary operator '!'", function_name, line);
        }
    }

    private void handleLT(String function_name, int line, Node node){
        if(node.jjtGetChild(0) instanceof ASTIDENTIFIER){
            ArrayList<Symbol.SymbolType> symbols = new ArrayList<>();
            symbols.add(Symbol.SymbolType.INT);
            handleIdentifier(function_name, line, node.jjtGetChild(0), symbols);
        } else if(node.jjtGetChild(0) instanceof ASTACCESS_ARRAY){
            ArrayList<Symbol.SymbolType> symbols = new ArrayList<>();
            symbols.add(Symbol.SymbolType.INT_ARRAY);
            handleIdentifier(function_name, line, node.jjtGetChild(0).jjtGetChild(0), symbols);
        } else if(!(node.jjtGetChild(0) instanceof ASTINT)){
            semanticError("Bad operand types for binary operator '<'", function_name, line);
        }
        if(node.jjtGetChild(1) instanceof ASTIDENTIFIER){
            ArrayList<Symbol.SymbolType> symbols = new ArrayList<>();
            symbols.add(Symbol.SymbolType.INT);
            handleIdentifier(function_name, line, node.jjtGetChild(1), symbols);
        } else if(node.jjtGetChild(1) instanceof ASTACCESS_ARRAY){
            ArrayList<Symbol.SymbolType> symbols = new ArrayList<>();
            symbols.add(Symbol.SymbolType.INT_ARRAY);
            handleIdentifier(function_name, line, node.jjtGetChild(1).jjtGetChild(0), symbols);
        } else if(!(node.jjtGetChild(1) instanceof ASTINT)){
            semanticError("Bad operand types for binary operator '<'", function_name, line);
        }
    }

    private void handleAND(String function_name, int line, Node node){
        if(node.jjtGetChild(0) instanceof ASTIDENTIFIER){
            ArrayList<Symbol.SymbolType> symbols = new ArrayList<>();
            symbols.add(Symbol.SymbolType.BOOLEAN);
            handleIdentifier(function_name, line, node.jjtGetChild(0), symbols);
        } else if(node.jjtGetChild(0) instanceof ASTNOT){
            handleNOT(function_name, line, node.jjtGetChild(0));
        } else if(node.jjtGetChild(0) instanceof ASTLT){
            handleLT(function_name, line, node.jjtGetChild(0));
        } else if (!(node.jjtGetChild(0) instanceof ASTTRUE || node.jjtGetChild(0) instanceof ASTFALSE)){
            semanticError("Bad operand types for binary operator '&&'", function_name, line);
        }

        if(node.jjtGetChild(1) instanceof ASTIDENTIFIER){
            ArrayList<Symbol.SymbolType> symbols = new ArrayList<>();
            symbols.add(Symbol.SymbolType.BOOLEAN);
            handleIdentifier(function_name, line, node.jjtGetChild(1), symbols);
        } else if(node.jjtGetChild(1) instanceof ASTNOT){
            if(node.jjtGetChild(1).jjtGetChild(0) instanceof ASTIDENTIFIER) {
                ArrayList<Symbol.SymbolType> symbols = new ArrayList<>();
                symbols.add(Symbol.SymbolType.BOOLEAN);
                handleIdentifier(function_name, line, node.jjtGetChild(1).jjtGetChild(0), symbols);
            } else if (!(node.jjtGetChild(1).jjtGetChild(0) instanceof ASTTRUE || node.jjtGetChild(1).jjtGetChild(0) instanceof ASTFALSE)){
                semanticError("Bad operand types for binary operator '&&'", function_name, line);
            }
        } else if(node.jjtGetChild(1) instanceof ASTLT){
            handleLT(function_name, line, node.jjtGetChild(1));
        } else if (!(node.jjtGetChild(1) instanceof ASTTRUE || node.jjtGetChild(1) instanceof ASTFALSE)){
            semanticError("Bad operand types for binary operator '&&'", function_name, line);
        }
    }

    private void handleIdentifier(String function_name, int line, Node node, ArrayList<Symbol.SymbolType> symbols){
        String variable_name = ((ASTIDENTIFIER) node).name;
        if(this.symbolTables.getFunctions().get(function_name).getParameters().containsKey(variable_name)){
            if(!symbols.contains(this.symbolTables.getFunctions().get(function_name).getParameters().get(variable_name).getType())){
                semanticError("Bad operand type", variable_name, line);
            }
        } else if(this.symbolTables.hasVariable(function_name, variable_name)){
            if(!symbols.contains(this.symbolTables.getVariableType(function_name, variable_name))){
                semanticError("Bad operand type", variable_name, line);
            }
            else if(!this.symbolTables.hasVariableBeenInitialized(function_name, variable_name)){
                semanticError("Variable might not have been initialized", variable_name, line);
            }

        } else {
            semanticError("Cannot find symbol", variable_name, line);
        }
    }

    private void handleMathOperationsReturnExpression(String function_name, int line, Node node){
        if(node.jjtGetChild(0) instanceof ASTIDENTIFIER){
            ArrayList<Symbol.SymbolType> symbols = new ArrayList<>();
            symbols.add(Symbol.SymbolType.INT);
            handleIdentifier(function_name, line, node.jjtGetChild(0), symbols);
        } else if(!(node.jjtGetChild(0) instanceof ASTINT)){
            semanticError("Cannot add symbol", function_name, line);
        }

        if(node.jjtGetChild(1) instanceof ASTIDENTIFIER){
            ArrayList<Symbol.SymbolType> symbols = new ArrayList<>();
            symbols.add(Symbol.SymbolType.INT);
            handleIdentifier(function_name, line, node.jjtGetChild(1), symbols);
        } else if(!(node.jjtGetChild(1) instanceof ASTINT)){
            semanticError("Cannot add symbol", function_name, line);
        }
    }

    private void printSymbolTables(){
        if(this.symbolTables.getExtendedClassName().equals("")){
            System.out.println("> Class name: " + this.symbolTables.getClassName());
        }else{
            System.out.println("> Class name: " + this.symbolTables.getClassName() + "\t> Extends: " + this.symbolTables.getExtendedClassName());
        }

        System.out.println("> Global variables:");
        for (Map.Entry<String, Symbol> entry : this.symbolTables.getGlobal_variables().entrySet()) {
            System.out.println("\t>Name: " + entry.getKey() + "\t>Type: " + entry.getValue().getTypeString());
        }

        if(this.symbolTables.getFunctions().entrySet().size() != 0)
            System.out.println("> Functions:");

        for (Map.Entry<String, FunctionSymbolTable> entry : this.symbolTables.getFunctions().entrySet()) {
            System.out.println("\t> Function name: " + entry.getKey());

            if(entry.getValue().getParameters().entrySet().size() != 0)
                System.out.println("\t\t> Parameters:");

            for (Map.Entry<String, Symbol> parameter_entry : entry.getValue().getParameters().entrySet()){
                System.out.println("\t\t\t>Name: " + parameter_entry.getValue().getAttribute() + "\tType: " + parameter_entry.getValue().getTypeString());
            }

            if(entry.getValue().getLocalVariables().entrySet().size() != 0)
                System.out.println("\t\t> Local Variables:");

            for (Map.Entry<String, Symbol> variable_entry : entry.getValue().getLocalVariables().entrySet()){
                System.out.print("\t\t\t>Name: " + variable_entry.getValue().getAttribute() + "\tType: " + variable_entry.getValue().getTypeString() + "\tLocal: ");
                int n = variable_entry.getValue().getLocalValue();
                for(int i = 1; i < n; i++){
                    System.out.print("1.");
                }
                System.out.println("1");
            }

            if(!entry.getKey().equals("main")){
                System.out.println("\t\t> Return: " + entry.getValue().getReturnSymbol().getTypeString());
            }
        }
    }

    private void semanticError(String error, String name, int line_number){
        System.out.println("> " + ++number_errors + "º Semantic Error (line "+ line_number + "): " + error + " -> "+ name);
    }

    private static void openFile(String filename){
        File file = new File(filename);
            
        try {
            fileStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("Error in file stream constructor: ");
            System.out.println("Usage: java -cp bin jmm <filePath> [-r=<n>] [-o]");
            e.printStackTrace();
        }
    }

    private static boolean readArgs(String args[]){
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
            if(!validArgs(args[i]))
                return false;
        }
        return true;
    }

    private static boolean validArgs(String arg){
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