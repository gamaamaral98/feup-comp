package Symbol;

public class Symbol {

    private String attribute = null;
    private String type;
    private boolean init = false;

    public Symbol(String atr, String type) {
        this.attribute = atr;
        this.type = type;
    }

    public String getAtr() {
        return this.attribute;
    }

    public String getType() {
        return this.type;
    }

    public boolean getInit(){
        return this.init;
    }

    public void setAtr(String atr) {
        this.attribute = atr;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setInit() {
        this.init = true;
    }

    public void unsetInit() {
        this.init = false;
    }

    public boolean equals(Object symbol) {
        Symbol s = (Symbol) symbol;
        return this.attribute.equals(s.getAtr());
    }

}