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

		this.printWriter.println("\n\n; methods");

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

		this.printWriter.println("\n\t.limit locals " + fst.getParameters().size() + " (not sure tambem)");
		this.printWriter.println("\t.limit stack " + "(idk calcular isto)\n");

		manageMethodBody((SimpleNode) method.jjtGetChild(3));
		// manageMethodReturn((SimpleNode) method.jjtGetChild(4));

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

	private void manageMethodBody(SimpleNode body){

		for(int i = 0; i < body.jjtGetNumChildren(); i++){

			if(body.jjtGetChild(i) instanceof ASTVAR_DECL){
				System.out.println("ASTVAR_DECL");
			} 
			else if(body.jjtGetChild(i) instanceof ASTASSIGN){
				System.out.println("ASTASSIGN");
			}
			else if(body.jjtGetChild(i) instanceof ASTASSIGN_ARRAY){
				System.out.println("ASTASSIGN_ARRAY");
			}
			else if(body.jjtGetChild(i) instanceof ASTCALL_FUNCTION){
				System.out.println("ASTCALL_FUNCTION");
			}
		}
		System.out.println("---");
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