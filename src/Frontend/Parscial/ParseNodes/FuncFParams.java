package Frontend.Parscial.ParseNodes;

import java.util.ArrayList;

public class FuncFParams extends Node{
    private ArrayList<FuncFParam> funcFParams;

    public FuncFParams(ArrayList<FuncFParam> funcFParams) {
        this.funcFParams = funcFParams;
    }

    public ArrayList<FuncFParam> getFuncFParams() {
        return funcFParams;
    }
}
