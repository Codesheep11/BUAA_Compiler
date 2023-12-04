package Frontend.Parscial.ParseNodes;

import java.util.ArrayList;

public class InitVal extends Node{
    private Exp exp = null;
    private ArrayList<InitVal> initVals = null;

    public InitVal(Exp exp) {
        this.exp = exp;
    }

    public InitVal(ArrayList<InitVal> initVals) {
        this.initVals = initVals;
    }

    public Exp getExp() {
        return exp;
    }

    public ArrayList<InitVal> getInitVals() {
        return initVals;
    }
}
