package Frontend.Parscial;

import Frontend.Lexical.Word;
import Frontend.MyError;
import Frontend.Parscial.ParseNodes.*;
import Frontend.Parscial.ParseNodes.Number;
import Frontend.Parscial.ParseNodes.Stmts.*;

import java.util.ArrayList;

import static Frontend.Parscial.SymbolTable.STtype.Func;

public class Parser {
    private final ArrayList<Word> tokens;
    private int index;
    private Word cur;

    private int parseForing = 0;

    private final boolean printword = true;
    private final boolean printdata = true;
    public static SymbolTable curST = new SymbolTable();

    private int stmtcnt = 0;

    public Parser(ArrayList<Word> tokens) {
        this.tokens = tokens;
        index = 0;
        cur = tokens.get(0);
    }

    private void getNext() {
        index++;
        if (index == tokens.size()) {
            cur = null;
            return;
        }
        cur = tokens.get(index);
        printWord();
    }

    private Word preRead() {
        if (index + 1 >= tokens.size()) {
            return null;
        }
        return tokens.get(index + 1);
    }

    private Word getLast() {
        return tokens.get(index - 1);
    }

    private void tokenInsertSeMICN() {
        tokens.add(index, new Word(";", Word.Identity.SEMICN, getLast().getLine()));
        cur = tokens.get(index);
    }

    private void tokenInsertRPARENT() {
        tokens.add(index, new Word(")", Word.Identity.RPARENT, getLast().getLine()));
        cur = tokens.get(index);
    }

    private void tokenInsertRBRACK() {
        tokens.add(index, new Word("]", Word.Identity.RBRACK, getLast().getLine()));
        cur = tokens.get(index);
    }

    public CompUnit parse() {
        printWord();
        CompUnit compUnit = parseCompUnit();
        return compUnit;
    }

    private CompUnit parseCompUnit() {
        ArrayList<Decl> decls = new ArrayList<>();
        while (isDecl()) {
            decls.add(parseDecl());
            getNext();
        }
        ArrayList<FuncDef> funcDefs = new ArrayList<>();
        while (isFuncDef()) {
            funcDefs.add(parseFuncDef());
            getNext();
        }

        MainFuncDef mainFuncDef = parseMainFuncDef();

        CompUnit compUnit = new CompUnit(decls, funcDefs, mainFuncDef);

        printData("CompUnit");
        return compUnit;
    }

    private Decl parseDecl() {
        Decl decl;
        if (isConstDecl()) {
            decl = new Decl(parseConstDecl());
        }
        else {
            decl = new Decl(parseVarDecl());
        }
        try {
            if (!cur.isSEMICN()) {
                throw new MyError(getLast().getLine(), MyError.ErrorType.I);
            }
        } catch (MyError e) {
            e.gather();
            tokenInsertSeMICN();
        }
//        printData("Decl");
        return decl;
    }

    private ConstDecl parseConstDecl() {
        assert cur.isCONSTTK();

        getNext();
        assert cur.isINTTK();
        BType bType = new BType(cur);

        getNext();
        ArrayList<ConstDef> constDefs = new ArrayList<>();
        ConstDef constDef = parseConstDef();
        constDefs.add(constDef);

        getNext();
        while (cur.isCOMMA()) {
            getNext();
            constDefs.add(parseConstDef());
            getNext();
        }

        assert cur.isSEMICN();
        printData("ConstDecl");
        return new ConstDecl(bType, constDefs);
    }

