package Frontend.Lexical;

public class Word {
    private String word;
    private Identity ident;
    private int line;

    public Word(String word, Identity ident, int line) {
        this.word = word;
        this.ident = ident;
        this.line = line;
    }

    public enum Identity {
        IDENFR, INTCON, STRCON, MAINTK, CONSTTK,
        INTTK, BREAKTK, CONTINUETK, IFTK, ELSETK,
        NOT, AND, OR, FORTK, GETINTTK,
        PRINTFTK, RETURNTK, PLUS, MINU, VOIDTK,
        MULT, DIV, MOD, LSS, LEQ,
        GRE, GEQ, EQL, NEQ, ASSIGN,
        SEMICN, COMMA, LPARENT, RPARENT, LBRACK,
        RBRACK, LBRACE, RBRACE
    }

    @Override
    public String toString() {
        return ident + " " + word;
//        return line + ":" + ident + " " + word;
    }

    public int getLine() {
        return line;
    }

    public String getWord() {
        return word;
    }

    public boolean isIDENFR() {
        return ident == Identity.IDENFR;
    }

    public boolean isINTCON() {
        return ident == Identity.INTCON;
    }

    public boolean isSTRCON() {
        return ident == Identity.STRCON;
    }

    public boolean isMAINTK() {
        return ident == Identity.MAINTK;
    }

    public boolean isCONSTTK() {
        return ident == Identity.CONSTTK;
    }

    public boolean isINTTK() {
        return ident == Identity.INTTK;
    }

    public boolean isBREAKTK() {
        return ident == Identity.BREAKTK;
    }

    public boolean isCONTINUETK() {
        return ident == Identity.CONTINUETK;
    }

    public boolean isIFTK() {
        return ident == Identity.IFTK;
    }

    public boolean isELSETK() {
        return ident == Identity.ELSETK;
    }

    public boolean isNOT() {
        return ident == Identity.NOT;
    }

    public boolean isAND() {
        return ident == Identity.AND;
    }

    public boolean isOR() {
        return ident == Identity.OR;
    }

    public boolean isFORTK() {
        return ident == Identity.FORTK;
    }

    public boolean isGETINTTK() {
        return ident == Identity.GETINTTK;
    }

    public boolean isPRINTFTK() {
        return ident == Identity.PRINTFTK;
    }

    public boolean isRETURNTK() {
        return ident == Identity.RETURNTK;
    }

    public boolean isPLUS() {
        return ident == Identity.PLUS;
    }

    public boolean isMINU() {
        return ident == Identity.MINU;
    }

    public boolean isVOIDTK() {
        return ident == Identity.VOIDTK;
    }

    public boolean isMULT() {
        return ident == Identity.MULT;
    }

    public boolean isDIV() {
        return ident == Identity.DIV;
    }

    public boolean isMOD() {
        return ident == Identity.MOD;
    }

    public boolean isLSS() {
        return ident == Identity.LSS;
    }

    public boolean isLEQ() {
        return ident == Identity.LEQ;
    }

    public boolean isGRE() {
        return ident == Identity.GRE;
    }

    public boolean isGEQ() {
        return ident == Identity.GEQ;
    }

    public boolean isEQL() {
        return ident == Identity.EQL;
    }

    public boolean isNEQ() {
        return ident == Identity.NEQ;
    }

    public boolean isASSIGN() {
        return ident == Identity.ASSIGN;
    }

    public boolean isSEMICN() {
        return ident == Identity.SEMICN;
    }

    public boolean isCOMMA() {
        return ident == Identity.COMMA;
    }

    public boolean isLPARENT() {
        return ident == Identity.LPARENT;
    }

    public boolean isRPARENT() {
        return ident == Identity.RPARENT;
    }

    public boolean isLBRACK() {
        return ident == Identity.LBRACK;
    }

    public boolean isRBRACK() {
        return ident == Identity.RBRACK;
    }

    public boolean isLBRACE() {
        return ident == Identity.LBRACE;
    }

    public boolean isRBRACE() {
        return ident == Identity.RBRACE;
    }
}
