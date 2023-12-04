package Frontend.Parscial.ParseNodes;

import Frontend.Lexical.Word;

import java.util.ArrayList;

public class FuncDef extends Node {
    private FuncType funcType;
    private Word ident;
    private FuncFParams funcFParams = new FuncFParams(new ArrayList<FuncFParam>());
    private Block block = null;

    public FuncDef(FuncType funcType, Word word, FuncFParams funcFParams, Block block) {
        this.funcType = funcType;
        this.ident = word;
        this.funcFParams = funcFParams;
        this.block = block;
    }

    public FuncDef(FuncType funcType, Word word) {
        this.funcType = funcType;
        this.ident = word;
    }

    public Word getIdent() {
        return ident;
    }

    public FuncType getFuncType() {
        return funcType;
    }

    public FuncFParams getFuncFParams() {
        return funcFParams;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    public void setFuncFParams(FuncFParams funcFParams) {
        this.funcFParams = funcFParams;
    }

    public boolean isVoid() {
        return funcType.getFuncType().isVOIDTK();
    }
}
