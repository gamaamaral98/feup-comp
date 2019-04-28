package codeGen;

import symbol.*;
import parser.*;

import java.io.*;
import java.util.*;

// CTRL+F "FALTA:" para ver o que resta fazer
public class JasminGenerator{

	private ClassSymbolTable symbolTable;
	private SimpleNode rootNode;

	private PrintWriter printWriter;

	/*
	 * Constructor of the class
	 */
	public JasminGenerator(ClassSymbolTable symbolTable, SimpleNode rootNode){

		this.symbolTable = symbolTable;
		this.rootNode = (SimpleNode) rootNode.jjtGetChild(0);

		createFile();

		createFileHeader();			// .class .super
		manageFields();				// Global Variables
		manageMethods();			// Methods

		this.printWriter.close();
	}

	/*
	 * Creates the .j files
	 */
	private void createFile(){

		try{

			File file = new File("jasmin/" + this.symbolTable.getClassName() +  ".j");

			if(!file.exists())
				file.getParentFile().mkdirs();
			
			this.printWriter = new PrintWriter(file);

		} catch(IOException exception){

			exception.printStackTrace();
		}
	}

	/*
	 * Writes to the .j file the .class and the .super
	 */
	// FALTA: ter a certeza se o super é so aquilo
	private void createFileHeader(){

		this.printWriter.println(".class public " + symbolTable.getClassName());
		this.printWriter.println(".super java/lang/Object\n");
	}

	/*
	 * Writes the global variables (fields)
	 *
	 * .field <access-spec> <field-name> <descriptor>
	 */
	// FALTA: testar para saber se nao preciso no .j do public/private/static mesmo que nao exista no .java
	private void manageFields(){

		this.printWriter.println("; global variables\n");
		Map<String, Symbol> map = symbolTable.getGlobal_variables();
		map.forEach((key, value) -> {

			String str = ".field ";
			str += key + " ";
			str += value.getTypeDescriptor();

			this.printWriter.println(str);
		});
	}

	/*
	 * Iterates through every method to create the code for each one
	 */
	private void manageMethods(){

		this.printWriter.println("\n; methods");

		SimpleNode methodsNode = (SimpleNode) this.rootNode.jjtGetChild(this.rootNode.jjtGetNumChildren() - 1);
		for(int i = 0; i < methodsNode.jjtGetNumChildren(); i++){

			manageMethod((SimpleNode) methodsNode.jjtGetChild(i));
		}
	}

	/*
	 * Manages the code generation for each method
	 *
	 * .method <access-spec> <method-spec>
 	 *     <statements>
 	 * .end method
	 */
	private void manageMethod(SimpleNode method){

		if(!(method instanceof ASTMETHOD)){		// Main
			
			String methodName = "main";
			FunctionSymbolTable fst = this.symbolTable.getFunctions().get(methodName);

			manageMethodHeader(methodName, fst);
			manageMethodLimits(fst);
			manageMethodBody((SimpleNode) method.jjtGetChild(1), fst);
		}
		else{

			String methodName = ((SimpleNode) method.jjtGetChild(1)).getName();
			FunctionSymbolTable fst = this.symbolTable.getFunctions().get(methodName);
			
			manageMethodHeader(methodName, fst);
			manageMethodLimits(fst);
			manageMethodBody((SimpleNode) method.jjtGetChild(3), fst);
			manageMethodReturn((SimpleNode) method.jjtGetChild(4), fst);
		}

		this.printWriter.println(".end method");
	}

	/*
	 * Manages the code generation for the method header
	 *
	 * .method <access-spec> <method-spec>
	 */
	private void manageMethodHeader(String methodName, FunctionSymbolTable fst){

		String str = "\n.method public ";

		if(methodName.equals("main")){
			str += "static ";
		}

		str += methodName;
		str += getParametersInformation(fst);

		if(fst.getReturnSymbol() == null)
			str += "V";
		else
			str += fst.getReturnSymbol().getTypeDescriptor();

		this.printWriter.println(str);		// Contains .method <access-spec> <method-spec>
	}