    private ConstDef parseConstDef() {

        assert cur.isIDENFR();
        Word ident = cur;

        getNext();
        ArrayList<ConstExp> constExps = new ArrayList<>();
        while (cur.isLBRACK()) {
            getNext();
            constExps.add(parseConstExp());
            getNext();
            assert cur.isRBRACK();
            try {
                if (!cur.isRBRACK())
                    throw new MyError(getLast().getLine(), MyError.ErrorType.K);
            } catch (MyError e) {
                e.gather();
                tokenInsertRBRACK();
            }
            getNext();
        }

        assert cur.isASSIGN();

        getNext();
        ConstInitVal constInitVal = parseConstInitVal();

        ConstDef constDef = new ConstDef(ident, constExps, constInitVal);
        try {
            if (curST.check(ident.getWord())) {
                throw new MyError(ident.getLine(), MyError.ErrorType.B);
            }
            else {
                curST.insert(new Symbol(constDef));
            }
            printData("ConstDef");
        } catch (MyError e) {
            e.gather();
        }
        return constDef;
    }

    private ConstInitVal parseConstInitVal() {
        if (cur.isLBRACE()) {
            getNext();
            ArrayList<ConstInitVal> constInitVals = new ArrayList<>();
            if (!cur.isRBRACE()) {
                constInitVals.add(parseConstInitVal());
                getNext();
                while (cur.isCOMMA()) {
                    getNext();
                    constInitVals.add(parseConstInitVal());
                    getNext();
                }
            }
            assert cur.isRBRACE();
            printData("ConstInitVal");
            return new ConstInitVal(constInitVals);
        }
        else {
            ConstExp constExp = parseConstExp();
            printData("ConstInitVal");
            return new ConstInitVal(constExp);
        }
    }

    private VarDecl parseVarDecl() {
        assert cur.isINTTK();
        BType bType = new BType(cur);

        getNext();
        ArrayList<VarDef> varDefs = new ArrayList<>();
        varDefs.add(parseVarDef());
        getNext();
        while (cur.isCOMMA()) {
            getNext();
            varDefs.add(parseVarDef());
            getNext();
        }
        assert cur.isSEMICN();

        printData("VarDecl");
        return new VarDecl(bType, varDefs);
    }

    private VarDef parseVarDef() {
        Word ident = cur;
        ArrayList<ConstExp> constExps = new ArrayList<>();
        while (preRead() != null && preRead().isLBRACK()) {
            getNext();
            getNext();
            constExps.add(parseConstExp());
            getNext();
            assert cur.isRBRACK();
            try {
                if (!cur.isRBRACK())
                    throw new MyError(getLast().getLine(), MyError.ErrorType.K);
            } catch (MyError e) {
                e.gather();
                tokenInsertRBRACK();
            }
        }
        VarDef varDef;
        if (preRead() != null && preRead().isASSIGN()) {
            getNext();
            getNext();
            InitVal initVal = parseInitVal();
            varDef = new VarDef(ident, constExps, initVal);
        }
        else {
            varDef = new VarDef(ident, constExps);
        }
        try {
            if (curST.check(ident.getWord())) {
                throw new MyError(ident.getLine(), MyError.ErrorType.B);
            }
            else {
                curST.insert(new Symbol(varDef));
            }
            printData("VarDef");
        } catch (MyError e) {
            e.gather();
        }
        return varDef;
    }

    private InitVal parseInitVal() {
        if (cur.isLBRACE()) {
            getNext();
            ArrayList<InitVal> initVals = new ArrayList<>();
            if (!cur.isRBRACE()) {
                initVals.add(parseInitVal());
                getNext();
                while (cur.isCOMMA()) {
                    getNext();
                    initVals.add(parseInitVal());
                    getNext();
                }
            }
            assert cur.isRBRACE();
            printData("InitVal");
            return new InitVal(initVals);
        }
        else {
            Exp exp = parseExp();
            printData("InitVal");
            return new InitVal(exp);
        }
    }

