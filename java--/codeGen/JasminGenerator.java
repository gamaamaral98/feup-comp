package codeGen;

import symbol.ClassSymbolTable;
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

		createFileHeader();
		// manageGlobals();

		printWriter.close();
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

		printWriter.println(".class public " + symbolTable.getClassName());
		printWriter.println(".super java/lang/Object\n");
	}

	// private void manageGlobals(){

	// 	// Pela SymbolTable
	// 	Map<String, Symbol> map = symbolTable.getGlobal_variables();
	// 	map.forEach((key,value) -> {
	// 	    System.out.println(key + " -> " + value.getType());
	// 	});


	// 	// Pela AST
	// 	for(int i = 0; i < this.rootNode.jjtGetNumChildren()){

	// 		SimpleNode child = (SimpleNode) this.rootNode.jjtGetChild(i);

	// 		if(child instanceof ASTVAR_DECL){

	// 			createDeclaration((ASTVAR_DECL) child);
	// 		}
	// 	}
	// }
}