	/*
	 * Manages the code generation for the method limits
	 *
	 * .limit locals
	 * .limit stack
	 */
	// FALTA: saber calcular os limites
	private void manageMethodLimits(FunctionSymbolTable fst){

		this.printWriter.println("\n\t.limit locals " + fst.getParameters().size() + " (not sure tambem)");
		this.printWriter.println("\t.limit stack " + "(idk calcular isto)\n");
	}

	/*
	 * Manages the code generation for the method body
	 *
	 * <statements>
	 */
	// FALTA: 
	// 1. true; / false; (isto faz sentido no .j?);
	// expressions
	private void manageMethodBody(SimpleNode body, FunctionSymbolTable fst){

		for(int i = 0; i < body.jjtGetNumChildren(); i++){

			if(body.jjtGetChild(i) instanceof ASTASSIGN)
				manageASSIGN((SimpleNode) body.jjtGetChild(i), fst);

			else if(body.jjtGetChild(i) instanceof ASTCALL_FUNCTION){
				manageCALL_FUNCTION((SimpleNode) body.jjtGetChild(i), fst);
				this.printWriter.println("\tpop\n");
			}
		}
	}

	/*
	 * Manages the code generation for the method return
	 */
	// FALTA: pôr a funcionar com expressoes/funções
	private void manageMethodReturn(SimpleNode returnAux, FunctionSymbolTable fst){

		SimpleNode ret = (SimpleNode) returnAux.jjtGetChild(0);

		if(ret instanceof ASTIDENTIFIER){

			// There is no difference between being a boolean or an int
			// Local/Param:  iload_X -> ireturn
			// Global:       aload_0 -> getfield(banana) -> ireturn
			
			String retName = ret.getName();
			if(isGlobal(retName)){

				this.printWriter.println("\taload_0");
				writeGetfield(ret);
			}
			else{

				int index = getNodeIndex(retName, fst);
				this.printWriter.println("\tiload_" + Integer.toString(index));
			}
			this.printWriter.println("\tireturn\n");
		}
		else if(ret instanceof ASTINT){
			
			int value = Integer.parseInt(ret.getValueInt());
			writeINT(value);
			this.printWriter.println("\tireturn\n");
		}
		else if(ret instanceof ASTTRUE || ret instanceof ASTFALSE){
			
			String value = ret.getValueBoolean();
			writeBOOLEAN(value);

			this.printWriter.println("\tireturn\n");
		}
		else if(ret instanceof ASTCALL_FUNCTION){

			manageCALL_FUNCTION(ret, fst);
			this.printWriter.println("\tireturn\n");
		}
		else{	// No return

			this.printWriter.println("\treturn");
		}
		// add, sub, div, mul, and, not, this, <, (), 
	}

	/*
	 * Manages the code generation for ASSIGN nodes
	 */
	// FALTA: pôr a funcionar com expressoes/new class por exemplo
	private void manageASSIGN(SimpleNode node, FunctionSymbolTable fst){

		if(!isGlobal(((SimpleNode) node.jjtGetChild(0)).getName()))
			manageParamLocalASSIGN(node, fst);
		else
			manageGlobalASSIGN(node, fst);
	}

