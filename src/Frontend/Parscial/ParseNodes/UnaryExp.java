package Frontend.Parscial.ParseNodes;

import Frontend.Lexical.Word;
import Frontend.Parscial.Symbol;

import java.util.ArrayList;

import static Frontend.Parscial.Parser.curST;

public class UnaryExp extends Node {
    private Word ident = null;
    private FuncRParams funcRParams = null;
    private PrimaryExp primaryExp = null;
    private UnaryOp unaryOp = null;
    private UnaryExp unaryExp = null;


    public UnaryExp(Word ident, FuncRParams funcRParams) {
        this.ident = ident;
        this.funcRParams = funcRParams;
    }

    public UnaryExp(Word ident) {
        this.ident = ident;
    }

    public UnaryExp(PrimaryExp primaryExp) {
        this.primaryExp = primaryExp;
    }


    public UnaryExp(UnaryOp unaryOp, UnaryExp unaryExp) {
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
    }


    public Symbol.SymbolType getDataType() {
        if (ident != null) {
            return curST.getSymbol(ident.getWord()).getType();
        } else if (primaryExp != null) {
            return primaryExp.getDataType();
        } else {
            return unaryExp.getDataType();
        }
    }

    public int getVal() {
        if (primaryExp != null) {
            return primaryExp.getVal();
        } else {
            if (unaryOp.getWord().isPLUS()) {
                return unaryExp.getVal();
            } else {
                return -1 * unaryExp.getVal();
            }
        }
    }

    public Word getIdent() {
        return ident;
    }

    public PrimaryExp getPrimaryExp() {
        return primaryExp;
    }

    public UnaryExp getUnaryExp() {
        return unaryExp;
    }

    public UnaryOp getUnaryOp() {
        return unaryOp;
    }

    public ArrayList<Exp> getFuncRParams() {
        return funcRParams.getExps();
    }
}
