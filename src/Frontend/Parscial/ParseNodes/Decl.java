package Frontend.Parscial.ParseNodes;

public class Decl extends Node {
    private ConstDecl constDecl = null;
    private VarDecl varDecl = null;

    public Decl(ConstDecl constDecl) {
        this.constDecl = constDecl;
    }

    public Decl(VarDecl varDecl) {
        this.varDecl = varDecl;
    }

    public boolean isConstDecl() {
        return constDecl != null;
    }

    public ConstDecl getConstDecl() {
        return constDecl;
    }

    public VarDecl getVarDecl() {
        return varDecl;
    }
}