    private FuncDef parseFuncDef() {

        FuncType funcType = parseFuncType();

        getNext();
        assert cur.isIDENFR();
        Word ident = cur;

        getNext();
        assert cur.isLPARENT();

        getNext();
        FuncDef funcDef = new FuncDef(funcType, ident);
        try {
            if (curST.check(ident.getWord())) {
                throw new MyError(ident.getLine(), MyError.ErrorType.B);
            }
            else {
                curST.insert(new Symbol(funcDef));
            }
        } catch (MyError e) {
            e.gather();
        }

        curST = new SymbolTable(curST, Func, ident.getWord());
        if (!cur.isRPARENT()) {
            if (cur.isINTTK()) {
                FuncFParams funcFParams = parseFuncFParams();
                funcDef.setFuncFParams(funcFParams);
                getNext();
            }
            else {
                try {
                    if (!cur.isRPARENT()) {
                        throw new MyError(getLast().getLine(), MyError.ErrorType.J);
                    }
                } catch (MyError e) {
                    e.gather();
                    tokenInsertRPARENT();
                }
            }
        }
        try {
            if (!cur.isRPARENT()) {
                throw new MyError(getLast().getLine(), MyError.ErrorType.J);
            }
        } catch (MyError e) {
            e.gather();
            tokenInsertRPARENT();
        }
        getNext();
        Block block = parseBlock();
        funcDef.setBlock(block);


        printData("FuncDef");
        curST = curST.getPreS();

        return funcDef;
    }

    private MainFuncDef parseMainFuncDef() {
        curST = new SymbolTable(curST, SymbolTable.STtype.Main, "main");
        assert cur.isINTTK();

        getNext();
        assert cur.isMAINTK();

        getNext();
        assert cur.isLPARENT();

        getNext();
        assert cur.isRPARENT();
        try {
            if (!cur.isRPARENT()) {
                throw new MyError(getLast().getLine(), MyError.ErrorType.J);
            }
        } catch (MyError e) {
            e.gather();
            tokenInsertRPARENT();
        }
        getNext();
        MainFuncDef mainFuncDef = new MainFuncDef(parseBlock());
        printData("MainFuncDef");
        curST = curST.getPreS();
        return mainFuncDef;
    }

    private FuncType parseFuncType() {
        assert cur.isVOIDTK() || cur.isINTTK();
        printData("FuncType");
        return new FuncType(cur);
    }

    private FuncFParams parseFuncFParams() {
        ArrayList<FuncFParam> funcFParams = new ArrayList<>();
        FuncFParam funcFParam = parseFuncFParam();
        funcFParams.add(funcFParam);

        while (preRead() != null && preRead().isCOMMA()) {
            getNext();
            getNext();
            funcFParams.add(parseFuncFParam());
        }
        printData("FuncFParams");
        return new FuncFParams(funcFParams);
    }

    private FuncFParam parseFuncFParam() {
        BType bType = new BType(cur);

        getNext();
        Word ident = cur;
        FuncFParam fp;
        if (preRead() != null && preRead().isLBRACK()) {
            getNext();


            getNext();

            try {
                if (!cur.isRBRACK())
                    throw new MyError(getLast().getLine(), MyError.ErrorType.K);
            } catch (MyError e) {
                e.gather();
                tokenInsertRBRACK();
            }
            ArrayList<ConstExp> constExps = new ArrayList<>();
            constExps.add(null);
            while (preRead() != null && preRead().isLBRACK()) {
                getNext();
                getNext();
                constExps.add(parseConstExp());
                getNext();
                try {
                    if (!cur.isRBRACK())
                        throw new MyError(getLast().getLine(), MyError.ErrorType.K);
                } catch (MyError e) {
                    e.gather();
                    tokenInsertRBRACK();
                }
            }
            fp = new FuncFParam(bType, ident, constExps);
        }
        else {
            fp = new FuncFParam(bType, ident);
        }

        try {
            if (curST.check(ident.getWord())) {
                throw new MyError(ident.getLine(), MyError.ErrorType.B);
            }
            else {
                curST.insert(new Symbol(fp));
            }
        } catch (MyError e) {
            e.gather();
        }
        printData("FuncFParam");
        return fp;
    }