	/*
	 * Manages the code generation for ASSIGN nodes for parameters and local variables
	 */
	// FALTA: pôr a funcionar com expressoes
	private void manageParamLocalASSIGN(SimpleNode node, FunctionSymbolTable fst){

		SimpleNode lhs = ((SimpleNode) node.jjtGetChild(0));
		String lhsName = lhs.getName();
		SimpleNode rhs = ((SimpleNode) node.jjtGetChild(1));

		int index = getNodeIndex(lhsName, fst);

		if(rhs instanceof ASTINT){

			int value = Integer.parseInt(rhs.getValueInt());
			writeINT(value);
		}
		else if(rhs instanceof ASTTRUE || rhs instanceof ASTFALSE){

			writeBOOLEAN(rhs.getValueBoolean());
		}
		else if(rhs instanceof ASTIDENTIFIER){

			String rhsName = rhs.getName(); 
			if(isGlobal(rhsName)){

				this.printWriter.println("\taload_0");
				writeGetfield(rhs);
			}
			else{

				int index2 = getNodeIndex(rhsName, fst);
				this.printWriter.println("\tiload_" + index2);
			}
		}
		else if(rhs instanceof ASTCALL_FUNCTION){

			manageCALL_FUNCTION(rhs, fst);
		}
		this.printWriter.println("\tistore_" + Integer.toString(index) + "\n");
	}

	/*
	 * Manages the code generation for ASSIGN nodes for global variables
	 */
	// FALTA: pôr a funcionar com expressoes
	private void manageGlobalASSIGN(SimpleNode node, FunctionSymbolTable fst){

		SimpleNode lhs = ((SimpleNode) node.jjtGetChild(0));
		SimpleNode rhs = ((SimpleNode) node.jjtGetChild(1));

		if(rhs instanceof ASTINT){

			this.printWriter.println("\taload_0");

			int value = Integer.parseInt(rhs.getValueInt());
			writeINT(value);
			writePutfield(lhs);
		}
		else if(rhs instanceof ASTTRUE || rhs instanceof ASTFALSE){

			this.printWriter.println("\taload_0");
			writeBOOLEAN(rhs.getValueBoolean());
			writePutfield(lhs);
		}
		else if(rhs instanceof ASTIDENTIFIER){

			String rhsName = rhs.getName(); 
			if(isGlobal(rhsName)){

				this.printWriter.println("\taload_0");
				this.printWriter.println("\taload_0");

				writeGetfield(rhs);
				writePutfield(lhs);
			}
			else{

				int index2 = getNodeIndex(rhsName, fst);

				this.printWriter.println("\taload_0");
				this.printWriter.println("\tiload_" +  Integer.toString(index2));

				writePutfield(lhs);
			}
		}
		else if(rhs instanceof ASTCALL_FUNCTION){

			manageCALL_FUNCTION(rhs, fst);
			writePutfield(lhs);
		}
		this.printWriter.println();
	}

	/*
	 * Manages the code generation for CALL_FUNCTION nodes
	 */
	private void manageCALL_FUNCTION(SimpleNode node, FunctionSymbolTable fst){

		SimpleNode child = (SimpleNode) node.jjtGetChild(0);

		if(child instanceof ASTIDENTIFIER){

			String identifierName = child.getName();

			if(isGlobal(identifierName)){

				this.printWriter.println("\taload_0");
				writeGetfield(child);
			}
			else{

				int index = getNodeIndex(identifierName, fst);
				this.printWriter.println("\taload_" + index);
			}
		}
		else if(child instanceof ASTCALL_FUNCTION){

			manageCALL_FUNCTION(child, fst);
		}
		manageCALL_ARGUMENTS((SimpleNode) node.jjtGetChild(2), fst);
		manageFUNCTION((SimpleNode) node.jjtGetChild(1));
	}

	private void manageCALL_ARGUMENTS(SimpleNode node, FunctionSymbolTable fst){

		// Args podem ser: int, true/false, call_function, identifier
		// expressions
		for(int i = 0; i < node.jjtGetNumChildren(); i++){

			SimpleNode child = (SimpleNode) node.jjtGetChild(i);

			if(child instanceof ASTINT){

				int value = Integer.parseInt(child.getValueInt());
				writeINT(value);
			}
			else if(child instanceof ASTTRUE || child instanceof ASTFALSE){

				String value = child.getValueBoolean();
				writeBOOLEAN(value);
			}
			else if(child instanceof ASTIDENTIFIER){

				String childName = child.getName();
				if(isGlobal(childName)){

					this.printWriter.println("\taload_0");
					writeGetfield(child);
				}
				else{

					int index = getNodeIndex(childName, fst);
					this.printWriter.println("\taload_" + index);
				}
			}
			// else if(child instanceof ASTADD){ //expressions

			// }
			else if(child instanceof ASTCALL_FUNCTION){
				
				manageCALL_FUNCTION(child, fst);
			}
		}
	}


