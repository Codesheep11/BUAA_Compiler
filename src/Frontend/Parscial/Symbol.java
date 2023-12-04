package Frontend.Parscial;

import Frontend.Parscial.ParseNodes.*;
import Frontend.Parscial.ParseNodes.Number;
import midend.ir.types.ArrayType;
import midend.ir.types.IntType;
import midend.ir.types.Type;
import midend.ir.values.Value;

import java.util.ArrayList;

public class Symbol {
    private String name;
    private boolean isConst;
    private ConstInitVal constInitVal;
    private boolean isFunc;

    private SymbolType type;

    private ArrayList<ConstExp> constExps = null;
    private FuncDef funcDef = null;

    private Value val;

    public Symbol(ConstDef constDef) {
        name = constDef.getIdent().getWord();
        isConst = true;
        isFunc = false;
        constExps = constDef.getConstExps();
        type = constDef.getDataType();
        constInitVal = constDef.getConstInitVal();
    }

    public Symbol(VarDef varDef) {
        name = varDef.getIdent().getWord();
        isConst = false;
        isFunc = false;
        constExps = varDef.getConstExps();
        type = varDef.getDataType();
    }

    public Symbol(FuncDef funcDef) {
        name = funcDef.getIdent().getWord();
        isConst = false;
        isFunc = true;
        this.funcDef = funcDef;
        if (funcDef.getFuncType().getFuncType().isVOIDTK()) {
            type = SymbolType.VOID;
        } else {
            type = SymbolType.INT;
        }
    }

    public Symbol(FuncFParam fp) {
        name = fp.getIdent().getWord();
        isConst = false;
        isFunc = false;
        constExps = fp.getConstExps();
        type = fp.getDataType();
    }

    public String getName() {
        return name;
    }

    public ArrayList<FuncFParam> getFuncFParams() {
        return funcDef.getFuncFParams().getFuncFParams();
    }

    public enum SymbolType {
        VOID, INT, ARRAY1, ARRAY2;

    }

    public SymbolType getType() {
        return type;
    }

    public boolean isConst() {
        return isConst;
    }

    public boolean isFunc() {
        return funcDef != null;
    }

    public Type genType() {
        if (type == Symbol.SymbolType.INT) {
            return new IntType(32);
        } else if (type == Symbol.SymbolType.ARRAY1) {
            int index1 = constExps.get(0).getVal();
            return new ArrayType(index1, new IntType(32));
        } else {
            int index1 = constExps.get(0).getVal();
            int index2 = constExps.get(1).getVal();
            return new ArrayType(index1, new ArrayType(index2, new IntType(32)));
        }
    }

    public int getConstVal(int index1, int index2) {
        if (this.type == SymbolType.INT) {
            return constInitVal.getConstExp().getVal();
        } else if (this.type == SymbolType.ARRAY1) {
            return constInitVal.getConstInitVals().get(index1).getConstExp().getVal();
        } else {
            return constInitVal.getConstInitVals().get(index1).getConstInitVals().get(index2).getConstExp().getVal();
        }
    }

    public void setVal(Value val) {
        this.val = val;
    }

    public Value getVal() {
        return val;
    }
}
