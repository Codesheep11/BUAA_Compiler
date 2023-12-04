package Frontend.Parscial.ParseNodes;

import Frontend.Lexical.Word;
import Frontend.Parscial.Symbol;


public class PrimaryExp extends Node {
    private Exp exp = null;
    private LVal lVal = null;
    private Number number = null;

    public PrimaryExp(Exp exp) {
        this.exp = exp;
    }

    public PrimaryExp(LVal lVal) {
        this.lVal = lVal;
    }

    public PrimaryExp(Number number) {
        this.number = number;
    }

    public Symbol.SymbolType getDataType() {
        if (exp != null) {
            return exp.getDataType();
        } else if (number != null) {
            return number.getDataType();
        } else {
            return lVal.getDataType();
        }
    }

    public int getVal() {
        if (exp != null) {
            return exp.getVal();
        } else if (number != null) {
            return number.getVal();
        } else {
            return lVal.getVal();
        }
    }

    public Exp getExp() {
        return exp;
    }

    public LVal getlVal() {
        return lVal;
    }

    public Number getNumber() {
        return number;
    }
}
