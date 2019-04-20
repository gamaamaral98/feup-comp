package symbol;

public class Symbol {

    public enum SymbolType {
        INT, BOOLEAN, INT_ARRAY, STRING_ARRAY, IDENTIFIER
    }

    private String attribute = null;
    private SymbolType type;
    private boolean init = false;

    private int local_value;

    private boolean booleanValue;
    private int intValue;
    private int[] arrayValue;

    // If type is IDENTIFIER
    private String identifier_name;

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

    public boolean setBooleanValue(boolean booleanValue) {
        if(type != SymbolType.BOOLEAN){
            System.out.println("Wrong variable type");
            return false;
        }
        this.booleanValue = booleanValue;
        this.init = true;
        return true;
    }

    public boolean setIntValue(int intValue) {
        if(type != SymbolType.INT){
            System.out.println("Wrong variable type");
            return false;
        }
        this.intValue = intValue;
        this.init = true;
        return true;
    }

    public boolean setArrayValue(int[] arrayValue) {
        if(type != SymbolType.INT_ARRAY){
            System.out.println("Wrong variable type");
            return false;
        }
        this.arrayValue = arrayValue;
        this.init = true;
        return true;
    }

    public boolean setIdentifierValue(String identifier_name) {
        if(type != SymbolType.IDENTIFIER){
            System.out.println("Wrong variable type");
            return false;
        }
        this.identifier_name = identifier_name;
        this.init = true;
        return true;
    }

    public String getIdentifier_name() {
        return identifier_name;
    }
}