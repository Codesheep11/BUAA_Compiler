

package Frontend.Parscial.ParseNodes;

import Frontend.Lexical.Word;
import Frontend.Parscial.Symbol;

import java.util.ArrayList;

public class ConstDef extends Node{
    private Word ident = null;
    private ArrayList<ConstExp> constExps = new ArrayList<>();
    private ConstInitVal constInitVal = null;

    public ConstDef(Word word, ArrayList<ConstExp> constExps, ConstInitVal constInitVal) {
        this.ident = word;
        this.constExps = constExps;
        this.constInitVal = constInitVal;
    }

    public Word getIdent() {
        return ident;
    }

    public ArrayList<ConstExp> getConstExps() {
        return constExps;
    }

    public Symbol.SymbolType getDataType() {
        int dim = constExps.size();
        if (dim == 0) {
            return Symbol.SymbolType.INT;
        } else if (dim == 1) {
            return Symbol.SymbolType.ARRAY1;
        } else {
            return Symbol.SymbolType.ARRAY2;
        }
    }

    public ConstInitVal getConstInitVal() {
        return constInitVal;
    }
}
