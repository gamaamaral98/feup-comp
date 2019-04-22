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
}