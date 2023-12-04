package Frontend.Parscial.ParseNodes;

import Frontend.Lexical.Word;
import Frontend.Parscial.Symbol;

import java.util.ArrayList;

import static Frontend.Parscial.Parser.curST;

public class LVal extends Node {
    private Word ident;
    private ArrayList<Exp> exps;

    public LVal(Word ident, ArrayList<Exp> exps) {
        this.ident = ident;
        this.exps = exps;
    }

    public Symbol.SymbolType getDataType() {
        Symbol.SymbolType type = curST.getSymbol(ident.getWord()).getType();
        if (type == Symbol.SymbolType.INT) {
            return Symbol.SymbolType.INT;
        } else if (type == Symbol.SymbolType.ARRAY1) {
            if (exps.size() == 0) {
                return Symbol.SymbolType.ARRAY1;
            } else {
                return Symbol.SymbolType.INT;
            }
        } else {
            if (exps.size() == 0) {
                return Symbol.SymbolType.ARRAY2;
            } else if (exps.size() == 1) {
                return Symbol.SymbolType.ARRAY1;
            } else {
                return Symbol.SymbolType.INT;
            }
        }
    }

    public int getVal() {
        return curST.getVal(this);
    }

    public Word getIdent() {
        return ident;
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }
}
