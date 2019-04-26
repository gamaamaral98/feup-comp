package codeGen;

import symbol.ClassSymbolTable;
import symbol.FunctionSymbolTable;
import symbol.Symbol;
import parser.SimpleNode;

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

		this.printWriter.println("; global variables");
		Map<String, Symbol> map = symbolTable.getGlobal_variables();
		map.forEach((key, value) -> {

			String str = ".field ";
			// Falta saber pela function symbol table se é public ou private + static
			str += "public? ";
			str += key + " ";
			str += value.getTypeDescriptor();

			this.printWriter.println(str);
		});
	}

	// .method <access-spec> <method-spec>
 	//        <statements>
 	//    .end method
	private void manageMethods(){

		this.printWriter.println("\n\n; methods");
		Map<String, FunctionSymbolTable> map = symbolTable.getFunctions();
		map.forEach((key, value) -> {

			String str = "\n.method ";

			// Falta saber pela function symbol table se é public ou private + static
			str += "public? ";
			str += key;
			str += getParametersInformation(value);
			str += value.getReturnSymbol().getTypeDescriptor();
			this.printWriter.println(str);		// Contains .method <access-spec> <method-spec>


			// STATEMENTS
			this.printWriter.println("\t<statements>");
			

			this.printWriter.println(".end method");
		});
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