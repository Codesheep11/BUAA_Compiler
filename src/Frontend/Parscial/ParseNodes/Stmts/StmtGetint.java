package Frontend.Parscial.ParseNodes.Stmts;

import Frontend.Parscial.ParseNodes.LVal;

public class StmtGetint extends Stmt{
    private LVal lVal;

    public StmtGetint(LVal lVal) {
        this.lVal = lVal;
    }

    public LVal getlVal() {
        return lVal;
    }
}
