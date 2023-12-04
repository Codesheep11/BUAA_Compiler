package Frontend.Parscial.ParseNodes;

import java.util.ArrayList;

public class VarDecl extends Node{
    private BType bType;
    private ArrayList<VarDef> varDefs;

    public VarDecl(BType bType, ArrayList<VarDef> varDefs) {
        this.bType = bType;
        this.varDefs = varDefs;
    }

    public ArrayList<VarDef> getVarDefs() {
        return varDefs;
    }
}
