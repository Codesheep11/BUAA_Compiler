package Frontend.Parscial.ParseNodes.Stmts;

import Frontend.Parscial.ParseNodes.Exp;
import Frontend.Lexical.Word;

import java.util.ArrayList;

public class StmtPrintf extends Stmt {
    private Word format;
    private ArrayList<Exp> exps = null;

    private int cntp = 0;

    public StmtPrintf(Word format, ArrayList<Exp> exps) {
        this.format = format;
        this.exps = exps;
        String str = format.getWord();
        if (str.contains("%"))
            cntp = str.length() - str.replaceAll("%", "").length();
    }

    public int getCntp() {
        return cntp;
    }

    public String getFormatString() {
        String str = format.getWord();
        return str.substring(1, str.length() - 1);
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }
}
