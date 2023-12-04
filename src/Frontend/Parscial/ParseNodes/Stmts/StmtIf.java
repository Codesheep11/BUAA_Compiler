package Frontend.Parscial.ParseNodes.Stmts;

import Frontend.Parscial.ParseNodes.Cond;

public class StmtIf extends Stmt{
    private Cond cond;
    private Stmt ifStmt;
    private Stmt elseStmt = null;

    public StmtIf(Cond cond, Stmt ifStmt, Stmt elseStmt) {
        this.cond = cond;
        this.ifStmt = ifStmt;
        this.elseStmt = elseStmt;
    }

    public StmtIf(Cond cond, Stmt ifStmt) {
        this.cond = cond;
        this.ifStmt = ifStmt;
    }

    public Stmt getIfStmt() {
        return ifStmt;
    }

    public Stmt getElseStmt() {
        return elseStmt;
    }

    public Cond getCond() {
        return cond;
    }
}
