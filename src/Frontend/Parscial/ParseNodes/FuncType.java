package Frontend.Parscial.ParseNodes;

import Frontend.Lexical.Word;

public class FuncType extends Node{
    private Word funcType;

    public FuncType(Word funcType) {
        this.funcType = funcType;
    }

    public Word getFuncType() {
        return funcType;
    }
}