	private void manageFUNCTION(SimpleNode node){

		String invokeStr = "\tinvokevirtual ";
		invokeStr += "java/lang/" + this.symbolTable.getClassName() + "/" + node.getName();
		invokeStr += getParametersInformation(this.symbolTable.getFunctions().get(node.getName()));
		invokeStr += this.symbolTable.getFunctions().get(node.getName()).getReturnSymbol().getTypeDescriptor();
		this.printWriter.println(invokeStr);
	}

	/*
	 * Manages the code generation for the ASSIGN of INT's
	 */
	private void writeINT(int value){

		if(value >= 0 && value <= 5)
			this.printWriter.println("\ticonst_" + Integer.toString(value));
		else if(value == -1)
			this.printWriter.println("\ticonst_m1");
		else if(value >= -128 && value <= 127)
			this.printWriter.println("\tbipush " + Integer.toString(value));
		else if(value >= -32768 && value <= 32767)
			this.printWriter.println("\tsipush " + Integer.toString(value));
		else
			this.printWriter.println("\tldc " + Integer.toString(value));
	}
	/*
	 * Manages the code generation for the ASSIGN of BOOLEAN's
	 */
	private void writeBOOLEAN(String value){

		if(value.equals("true"))
			this.printWriter.println("\ticonst_1");
		else
			this.printWriter.println("\ticonst_0");
	}

	/*
	 * Manages the code generation for "getfield"
	 */
	private void writeGetfield(SimpleNode var){

		// FALTA: NOT SURE ABOUT THE CLASS NAME
		String getfieldStr = "\tgetfield ";
		getfieldStr += "java/lang/" + this.symbolTable.getClassName() + "/" + var.getName() + " ";
		getfieldStr += this.symbolTable.getGlobal_variables().get(var.getName()).getTypeDescriptor();
		this.printWriter.println(getfieldStr);
	}

	/*
	 * Manages the code generation for "putfield"
	 */
	private void writePutfield(SimpleNode var){

		// FALTA: NOT SURE ABOUT THE CLASS NAME
		String putfieldStr = "\tputfield ";
		putfieldStr += "java/lang/" + this.symbolTable.getClassName() + "/" + var.getName() + " ";
		putfieldStr += this.symbolTable.getGlobal_variables().get(var.getName()).getTypeDescriptor();
		this.printWriter.println(putfieldStr);
	}

	/*
	 * Checks whether a variable is global or not
	 * Returns true if it is global, false if not
	 */
	private boolean isGlobal(String name){

		if(this.symbolTable.getGlobal_variables().get(name) != null)
			return true;
		else
			return false;
	}

	/*
	 * Returns the index of a parameter or local variable inside a method
	 */
	private int getNodeIndex(String name, FunctionSymbolTable fst){

		Map<String, Symbol> map;
		int index;

		if(fst.getParameters().get(name) != null){
			map = fst.getParameters();
			index = 1;
		}
		else{
			map = fst.getLocalVariables();
			index = 1 + fst.getParameters().size();
		}

		for (Map.Entry<String, Symbol> entry : map.entrySet()) {
		    if(entry.getKey().equals(name))
		    	break;
		    index++;
		}
		return index;
	}

	/*
	 * Creates a string with the code needed for the parameters
	 */
	private String getParametersInformation(FunctionSymbolTable value){

		String str = "(";

		Map<String, Symbol> map2 = value.getParameters();
		for (Map.Entry<String, Symbol> entry : map2.entrySet()) {
		    str += entry.getValue().getTypeDescriptor();
		    str += ";";
		}

		str += ")";
		return str;
	}
}