package Frontend.Parscial.ParseNodes;

import java.util.ArrayList;

public class ConstInitVal extends Node {
    private ConstExp constExp = null;
    private ArrayList<ConstInitVal> constInitVals = null;

    public ConstInitVal(ConstExp constExp) {
        this.constExp = constExp;
    }

    public ConstInitVal(ArrayList<ConstInitVal> constInitVals) {
        this.constInitVals = constInitVals;
    }

    public ConstExp getConstExp() {
        return constExp;
    }

    public ArrayList<ConstInitVal> getConstInitVals() {
        return constInitVals;
    }
}
