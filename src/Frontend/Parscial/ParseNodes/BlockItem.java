package Frontend.Parscial.ParseNodes;

import Frontend.Parscial.ParseNodes.Stmts.Stmt;
import Frontend.Parscial.ParseNodes.Stmts.StmtReturn;

public class BlockItem extends Node{
    private Decl decl = null;
    private Stmt stmt = null;

    public BlockItem(Decl decl) {
        this.decl = decl;
    }

    public BlockItem(Stmt stmt) {
        this.stmt = stmt;
    }

    public boolean isReturn() {
        if (stmt != null && stmt instanceof StmtReturn) {
            return true;
        }
        return false;
    }

    public Decl getDecl() {
        return decl;
    }

    public Stmt getStmt() {
        return stmt;
    }
}
