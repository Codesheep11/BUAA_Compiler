package Frontend.Parscial;

import Frontend.Parscial.ParseNodes.LVal;
import midend.ir.values.Value;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Symbol> symbols = new HashMap<>();
    private SymbolTable preS = null;
    private ArrayList<SymbolTable> nxtSTs = new ArrayList<>();

    public STtype type;

    private String name;


    public enum STtype {
        Root,
        Func,
        Main,
        Stmt
    }

    public SymbolTable() {
        type = STtype.Root;
    }


    public SymbolTable(SymbolTable preS, STtype type, String name) {
        this.preS = preS;
        this.type = type;
        preS.addNxtST(this);
        this.name = name;
    }

    public ArrayList<SymbolTable> getNxtSTs() {
        return nxtSTs;
    }

    public void addNxtST(SymbolTable nxt) {
        nxtSTs.add(nxt);
    }

    public SymbolTable getPreS() {
        return preS;
    }

    public HashMap<String, Symbol> getSymbols() {
        return symbols;
    }

    public boolean isRoot() {
        return preS == null;
    }

    public Symbol getSymbol(String s) {
        SymbolTable ST = this;
        if (symbols.containsKey(s)) {
            return symbols.get(s);
        }
        while (!ST.isRoot()) {
            ST = ST.getPreS();
            if (ST.getSymbols().containsKey(s)) {
                return ST.getSymbols().get(s);
            }
        }
        return null;
    }

    //未定义
    public boolean find(String s) {
        SymbolTable ST = this;
        if (symbols.containsKey(s)) {
            return true;
        }
        while (!ST.isRoot()) {
            ST = ST.getPreS();
            if (ST.getSymbols().containsKey(s)) {
                return true;
            }
        }
        return false;
    }

    //重复定义
    public boolean check(String s) {
        if (symbols.containsKey(s)) {
            return true;
        }
        return false;
    }

    //填表
    public void insert(Symbol symbol) {
        symbols.put(symbol.getName(), symbol);
    }

    public STtype getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Symbol.SymbolType findFuncType() {
        SymbolTable ST = this;
        while (!ST.isRoot()) {
            String name = ST.getName();
            if (ST.getType() == STtype.Main) {
                return Symbol.SymbolType.INT;
            } else if (ST.getType() == STtype.Func) {
                return ST.getPreS().getSymbol(name).getType();
            } else {
                ST = ST.getPreS();
            }
        }
        return null;
    }

    public boolean isIntFunc() {
        return this.type == SymbolTable.STtype.Main
                || ((this.type == SymbolTable.STtype.Func)
                && (this.getPreS().getSymbol(this.getName()).getType() == Symbol.SymbolType.INT));
    }

    public int getVal(LVal lVal) {
        Symbol sym = getSymbol(lVal.getIdent().getWord());
        if (sym.getType() == Symbol.SymbolType.INT) {
            return sym.getConstVal(0, 0);
        } else if (sym.getType() == Symbol.SymbolType.ARRAY1) {
            int index1 = lVal.getExps().get(0).getVal();
            return sym.getConstVal(index1, 0);
        } else {
            int index1 = lVal.getExps().get(0).getVal();
            int index2 = lVal.getExps().get(1).getVal();
            return sym.getConstVal(index1, index2);
        }
    }

    public SymbolTable getRoot() {
        SymbolTable ST = this;
        while (!ST.isRoot()) {
            ST = ST.getPreS();
        }
        return ST;
    }

    public SymbolTable nxt(String name) {
        for (SymbolTable ST : nxtSTs) {
            if (ST.getName().equals(name)) {
                return ST;
            }
        }
        return null;
    }

    public Value getPointer(String sym) {
        SymbolTable ST = this;
        if (ST.getSymbol(sym).getVal() == null) {
            ST = ST.getPreS();
        }
        return ST.getSymbol(sym).getVal();
    }
}
