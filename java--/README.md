## PROJECT TITLE: Compiler of the Java-- language to Java Bytecodes

## GROUP: G13

NAME1: João Miguel Vaz Tello da Gama Amaral, NR1: 201708805, GRADE1: 17, CONTRIBUTION1: 25%

NAME2: João Nuno Rodrigues Ferreira, NR2: 201605330, GRADE2: 17, CONTRIBUTION2: 25%

NAME3: Nuno Tiago Tavares Lopes, NR3: 201605337, GRADE3: 17, CONTRIBUTION3: 25%

NAME4: Amadeu Prazeres Pereira, NR4: 201605646, GRADE4: 17, CONTRIBUTION4: 25%

GLOBAL Grade of the project: 17

## SUMMARY:
Handles Java code, generating the AST tree with the code given. Given the tree and the symbol table we can generate machine code that can be transformed into executable code with the aid of Jasmin.
The main features of the tool we developed are:
- Syntactic error controller
- Semantic analysis
- Code generation

## EXECUTE:

There are 2 possible ways of running the tools:

1. Using jar file

```sh
java -jar jmm.jar <input_file> <output_file>
```

2. Using Makefile (this option automatically uses the jasmin.jar to generate the .class file)

```sh
make #to compile the tool
make run #run script
```

PS: In the second option the test file can be changed in the Makefile and the .j file goes to the jasmin/ directory


## DEALING WITH SYNTACTIC ERRORS:
The compiler does not abort execution immediately after the first error, but reports a given number of errors (in our case 10) before aborting the execution.

In this compiler this only occurs in the while expression.


## SEMANTIC ANALYSIS:

This tools performs semantic analysis detects most of semantic errors, like:

- Redefenition of gloval variables
- Duplicate functions
- Duplicate paramenters
- Duplicate variables
- Variable redefinition
- Incompatible assign types
- Incomptatible types
- Calling `this` variable in a static function
- Undefined function
- Incompatible return types
- Missing parameters
- Bad operand type for `!`
- Bad operand type for `<`
- Bad operand type for `&&`
- ...

It also displays a waringn when a variable is defined in an if statement.


## CODE GENERATION:

The code generation of our tool makes use of the AST tree and the symbol tables, using them to get the order of the operations and to get the values, names or types of the variables, respectively. Using these, the code generation starts writing to a file the machine code provided by the Jasmin documentation.


## OVERVIEW:

We made use of JJTree and JavaCC to generate the code's AST.

The .j classes were translated into Java bytecode classes (classfiles) using the tool jasmin.

Even though we completed all "obligatory" tasks we didn't implement the register allocation and constant propagation optimizations as suggested, due to lack of time. To compensate we use templates for compiling while loops that eliminate the use of unnecessary `goto` instructions just after the conditional branch that controls if the loop shall execute another iteration or shall terminate.

## TASK DISTRIBUTION:

All the group members participated in each iteration of the project's development. Amadeu and João Ferreira worked more closely with the generation of the AST generation and code generation, while Nuno Lopes and João Amaral worked more closely with the semantic analysis.
But in the end we all know how each iteration was developed and all contributed in testing it before the release.

## PROS:

- Use templates for compiling while loops that eliminate the use of unnecessary `goto` instructions just after the conditional branch that controls if the loop shall execute another iteration or shall terminate.
- Flexible architecture that can be extended.
- Debug mode easily accessible just by changing the `DEBUG` variable to `true` in the jmm.java file.
- Custom error and warning messages (with the line in which it occurs for easy fix).


## CONS:

- Not totally optimized.
- Gives a `variable might not have been initialized` warning every time a variable is declared inside an if statement, even when it is surely initialized.
- Polimorfism not properly tested due to lack of time.
