package Frontend.Parscial.ParseNodes.Stmts;

import Frontend.Parscial.ParseNodes.Exp;
import Frontend.Parscial.ParseNodes.LVal;

public class StmtAssign extends Stmt {
    private LVal lVal = null;
    private Exp exp = null;

    public StmtAssign(LVal lVal, Exp exp) {
        this.lVal = lVal;
        this.exp = exp;
    }

    public StmtAssign(Exp exp) {
        this.exp = exp;
    }

    public StmtAssign(LVal lVal) {
        this.lVal = lVal;
    }

    public StmtAssign() {
    }

    public Exp getExp() {
        return exp;
    }

    public LVal getlVal() {
        return lVal;
    }
}
