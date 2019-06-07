package symbol;

public class Symbol {

    public enum SymbolType {
        INT, BOOLEAN, INT_ARRAY, STRING_ARRAY, IDENTIFIER
    }

    private String attribute = null;
    private SymbolType type;
    private boolean init = false;

    private int local_value;

    // If type is IDENTIFIER
    private String identifier_name;

    public Symbol(SymbolType type) {
        this.type = type;
    }

    public Symbol(String atr, SymbolType type) {
        this.attribute = atr;
        this.type = type;
    }

    public Symbol(String atr, SymbolType type, int local_value){
        this.attribute = atr;
        this.type = type;
        this.local_value = local_value;
    }

    public Symbol(String atr, SymbolType type, String identifier_name, int local_value){
        this.attribute = atr;
        this.type = type;
        this.local_value = local_value;
        this.identifier_name = identifier_name;
    }

    public Symbol(String atr, SymbolType type, String identifier_name){
        this.attribute = atr;
        this.type = type;
        this.identifier_name = identifier_name;
    }

    public String getAttribute() {
        return attribute;
    }

    public SymbolType getType() {
        return type;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public String getIdentifier_name() {
        return identifier_name;
    }

    public void setIdentifier_name(String identifier_name) {
        this.identifier_name = identifier_name;
    }

    public boolean isInit() {
        return init;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getTypeString(){
        if(type == Symbol.SymbolType.IDENTIFIER){
            return identifier_name;
        } else if(type == SymbolType.INT){
            return "int";
        } else if(type == SymbolType.INT_ARRAY){
            return "int[]";
        } else if(type == SymbolType.BOOLEAN){
            return "boolean";
        } else if(type == SymbolType.STRING_ARRAY){
            return "string[]";
        }
        return "";
    }

    public String getTypeDescriptor(){

        switch(getType()) {
            case INT:
                return "I";
            case BOOLEAN:
                return "Z";
            case INT_ARRAY:
                return "[I";
            case STRING_ARRAY:
                return "[Ljava/lang/String";
            case IDENTIFIER:
                return getIdentifier_name();
            default:
                return "V";
        }
    }

    public int getLocalValue(){
        
        return local_value;
    }
}