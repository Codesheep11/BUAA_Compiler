package Frontend.Parscial.ParseNodes.Stmts;

import Frontend.Lexical.Word;

public class StmtBC extends Stmt {
    private Word bOrC;

    public StmtBC(Word bOrC) {
        this.bOrC = bOrC;
    }

    public boolean isBreak() {
        return bOrC.isBREAKTK();
    }
}
