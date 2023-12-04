package Frontend.Parscial.ParseNodes;

public class ForStmt extends Node{
    private LVal lVal;
    private Exp exp;

    public ForStmt(LVal lVal, Exp exp) {
        this.lVal = lVal;
        this.exp = exp;
    }

    public Exp getExp() {
        return exp;
    }

    public LVal getlVal() {
        return lVal;
    }
}
