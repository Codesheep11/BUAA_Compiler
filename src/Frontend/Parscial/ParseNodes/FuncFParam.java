package Frontend.Parscial.ParseNodes;

import Frontend.Lexical.Word;
import Frontend.Parscial.Symbol;

import java.util.ArrayList;

public class FuncFParam extends Node{
    private BType bType;
    private Word ident;
    private ArrayList<ConstExp> constExps = new ArrayList<>();

    public FuncFParam(BType bType, Word word) {
        this.bType = bType;
        ident = word;
    }

    public FuncFParam(BType bType, Word word, ArrayList<ConstExp> constExps) {
        this.bType = bType;
        ident = word;
        this.constExps = constExps;
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
}
