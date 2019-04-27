package codeGen;

import symbol.*;
import parser.*;

import java.io.*;
import java.util.*;

public class JasminGenerator{

	private ClassSymbolTable symbolTable;
	private SimpleNode rootNode;

	private PrintWriter printWriter;

	public JasminGenerator(ClassSymbolTable symbolTable, SimpleNode rootNode){

		this.symbolTable = symbolTable;
		this.rootNode = (SimpleNode) rootNode.jjtGetChild(0);

		createFile();

		createFileHeader();			// .class .super
		manageFields();				// Global Variables
		manageMethods();			// Methods

		this.printWriter.close();
	}

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

	private void createFileHeader(){

		this.printWriter.println(".class public " + symbolTable.getClassName());
		this.printWriter.println(".super java/lang/Object\n");
	}


	// .field <access-spec> <field-name> <descriptor>
	// public static final float PI;   ->   .field public static final PI F
	private void manageFields(){

		this.printWriter.println("; global variables\n");
		Map<String, Symbol> map = symbolTable.getGlobal_variables();
		map.forEach((key, value) -> {

			String str = ".field ";
			// str += "public"...idk se o .j precisa de ter isto na mesma
			str += key + " ";
			str += value.getTypeDescriptor();

			this.printWriter.println(str);
		});
	}


	private void manageMethods(){

		this.printWriter.println("\n; methods");

		SimpleNode methodsNode = (SimpleNode) this.rootNode.jjtGetChild(this.rootNode.jjtGetNumChildren() - 1);
		for(int i = 0; i < methodsNode.jjtGetNumChildren(); i++){

			manageMethod((SimpleNode) methodsNode.jjtGetChild(i));
		}
	}

	// .method <access-spec> <method-spec>
 	//     <statements>
 	// .end method
	private void manageMethod(SimpleNode method){

		String methodName = ((SimpleNode) method.jjtGetChild(1)).getName();
		FunctionSymbolTable fst = this.symbolTable.getFunctions().get(methodName);
		
		manageMethodHeader(methodName, fst);
		manageMethodLimits(fst);
		manageMethodBody((SimpleNode) method.jjtGetChild(3), fst);
		manageMethodReturn((SimpleNode) method.jjtGetChild(4), fst);

		this.printWriter.println(".end method");
	}

	private void manageMethodHeader(String methodName, FunctionSymbolTable fst){

		String str = "\n.method public ";

		if(methodName.equals("main"))
			str += "static ";

		str += methodName;
		str += getParametersInformation(fst);

		if(fst.getReturnSymbol() == null)
			str += "V";
		else
			str += fst.getReturnSymbol().getTypeDescriptor();

		this.printWriter.println(str);		// Contains .method <access-spec> <method-spec>
	}

	private void manageMethodLimits(FunctionSymbolTable fst){

		this.printWriter.println("\n\t.limit locals " + fst.getParameters().size() + " (not sure tambem)");
		this.printWriter.println("\t.limit stack " + "(idk calcular isto)\n");
	}

	private void manageMethodBody(SimpleNode body, FunctionSymbolTable fst){

		for(int i = 0; i < body.jjtGetNumChildren(); i++){

			if(body.jjtGetChild(i) instanceof ASTASSIGN)
				manageASSIGN((SimpleNode) body.jjtGetChild(i), fst);

			else if(body.jjtGetChild(i) instanceof ASTCALL_FUNCTION)
				manageCALL_FUNCTION((SimpleNode) body.jjtGetChild(i));
		}
	}

	private void manageMethodReturn(SimpleNode returnAux, FunctionSymbolTable fst){

		SimpleNode ret = (SimpleNode) returnAux.jjtGetChild(0);

		if(ret instanceof ASTIDENTIFIER){

			// There is no difference between being a boolean or an int
			// Local/Param:  iload_X -> ireturn
			// Global:       aload_0 -> getfield(banana) -> ireturn
			
			String retName = ret.getName();
			if(isGlobal(retName)){

				this.printWriter.println("\taload_0");

				// NOT SURE ABOUT THE CLASS NAME
				String getfieldStr = "\tgetfield ";
				getfieldStr += "java/lang/" + this.symbolTable.getClassName() + "/" + ret.getName() + " ";
				getfieldStr += this.symbolTable.getGlobal_variables().get(ret.getName()).getTypeDescriptor();
				this.printWriter.println(getfieldStr);
			}
			else{

				int index = getNodeIndex(retName, fst);
				this.printWriter.println("\tiload_" + Integer.toString(index));
			}
			this.printWriter.println("\tireturn\n");
		}
		else if(ret instanceof ASTINT){
			
			int value = Integer.parseInt(ret.getValueInt());
			manageINT(value);
			this.printWriter.println("\tireturn\n");
		}
		else if(ret instanceof ASTTRUE || ret instanceof ASTFALSE){
			
			String value = ret.getValueBoolean();
			manageBoolean(value);

			this.printWriter.println("\tireturn\n");
		}
		else{	// No return

			this.printWriter.println("\treturn");
		}
		// add, sub, div, mul, and, not, this, <, (), 
	}