    private Block parseBlock() {
        assert cur.isLBRACE();
        getNext();
        ArrayList<BlockItem> blockItems = new ArrayList<>();
        while (!cur.isRBRACE()) {
            blockItems.add(parseBlockItem());
            getNext();
        }
        try {
            if (curST.isIntFunc() &&
                    (blockItems.size() <= 0 || !(blockItems.get(blockItems.size() - 1).isReturn())))
            {
                throw new MyError(cur.getLine(), MyError.ErrorType.G);
            }
        } catch (MyError e) {
            e.gather();
        }
//        if (!curST.isIntFunc() &&
//                (blockItems.size() <= 0 || !(blockItems.get(blockItems.size() - 1).isReturn()))) {
//            blockItems.add(new BlockItem(new StmtReturn()));
//        }
        assert cur.isRBRACE();

        Block block = new Block(blockItems);
        printData("Block");
        return block;
    }

    private BlockItem parseBlockItem() {
        if (isDecl()) {
            return new BlockItem(parseDecl());
        }
        else {
            return new BlockItem(parseStmt());
        }
    }

    private boolean LineContainsAssign() {
        int line = cur.getLine();
        for (int i = index; i < tokens.size(); i++) {
            Word p = tokens.get(i);
            if (p.isASSIGN() && p.getLine() == line) {
                return true;
            }
            else if (p.isSEMICN() || p.getLine() > line) {
                return false;
            }
        }
        return false;
    }


    private Stmt parseStmt() {
        Stmt stmt;
        if (cur.isIFTK() || cur.isFORTK() || cur.isLBRACE()) {
            if (cur.isIFTK()) {
                getNext();

                getNext();
                Cond cond = parseCond();

                getNext();

                try {
                    if (!cur.isRPARENT()) {
                        throw new MyError(getLast().getLine(), MyError.ErrorType.J);
                    }
                } catch (MyError e) {
                    e.gather();
                    tokenInsertRPARENT();
                }
                getNext();
                Stmt stmt1 = parseStmt();
                if (preRead() != null && preRead().isELSETK()) {
                    getNext();
                    getNext();
                    Stmt stmt2 = parseStmt();
                    stmt = new StmtIf(cond, stmt1, stmt2);
                }
                else {
                    stmt = new StmtIf(cond, stmt1);
                }
            }
            else if (cur.isFORTK()) {
                parseForing++;
                getNext();

                getNext();
                ForStmt format = null;
                if (!cur.isSEMICN()) {
                    format = parseForStmt();
                    getNext();
                }
                getNext();

                Cond cond = null;
                if (!cur.isSEMICN()) {
                    cond = parseCond();
                    getNext();
                }
                getNext();

                ForStmt format2 = null;
                if (!cur.isRPARENT()) {
                    format2 = parseForStmt();
                    getNext();
                }
                getNext();

                Stmt stmt1 = parseStmt();


                stmt = new StmtFor(format, cond, format2, stmt1);
                parseForing--;
            }
            else {
                curST = new SymbolTable(curST, SymbolTable.STtype.Stmt, "stmt" + stmtcnt);
                stmtcnt++;
                Block block = parseBlock();
                curST = curST.getPreS();
                stmt = new StmtBlock(block);
            }
        }
        else {
            if (cur.isRETURNTK()) {
                getNext();
                if (!cur.isSEMICN()) {
                    try {
                        if (curST.findFuncType() == Symbol.SymbolType.VOID) {
                            throw new MyError(cur.getLine(), MyError.ErrorType.F);
                        }
                    } catch (MyError e) {
                        e.gather();
                    }
                    Exp exp = parseExp();
                    getNext();
                    stmt = new StmtReturn(exp);
                }
                else {
                    stmt = new StmtReturn();
                }
            }
            else if (cur.isBREAKTK() || cur.isCONTINUETK()) {
                try {
                    if (parseForing == 0)
                        throw new MyError(cur.getLine(), MyError.ErrorType.M);
                } catch (MyError e) {
                    e.gather();
                }
                stmt = new StmtBC(cur);
                getNext();
            }
            else if (cur.isPRINTFTK()) {
                getNext();
                getNext();
                Word format = cur;
                getNext();
                ArrayList<Exp> exps = new ArrayList<>();
                while (cur.isCOMMA()) {
                    getNext();
                    exps.add(parseExp());
                    getNext();
                }
                try {
                    if (!cur.isRPARENT()) {
                        throw new MyError(getLast().getLine(), MyError.ErrorType.J);
                    }
                } catch (MyError e) {
                    e.gather();
                    tokenInsertRPARENT();
                }
                getNext();

                stmt = new StmtPrintf(format, exps);
                try {
                    int cntp = ((StmtPrintf) stmt).getCntp();
                    if (cntp != exps.size()) {
                        throw new MyError(getLast().getLine(), MyError.ErrorType.L);
                    }
                } catch (MyError e) {
                    e.gather();
                }
            }
            else {
                if (cur.isIDENFR() && LineContainsAssign()) {
                    LVal lVal = parseLval();

                    getNext();

                    getNext();
                    if (cur.isGETINTTK()) {
                        getNext();
                        getNext();
                        try {
                            if (!cur.isRPARENT()) {
                                throw new MyError(preRead().getLine(), MyError.ErrorType.J);
                            }
                        } catch (MyError e) {
                            e.gather();
                            tokenInsertRPARENT();
                        }
                        getNext();

                        stmt = new StmtGetint(lVal);
                    }
                    else {
                        Exp exp = parseExp();
                        getNext();

                        stmt = new StmtAssign(lVal, exp);
                    }

                }
                else {
                    if (cur.isSEMICN()) {
                        stmt = new StmtAssign();
                    }
                    else {
                        Exp exp = parseExp();
                        getNext();

                        stmt = new StmtAssign(exp);
                    }
                }
            }
            try {
                if (!cur.isSEMICN()) {
                    throw new MyError(getLast().getLine(), MyError.ErrorType.I);
                }
            } catch (MyError e) {
                e.gather();
                tokenInsertSeMICN();
            }
        }

        printData("Stmt");
        return stmt;
    }

