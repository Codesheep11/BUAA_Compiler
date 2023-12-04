package Frontend.Parscial.ParseNodes;

import java.util.ArrayList;

public class ConstDecl extends Node{
    private BType bType;
    private ArrayList<ConstDef> constDefs;

    public ConstDecl(BType bType, ArrayList<ConstDef> constDefs) {
        this.bType = bType;
        this.constDefs = constDefs;
    }

    public ArrayList<ConstDef> getConstDefs() {
        return constDefs;
    }
}
