package Frontend.Parscial.ParseNodes;

import Frontend.Parscial.Symbol;

public class Exp extends Node {
    private AddExp addExp;

    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }

    public Symbol.SymbolType getDataType() {
        return addExp.getDataType();
    }

    public int getVal() {
        return addExp.getVal();
    }

    public AddExp getAddExp() {
        return addExp;
    }
}
