package Frontend.Parscial.ParseNodes;

import java.util.ArrayList;

public class FuncRParams extends Node{
    private ArrayList<Exp> exps;

    public FuncRParams(ArrayList<Exp> exps) {
        this.exps = exps;
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }
}
