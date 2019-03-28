SRC = jmm

JJTREE_DIR := compiled
JJTREE_DEBUG := #-track_tokens
JJTREE_FLAGS := -output_directory=$(JJTREE_DIR)

JAVACC_DIR := compiled
JAVACC_DEBUG := -debug_lookahead #-debug_token_manager
JAVACC_FLAGS := -output_directory=$(JAVACC_DIR) #-debug_parser

JAVAC_DIR := bin
JAVAC_DEBUG := -g
JAVAC_FLAGS := -d $(JAVAC_DIR) -sourcepath $(JAVACC_DIR)

TEST_DIR := test
TEST_FILE := FindMaximum.java

all: mkdir
	jjtree $(JJTREE_FLAGS) $(SRC).jjt
	javacc $(JAVACC_FLAGS) $(JJTREE_DIR)/$(SRC).jj
	javac  $(JAVAC_FLAGS)  $(JAVACC_DIR)/$(SRC).java
	cp $(JAVAC_DIR)/$(SRC).class .

debug: mkdir
	jjtree $(JJTREE_FLAGS) $(JJTREE_DEBUG) $(SRC).jjt
	javacc $(JAVACC_FLAGS) $(JAVACC_DEBUG) $(JJTREE_DIR)/$(SRC).jj
	javac  $(JAVAC_FLAGS)  $(JAVAC_DEBUG)  $(JAVACC_DIR)/$(SRC).java
	cp $(JAVAC_DIR)/$(SRC).class .

mkdir:
	@mkdir -p compiled/ bin/ $(JJTREE_DIR)

clean:
	@rm -f bin/* compiled/* $(SRC).jj *.class

run:
	java -cp ${JAVAC_DIR} ${SRC} ${TEST_DIR}/${TEST_FILE}