	private void manageASSIGN(SimpleNode node, FunctionSymbolTable fst){

		if(!isGlobal(((SimpleNode) node.jjtGetChild(0)).getName()))
			manageParamLocalASSIGN(node, fst);
		else
			manageGlobalASSIGN(node, fst);
	}
	private void manageParamLocalASSIGN(SimpleNode node, FunctionSymbolTable fst){

		SimpleNode lhs = ((SimpleNode) node.jjtGetChild(0));
		String lhsName = lhs.getName();
		SimpleNode rhs = ((SimpleNode) node.jjtGetChild(1));

		int index = getNodeIndex(lhsName, fst);

		if(rhs instanceof ASTINT){

			int value = Integer.parseInt(rhs.getValueInt());
			manageINT(value);
		}
		else if(rhs instanceof ASTTRUE || rhs instanceof ASTFALSE){

			manageBoolean(rhs.getValueBoolean());
		}
		else if(rhs instanceof ASTIDENTIFIER){

			String rhsName = rhs.getName(); 
			if(isGlobal(rhsName)){

				this.printWriter.println("\taload_0");

				// NOT SURE ABOUT THE CLASS NAME
				String getfieldStr = "\tgetfield ";
				getfieldStr += "java/lang/" + this.symbolTable.getClassName() + "/" + rhs.getName() + " ";
				getfieldStr += this.symbolTable.getGlobal_variables().get(rhs.getName()).getTypeDescriptor();
				this.printWriter.println(getfieldStr);
			}
			else{

				int index2 = getNodeIndex(rhsName, fst);
				this.printWriter.println("\tiload_" + index2);
			}
		}
		this.printWriter.println("\tistore_" + Integer.toString(index) + "\n");
	}
	private void manageGlobalASSIGN(SimpleNode node, FunctionSymbolTable fst){

		SimpleNode lhs = ((SimpleNode) node.jjtGetChild(0));
		SimpleNode rhs = ((SimpleNode) node.jjtGetChild(1));

		if(rhs instanceof ASTINT){

			this.printWriter.println("\taload_0");

			int value = Integer.parseInt(rhs.getValueInt());
			manageINT(value);

			// NOT SURE ABOUT THE CLASS NAME
			String putfieldStr = "\tputfield ";
			putfieldStr += this.symbolTable.getClassName() + "/" + lhs.getName() + " ";
			putfieldStr += this.symbolTable.getGlobal_variables().get(lhs.getName()).getTypeDescriptor();
			this.printWriter.println(putfieldStr + "\n");
		}
		else if(rhs instanceof ASTTRUE || rhs instanceof ASTFALSE){

			this.printWriter.println("\taload_0");
			manageBoolean(rhs.getValueBoolean());

			// NOT SURE ABOUT THE CLASS NAME
			String putfieldStr = "\tputfield ";
			putfieldStr += "java/lang/" + this.symbolTable.getClassName() + "/" + lhs.getName() + " ";
			putfieldStr += this.symbolTable.getGlobal_variables().get(lhs.getName()).getTypeDescriptor();
			this.printWriter.println(putfieldStr + "\n");
		}
		else if(rhs instanceof ASTIDENTIFIER){

			String rhsName = rhs.getName(); 
			if(isGlobal(rhsName)){

				this.printWriter.println("\taload_0");
				this.printWriter.println("\taload_0");

				// NOT SURE ABOUT THE CLASS NAME
				String getfieldStr = "\tgetfield ";
				getfieldStr += "java/lang/" + this.symbolTable.getClassName() + "/" + rhs.getName() + " ";
				getfieldStr += this.symbolTable.getGlobal_variables().get(rhs.getName()).getTypeDescriptor();
				this.printWriter.println(getfieldStr);

				// NOT SURE ABOUT THE CLASS NAME
				String putfieldStr = "\tputfield ";
				putfieldStr += "java/lang/" + this.symbolTable.getClassName() + "/" + lhs.getName() + " ";
				putfieldStr += this.symbolTable.getGlobal_variables().get(lhs.getName()).getTypeDescriptor();
				this.printWriter.println(putfieldStr + "\n");
			}
			else{

				int index2 = getNodeIndex(rhsName, fst);

				this.printWriter.println("\taload_0");
				this.printWriter.println("\tiload_" +  Integer.toString(index2));

				// NOT SURE ABOUT THE CLASS NAME
				String putfieldStr = "\tputfield ";
				putfieldStr += "java/lang/" + this.symbolTable.getClassName() + "/" + lhs.getName() + " ";
				putfieldStr += this.symbolTable.getGlobal_variables().get(lhs.getName()).getTypeDescriptor();
				this.printWriter.println(putfieldStr + "\n");
			}
		}
	}

	private void manageINT(int value){

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
	private void manageBoolean(String value){

		if(value.equals("true"))
			this.printWriter.println("\ticonst_1");
		else
			this.printWriter.println("\ticonst_0");
	}

	private boolean isGlobal(String name){

		if(this.symbolTable.getGlobal_variables().get(name) != null)
			return true;
		else
			return false;
	}

	private int getNodeIndex(String name, FunctionSymbolTable fst){

		Map<String, Symbol> map;
		int index;

		// if(this.symbolTable.getGlobal_variables().get(name) != null){
		// 	map = this.symbolTable.getGlobal_variables();
		// 	index = 420;
		// 	return index;
		// }
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

	private void manageCALL_FUNCTION(SimpleNode node){

		this.printWriter.println("CALL_FUNCTION");
	}

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