    private ForStmt parseForStmt() {
        LVal lVal = parseLval();

        getNext();

        getNext();
        Exp exp = parseExp();
        printData("ForStmt");
        return new ForStmt(lVal, exp);
    }

    private Exp parseExp() {
        AddExp addExp = parseAddExp();
        printData("Exp");
        return new Exp(addExp);
    }

    private Cond parseCond() {
        LOrExp lOrExp = parseLOrExp();
        printData("Cond");
        return new Cond(lOrExp);
    }

    private LVal parseLval() {
        Word ident = cur;
        ArrayList<Exp> exps = new ArrayList<>();
        while (preRead() != null && preRead().isLBRACK()) {
            getNext();

            getNext();
            exps.add(parseExp());

            getNext();
            try {
                if (!cur.isRBRACK())
                    throw new MyError(getLast().getLine(), MyError.ErrorType.K);
            } catch (MyError e) {
                e.gather();
                tokenInsertRBRACK();
            }
        }
        try {
            if (!curST.find(ident.getWord())) {
                throw new MyError(ident.getLine(), MyError.ErrorType.C);
            }
            if (preRead().isASSIGN() && curST.getSymbol(ident.getWord()).isConst()) {
                throw new MyError(cur.getLine(), MyError.ErrorType.H);
            }
        } catch (MyError e) {
            e.gather();
        }
        printData("LVal");
        return new LVal(ident, exps);
    }


