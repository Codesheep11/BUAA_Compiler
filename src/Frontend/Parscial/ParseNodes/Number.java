package Frontend.Parscial.ParseNodes;

import Frontend.Lexical.Word;
import Frontend.Parscial.Symbol;

import static Frontend.Parscial.Parser.curST;

public class Number extends Node {
    private Word IntConst;

    public Number(Word intConst) {
        this.IntConst = intConst;
    }

    public Symbol.SymbolType getDataType() {
        return Symbol.SymbolType.INT;
    }

    public int getVal() {
        return Integer.parseInt(IntConst.getWord());
    }
}
