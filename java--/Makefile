SRC = jmm
JJTREE_PARSER = Parser

JJTREE_DIR := parser
JJTREE_DEBUG := #-track_tokens
JJTREE_FLAGS := -output_directory=$(JJTREE_DIR)

JAVACC_DIR := parser
JAVACC_DEBUG := -debug_lookahead #-debug_token_manager
JAVACC_FLAGS := -output_directory=$(JAVACC_DIR) #-debug_parser

JAVAC_DIR := bin
JAVAC_DEBUG := -g
JAVAC_FLAGS := -d $(JAVAC_DIR)

JASMIN_DIR := jasmin
JASMIN_JAR := $(JASMIN_DIR)/jasmin.jar

TEST_DIR := test
TEST_FILE := Life.jmm  #test file

all:
	javac $(JAVAC_FLAGS) parser/*.java symbol/*.java codeGen/*.java $(SRC).java

parser: mkdir
	jjtree $(JJTREE_FLAGS) $(JJTREE_DIR)/$(JJTREE_PARSER).jjt
	javacc $(JAVACC_FLAGS) $(JJTREE_DIR)/$(JJTREE_PARSER).jj
	javac  $(JAVAC_FLAGS)  $(JAVACC_DIR)/$(JJTREE_PARSER).java

parser_debug: mkdir
	jjtree $(JJTREE_FLAGS) $(JJTREE_DEBUG) $(JJTREE_DIR)/$(JJTREE_PARSER).jjt
	javacc $(JAVACC_FLAGS) $(JAVACC_DEBUG) $(JJTREE_DIR)/$(JJTREE_PARSER).jj
	javac  $(JAVAC_FLAGS) $(JAVAC_DEBUG) $(JAVACC_DIR)/$(JJTREE_PARSER).java

mkdir:
	@mkdir -p parser/ bin/

clean:
	@rm -f bin/* $(SRC).jj *.class $(JASMIN_DIR)/*.j

run:
	java -cp ${JAVAC_DIR} ${SRC} ${TEST_DIR}/${TEST_FILE}
	java -jar $(JASMIN_JAR) $(JASMIN_DIR)/*.j