    private PrimaryExp parsePrimaryExp() {
        if (cur.isLPARENT()) {
            getNext();
            Exp exp = parseExp();
            getNext();

            printData("PrimaryExp");
            return new PrimaryExp(exp);
        }
        else if (cur.isIDENFR()) {
            LVal lVal = parseLval();

            printData("PrimaryExp");
            return new PrimaryExp(lVal);
        }
        else {
            Number number = parseNumber();

            printData("PrimaryExp");
            return new PrimaryExp(number);
        }
    }

    private Number parseNumber() {
        Number number = new Number(cur);

        printData("Number");
        return number;
    }

    private UnaryExp parseUnaryExp() {
        if (cur.isIDENFR() && preRead().isLPARENT()) {
            Word ident = cur;
            boolean ec = false;
            try {
                if (!curST.find(ident.getWord()) ||
                        (curST.find(ident.getWord()) && !curST.getSymbol(ident.getWord()).isFunc()))
                {
                    throw new MyError(ident.getLine(), MyError.ErrorType.C);
                }
            } catch (MyError e) {
                e.gather();
                ec = true;
            }
            getNext();
            getNext();
            UnaryExp unaryExp;
            FuncRParams funcRParams;
            if (isExp()) {
                funcRParams = parseFuncRParams();
                getNext();
            }
            else {
                funcRParams = new FuncRParams(new ArrayList<>());
            }
            if (!ec)
                try {
                    ArrayList<Exp> exps = funcRParams.getExps();
                    ArrayList<FuncFParam> fps = curST.getSymbol(ident.getWord()).getFuncFParams();
                    if (exps.size() != fps.size()) {
                        throw new MyError(ident.getLine(), MyError.ErrorType.D);
                    }
                    int size = exps.size();
                    for (int i = 0; i < size; i++) {
                        if (fps.get(i).getDataType() != exps.get(i).getDataType()) {
                            throw new MyError(ident.getLine(), MyError.ErrorType.E);
                        }
                    }
                } catch (MyError e) {
                    e.gather();
                }
            unaryExp = new UnaryExp(ident, funcRParams);
            try {
                if (!cur.isRPARENT()) {
                    tokenInsertRPARENT();
                    throw new MyError(getLast().getLine(), MyError.ErrorType.J);
                }
            } catch (MyError e) {
                e.gather();
            }

            printData("UnaryExp");
            return unaryExp;

        }
        else if (isUnaryOp()) {
            UnaryOp unaryOp = parseUnaryOp();
            getNext();
            UnaryExp unaryExp = parseUnaryExp();

            printData("UnaryExp");
            return new UnaryExp(unaryOp, unaryExp);
        }
        else {
            PrimaryExp primaryExp = parsePrimaryExp();

            printData("UnaryExp");
            return new UnaryExp(primaryExp);
        }
    }

    private UnaryOp parseUnaryOp() {
        UnaryOp unaryOp = new UnaryOp(cur);

        printData("UnaryOp");
        return unaryOp;
    }

    private FuncRParams parseFuncRParams() {
        ArrayList<Exp> exps = new ArrayList<>();
        Exp exp = parseExp();
        exps.add(exp);

        while (preRead() != null && preRead().isCOMMA()) {
            getNext();
            getNext();
            exps.add(parseExp());
        }
//        getNext();
        printData("FuncRParams");
        return new FuncRParams(exps);
    }

    private MulExp parseMulExp() {
        ArrayList<UnaryExp> unaryExps = new ArrayList<>();
        ArrayList<Word> words = new ArrayList<>();
        unaryExps.add(parseUnaryExp());
        while (preRead() != null &&
                (preRead().isMULT() || preRead().isDIV() || preRead().isMOD())) {
            printData("MulExp");
            getNext();
            words.add(cur);
            getNext();
            unaryExps.add(parseUnaryExp());
        }

        printData("MulExp");
        return new MulExp(unaryExps, words);
    }

