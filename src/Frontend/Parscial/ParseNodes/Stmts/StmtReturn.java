package Frontend.Parscial.ParseNodes.Stmts;

import Frontend.Parscial.ParseNodes.Exp;

public class StmtReturn extends Stmt {
    private Exp exp = null;

    public StmtReturn() {

    }

    public StmtReturn(Exp exp) {
        this.exp = exp;
    }

    public Exp getExp() {
        return exp;
    }
}
