package Frontend.Parscial.ParseNodes;

import Frontend.Lexical.Word;
import Frontend.Parscial.Symbol;

import java.util.ArrayList;

public class VarDef extends Node{
    private Word ident;
    private ArrayList<ConstExp> constExps = new ArrayList<>();
    private InitVal initVal = null;


    public VarDef(Word word, ArrayList<ConstExp> constExps) {
        ident = word;
        this.constExps = constExps;
    }

    public VarDef(Word word, ArrayList<ConstExp> constExps, InitVal initVal) {
        ident = word;
        this.constExps = constExps;
        this.initVal = initVal;
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

    public InitVal getInitVal() {
        return initVal;
    }
}
