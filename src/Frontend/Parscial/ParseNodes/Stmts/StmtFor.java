package Frontend.Parscial.ParseNodes.Stmts;

import Frontend.Parscial.ParseNodes.Cond;
import Frontend.Parscial.ParseNodes.ForStmt;

public class StmtFor extends Stmt{
    private ForStmt forStmt1 = null;
    private Cond cond = null;
    private ForStmt forStmt2 = null;
    private Stmt stmt = null;

    public StmtFor(ForStmt forStmt1, Cond cond, ForStmt forStmt2, Stmt stmt) {
        this.forStmt1 = forStmt1;
        this.cond = cond;
        this.forStmt2 = forStmt2;
        this.stmt = stmt;
    }

    public Cond getCond() {
        return cond;
    }

    public Stmt getStmt() {
        return stmt;
    }

    public ForStmt getForStmt1() {
        return forStmt1;
    }

    public ForStmt getForStmt2() {
        return forStmt2;
    }
}