    private AddExp parseAddExp() {
        ArrayList<MulExp> mulExps = new ArrayList<>();
        ArrayList<Word> words = new ArrayList<>();
        mulExps.add(parseMulExp());
        while (preRead() != null &&
                (preRead().isPLUS() || preRead().isMINU())) {
            printData("AddExp");
            getNext();
            words.add(cur);
            getNext();
            mulExps.add(parseMulExp());
        }

        printData("AddExp");
        return new AddExp(mulExps, words);
    }

    private RelExp parseRelExp() {
        ArrayList<AddExp> addExps = new ArrayList<>();
        ArrayList<Word> words = new ArrayList<>();
        addExps.add(parseAddExp());
        while (preRead() != null &&
                (preRead().isLSS() || preRead().isLEQ() || preRead().isGRE() || preRead().isGEQ())) {
            printData("RelExp");
            getNext();
            words.add(cur);
            getNext();
            addExps.add(parseAddExp());
        }

        printData("RelExp");
        return new RelExp(addExps, words);
    }

    private EqExp parseEqExp() {
        ArrayList<RelExp> relExps = new ArrayList<>();
        ArrayList<Word> words = new ArrayList<>();
        relExps.add(parseRelExp());
        while (preRead() != null &&
                (preRead().isEQL() || preRead().isNEQ())) {
            printData("EqExp");
            getNext();
            words.add(cur);
            getNext();
            relExps.add(parseRelExp());
        }

        printData("EqExp");
        return new EqExp(relExps, words);
    }

    private LAndExp parseLAndExp() {
        ArrayList<EqExp> eqExps = new ArrayList<>();
        ArrayList<Word> words = new ArrayList<>();
        eqExps.add(parseEqExp());
        while (preRead() != null &&
                (preRead().isAND())) {
            printData("LAndExp");
            getNext();
            words.add(cur);
            getNext();
            eqExps.add(parseEqExp());
        }

        printData("LAndExp");
        return new LAndExp(eqExps, words);
    }

    private LOrExp parseLOrExp() {
        ArrayList<LAndExp> lAndExps = new ArrayList<>();
        ArrayList<Word> words = new ArrayList<>();
        lAndExps.add(parseLAndExp());
        while (preRead() != null &&
                (preRead().isOR())) {
            printData("LOrExp");
            getNext();
            words.add(cur);
            getNext();
            lAndExps.add(parseLAndExp());
        }

        printData("LOrExp");
        return new LOrExp(lAndExps, words);
    }

    private ConstExp parseConstExp() {
        ConstExp constExp = new ConstExp(parseAddExp());

        printData("ConstExp");
        return constExp;
    }


    private boolean isExp() {
        if (cur.isPLUS() || cur.isMINU() || cur.isNOT()
                || cur.isIDENFR()
                || cur.isLPARENT()
                || cur.isINTCON())
        {
            return true;
        }
        return false;
    }

    private boolean isFuncDef() {
        if ((cur.isINTTK() || cur.isVOIDTK()) &&
                (preRead().isIDENFR() && tokens.get(index + 2).isLPARENT()))
        {
            return true;
        }
        return false;
    }

    private boolean isUnaryOp() {
        if (cur.isPLUS() || cur.isMINU() || cur.isNOT()) {
            return true;
        }
        return false;
    }

    private boolean isDecl() {
        if (isConstDecl() || isVarDecl()) {
            return true;
        }
        return false;
    }

    private boolean isConstDecl() {
        if (cur.isCONSTTK()) {
            return true;
        }
        return false;
    }

    private boolean isVarDecl() {
        if (cur.isINTTK() && preRead().isIDENFR()
                && (tokens.get(index + 2).isLBRACK()
                || tokens.get(index + 2).isASSIGN()
                || tokens.get(index + 2).isSEMICN()
                || tokens.get(index + 2).isCOMMA()))
        {
            return true;
        }
        return false;
    }

    private void printWord() {
        if (printword) {
            System.out.println(cur);
        }
    }

    private void printData(String name) {
        if (printdata) {
            System.out.println("<" + name + ">");
        }
    }
}
