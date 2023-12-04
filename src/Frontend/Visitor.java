package Frontend;

import Frontend.Lexical.Word;
import Frontend.Parscial.ParseNodes.*;
import Frontend.Parscial.ParseNodes.Stmts.*;
import Frontend.Parscial.Parser;
import Frontend.Parscial.Symbol;
import Frontend.Parscial.SymbolTable;
import midend.ir.Module;
import midend.ir.IRPort;
import midend.ir.types.*;
import midend.ir.types.FuncType;
import midend.ir.values.*;
import midend.ir.values.constants.*;
import midend.ir.values.constants.ValueArray;
import midend.ir.values.constants.ConstantInt;
import midend.ir.values.constants.GlobalValue;

import midend.ir.values.instructions.Instruction;
import midend.ir.values.instructions.binary.Icmp;
import midend.ir.values.instructions.memory.Gep;
import midend.ir.values.instructions.terminator.Br;

import java.util.ArrayList;

public class Visitor {
    private CompUnit compUnit;

    private Module module;

    private SymbolTable curST;

    private Function curFunc = null;
    private BasicBlock curBB = null;
    private BasicBlock curCond = null;
    private BasicBlock curFollow = null;
    private BasicBlock curStmt2 = null;

    private int cntStmt = 0;


    public Visitor(CompUnit compUnit) {
        this.compUnit = compUnit;
        this.module = new Module();
        this.curST = Parser.curST.getRoot();
        addLibs();
    }

    public Module visit() {
        visitCompUnit();
        return module;
    }

    private void visitCompUnit() {
        for (Decl decl : compUnit.getDecls()) {
            visitDecl(decl);
        }
        for (FuncDef funcDef : compUnit.getFuncDefs()) {
            visitFuncDef(funcDef);
        }
        visitMainFunc(compUnit.getMainFuncDef());
    }

    private void visitDecl(Decl decl) {
        if (decl.isConstDecl()) {
            visitConstDecl(decl.getConstDecl());
        }
        else {
            visitVarDecl(decl.getVarDecl());
        }
    }

    private void visitConstDecl(ConstDecl constDecl) {
        for (ConstDef constDef : constDecl.getConstDefs()) {
            visitConstDef(constDef);
        }
    }


    private Value visitConstInitVal(ConstInitVal constInitVal) {
        if (constInitVal.getConstExp() != null) {
            return visitConstExp(constInitVal.getConstExp());
        }
        else {
            ArrayList<Value> array = new ArrayList<>();
            for (ConstInitVal ci : constInitVal.getConstInitVals()) {
                array.add((Constant) visitConstInitVal(ci));
            }
            return new ValueArray(array);
        }
    }

    public Value visitConstExp(ConstExp constExp) {
        return new ConstantInt(32, constExp.getVal());
    }

    private void visitVarDecl(VarDecl varDecl) {
        for (VarDef varDef : varDecl.getVarDefs()) {
            visitVarDef(varDef);
        }
    }

    private void visitConstDef(ConstDef constDef) {
        String ident = constDef.getIdent().getWord();
        Symbol sym = curST.getSymbol(ident);
        Type type = sym.genType();

        Value constInitVal = visitConstInitVal(constDef.getConstInitVal());

        if (curST.isRoot()) {
            GlobalValue gv = new GlobalValue(ident, type, true, constInitVal);
            sym.setVal(gv);
            module.addGlobalVariable(gv);
        }
        else {
            Value alloca = IRPort.newAlloca(type, curBB);
            if (constInitVal instanceof ValueArray) {
                ArrayInit(alloca, (ArrayType) type, constInitVal);
            }
            else {
                IRPort.newStore(curBB, constInitVal, alloca);
            }
            sym.setVal(alloca);
        }
    }

    private void visitVarDef(VarDef varDef) {
        String ident = varDef.getIdent().getWord();
        Symbol sym = curST.getSymbol(ident);
        Type type = sym.genType();

        Value initVal = visitInitVal(varDef.getInitVal());

        if (curST.isRoot()) {
            GlobalValue gv = new GlobalValue(ident, type, false, initVal);
            sym.setVal(gv);
            module.addGlobalVariable(gv);
        }
        else {
            Value alloca = IRPort.newAlloca(type, curBB);
            if (initVal != null) {
                if (initVal instanceof ValueArray) {
                    ArrayInit(alloca, (ArrayType) type, initVal);
                }
                else {
                    IRPort.newStore(curBB, initVal, alloca);
                }
            }
            else {
                if (type instanceof IntType) {
                    IRPort.newStore(curBB, new ConstantInt(32, 0), alloca);
                }
            }
            sym.setVal(alloca);
        }
    }

    private Value visitInitVal(InitVal initVal) {
        if (initVal == null) {
            return null;
        }
        if (initVal.getExp() != null) {
            if (curST.isRoot()) {
                return new ConstantInt(32, initVal.getExp().getVal());
            }
            else {
                return visitExp(initVal.getExp());
            }
        }
        else {
            ArrayList<Value> array = new ArrayList<>();
            for (InitVal iv : initVal.getInitVals()) {
                array.add(visitInitVal(iv));
            }
            return new ValueArray(array);
        }
    }


    private void ArrayInit(Value base, ArrayType at, Value curConstInitVal) {
        int size = at.getElemNum();
        if (at.getElemType() instanceof ArrayType) {
            ValueArray ca = (ValueArray) curConstInitVal;
            for (int i = 0; i < size; i++) {
                curConstInitVal = ca.get(i);
                Gep gep = IRPort.newGep(curBB, at, base, new ConstantInt(32, 0), new ConstantInt(32, i));
                ArrayInit(gep, (ArrayType) at.getElemType(), curConstInitVal);
            }
        }
        else {
            for (int i = 0; i < size; i++) {
                Gep gep = IRPort.newGep(curBB, at, base, new ConstantInt(32, 0), new ConstantInt(32, i));
                IRPort.newStore(curBB, ((ValueArray) curConstInitVal).get(i), gep);
            }
        }
    }

    private void visitFuncDef(FuncDef funcDef) {
        String fname = funcDef.getIdent().getWord();
        ArrayList<FuncFParam> funcFParams = funcDef.getFuncFParams().getFuncFParams();
        ArrayList<Type> types = new ArrayList<>();
        for (FuncFParam fp : funcFParams) {
            if (fp.getDataType() == Symbol.SymbolType.INT) {
                types.add(new IntType(32));
            }
            else if (fp.getDataType() == Symbol.SymbolType.ARRAY1) {
                types.add(new PointerType(new IntType(32)));
            }
            else {
                types.add(new PointerType(new ArrayType(fp.getConstExps().get(1).getVal(), new IntType(32))));
            }
        }
        Type retType = funcDef.isVoid() ? new VoidType() : new IntType(32);

        FuncType ft = new FuncType(types, retType, true);

        Function function = IRPort.newFunc(ft, fname);
        module.addFunction(function);
        curST = curST.nxt(fname);
        curFunc = function;
        for (Type type : types) {
            IRPort.newArg(curFunc, type);
        }
        curBB = IRPort.newBB(curFunc);
        for (int i = 0; i < function.getArgs().size(); i++) {
            Argument arg = function.getArgs().get(i);
            Value alloca = IRPort.newAlloca(arg.getType(), curBB);
            IRPort.newStore(curBB, arg, alloca);
            Symbol sym = curST.getSymbol(funcFParams.get(i).getIdent().getWord());
            sym.setVal(alloca);
        }
        visitBlock(funcDef.getBlock());
        curFunc.retCheck();

        for (BasicBlock bb : curFunc.getBbs()) {
            if (bb.getLast() instanceof Br) {
                Br br = (Br) bb.getLast();
                if (br.isCondition()) {
                    Instruction nxt1 = br.getTrueBB().getFirst();
                    nxt1.preInstr.add(br);
                    br.nxtInstr.add(nxt1);

                    Instruction nxt2 = br.getFalseBB().getFirst();
                    nxt2.preInstr.add(br);
                    br.nxtInstr.add(nxt2);
                }
                else {
                    Instruction nxt = br.getToBB().getFirst();
                    nxt.preInstr.add(br);
                    br.nxtInstr.add(nxt);
                }
            }
        }
        curST = curST.getPreS();
        curFunc = null;
        curBB = null;
    }

    private void visitMainFunc(MainFuncDef mainFuncDef) {
        curFunc = IRPort.newFunc(new FuncType(new ArrayList<>(), new IntType(32), true), "main");
        module.addFunction(curFunc);
        curST = curST.nxt("main");
        curBB = IRPort.newBB(curFunc);
        visitBlock(mainFuncDef.getBlock());
        curFunc.retCheck();
        curST = curST.getPreS();
    }

    private void visitBlock(Block block) {
        for (BlockItem bi : block.getBlockItems()) {
            visitBlockItem(bi);
        }
    }

    private void visitBlockItem(BlockItem blockItem) {
        if (blockItem.getDecl() != null) {
            visitDecl(blockItem.getDecl());
        }
        else {
            visitStmt(blockItem.getStmt());
        }
    }

    private void visitStmt(Stmt stmt) {
        if (stmt instanceof StmtReturn) {
            visitStmtReturn((StmtReturn) stmt);
        }
        else if (stmt instanceof StmtBlock) {
            visitStmtBlock((StmtBlock) stmt);
        }
        else if (stmt instanceof StmtAssign) {
            visitStmtAssign((StmtAssign) stmt);
        }
        else if (stmt instanceof StmtGetint) {
            visitStmtGetint((StmtGetint) stmt);
        }
        else if (stmt instanceof StmtPrintf) {
            visitStmtPrintf((StmtPrintf) stmt);
        }
        else if (stmt instanceof StmtIf) {
            visitStmtIf((StmtIf) stmt);
        }
        else if (stmt instanceof StmtFor) {
            visitStmtFor((StmtFor) stmt);
        }
        else if (stmt instanceof StmtBC) {
            visitStmtBreakOrContinue((StmtBC) stmt);
        }
    }

    private void visitStmtReturn(StmtReturn ret) {
        if (ret.getExp() != null) {
            IRPort.newUnVoidRet(curBB, visitExp(ret.getExp()));
        }
        else {
            IRPort.newVoidRet(curBB);
        }
    }

    private void visitStmtBlock(StmtBlock stmt) {
        curST = curST.nxt("stmt" + cntStmt);
        cntStmt++;
        visitBlock((stmt).getBlock());
        curST = curST.getPreS();
    }

    private void visitStmtAssign(StmtAssign stmt) {
        if ((stmt).getlVal() != null) {
            //Lval = exp
            Value exp = visitExp((stmt).getExp());
            StoreLVal((stmt).getlVal(), exp);
        }
        else if ((stmt).getExp() != null) {
            //exp
            visitExp((stmt).getExp());
        }
    }

    private void visitStmtPrintf(StmtPrintf stmt) {
        String str = (stmt).getFormatString();
        int cntp = 0;
        ArrayList<Value> args;
        ArrayList<Exp> exps = (stmt).getExps();
        String gstr = new String();
        for (int i = 0; i < str.length(); i++) {
            args = new ArrayList<>();
            Character c = str.charAt(i);
            if (c == '%') {
                Value v = visitExp(exps.get(cntp));
                cntp++;
                i++;
                args.add(v);
                CallFunc("putint", args);
            }
            else if (c == '\\') {
                i++;
                args.add(new ConstantInt(32, 10));
                CallFunc("putch", args);
            }
            else {
                args.add(new ConstantInt(32, ((int) c)));
                CallFunc("putch", args);
            }
        }

    }

    private void visitStmtGetint(StmtGetint stmt) {
        Value call = CallFunc("getint", new ArrayList<>());
        StoreLVal((stmt).getlVal(), call);
    }

    private void visitStmtIf(StmtIf stmt) {
        Stmt ifStmt = stmt.getIfStmt();
        Stmt elseStmt = stmt.getElseStmt();
        boolean hasElse = elseStmt != null;
        BasicBlock ifBB = IRPort.newBB(curFunc);
        BasicBlock elseBB = null;
        if (hasElse) {
            elseBB = IRPort.newBB(curFunc);
        }
        BasicBlock followBB = IRPort.newBB(curFunc);

        if (hasElse) {
            visitLOrExp(stmt.getCond().getlOrExp(), ifBB, elseBB);
            curBB = ifBB;
            visitStmt(ifStmt);
            IRPort.newBrWithNoCond(curBB, followBB);
            curBB = elseBB;
            visitStmt(elseStmt);
        }
        else {
            visitLOrExp(stmt.getCond().getlOrExp(), ifBB, followBB);
            curBB = ifBB;
            visitStmt(ifStmt);
        }

        IRPort.newBrWithNoCond(curBB, followBB);
        curBB = followBB;
    }

    private void visitStmtFor(StmtFor stmt) {
        BasicBlock condBB = curCond;
        BasicBlock followBB = curFollow;
        BasicBlock stmt2BB = curStmt2;

        if (stmt.getForStmt1() != null) {
            visitForStmt(stmt.getForStmt1());
        }

        BasicBlock stmtBB = IRPort.newBB(curFunc);

        //如果Cond缺省，直接跳到stmtBB
        if (stmt.getCond() != null) {
            curCond = IRPort.newBB(curFunc);
        }
        else {
            curCond = stmtBB;
        }
        IRPort.newBrWithNoCond(curBB, curCond);

        curFollow = IRPort.newBB(curFunc);
        curBB = curCond;
        if (stmt.getCond() != null) {
            visitLOrExp(stmt.getCond().getlOrExp(), stmtBB, curFollow);
        }


        if (stmt.getForStmt2() != null) {
            curStmt2 = IRPort.newBB(curFunc);
        }
        else {
            curStmt2 = curCond;
        }

        curBB = stmtBB;
        visitStmt(stmt.getStmt());
        IRPort.newBrWithNoCond(curBB, curStmt2);

        curBB = curStmt2;
        if (stmt.getForStmt2() != null) {
            visitForStmt(stmt.getForStmt2());
        }
        IRPort.newBrWithNoCond(curBB, curCond);
        curBB = curFollow;
        curCond = condBB;
        curFollow = followBB;
        curStmt2 = stmt2BB;
    }

    private void visitStmtBreakOrContinue(StmtBC stmt) {
        if (stmt.isBreak()) {
            IRPort.newBrWithNoCond(curBB, curFollow);
        }
        else {
            IRPort.newBrWithNoCond(curBB, curStmt2);
        }
    }

    private void visitForStmt(ForStmt stmt) {
        Value exp = visitExp(stmt.getExp());
        Value lVal = StoreLVal(stmt.getlVal(), exp);
    }

    public Value CallFunc(String funcName, ArrayList<Value> args) {
        Function func = module.getFunc(funcName);
        FuncType ft = (FuncType) func.getType();
        if (ft.isRet()) {
            return IRPort.newUnVoidCall(curBB, func, args);
        }
        else {
            return IRPort.newVoidCall(curBB, func, args);
        }
    }

    private Value visitExp(Exp exp) {
        return visitAddExp(exp.getAddExp());
    }


    private void visitLOrExp(LOrExp lOrExp, BasicBlock trueBB, BasicBlock falseBB) {
        int nums = lOrExp.getlAndExps().size();
        ArrayList<LAndExp> lands = lOrExp.getlAndExps();

        BasicBlock nxtBB = curBB;//下一个判断Or条件所在的BB
        for (int i = 0; i < nums; i++) {
            curBB = nxtBB;//进入新的判断条件
            if (i == nums - 1) {
                nxtBB = falseBB;//最后一个判断条件 不符合进入False
            }
            else {
                nxtBB = IRPort.newBB(curFunc);
            }
            visitLAndExp(lands.get(i), trueBB, nxtBB);
            curBB = nxtBB;
        }
    }

    //curBB = final cond bb
    private void visitLAndExp(LAndExp lAndExp, BasicBlock trueBB, BasicBlock falseBB) {
        int nums = lAndExp.getEqExps().size();
        ArrayList<EqExp> eqs = lAndExp.getEqExps();
        BasicBlock nxtBB = curBB;//下一个判断AND条件所在的BB
        for (int i = 0; i < nums; i++) {
            curBB = nxtBB;//进入新的判断条件
            if (i == nums - 1) {
                nxtBB = trueBB;//最后一个判断条件 符合进入True
            }
            else {
                nxtBB = IRPort.newBB(curFunc);
            }
            Value m = visitEqExp(eqs.get(i));
            IRPort.newBrWithCond(curBB, m, nxtBB, falseBB);
        }
    }

    private Value visitEqExp(EqExp eqExp) {
        int nums = eqExp.getRelExps().size();
        ArrayList<RelExp> rels = eqExp.getRelExps();
        ArrayList<Word> ops = eqExp.getWords();
        Value m0 = visitRelExp(rels.get(0));
        for (int i = 1; i < nums; i++) {
            Value m = visitRelExp(rels.get(i));
            Word op = ops.get(i - 1);
            if ((m0 instanceof ConstantInt) && (m instanceof ConstantInt)) {
                int a = ((ConstantInt) m0).getVal();
                int b = ((ConstantInt) m).getVal();
                if (op.isEQL()) {
                    if (a == b) {
                        m0 = new ConstantInt(1, 1);
                    }
                    else {
                        m0 = new ConstantInt(1, 0);
                    }
                }
                else {
                    if (a < b) {
                        m0 = new ConstantInt(1, 1);
                    }
                    else {
                        m0 = new ConstantInt(1, 0);
                    }
                }
            }
            else {
                if (((IntType) m0.getType()).getBits() == 1) {
                    if (m0 instanceof ConstantInt) {
                        m0 = new ConstantInt(32, ((ConstantInt) m0).getVal());
                    }
                    else {
                        m0 = IRPort.newZextTo(curBB, m0, new IntType(32));
                    }
                }
                if (((IntType) m.getType()).getBits() == 1) {
                    if (m instanceof ConstantInt) {
                        m = new ConstantInt(32, ((ConstantInt) m).getVal());
                    }
                    else {
                        m = IRPort.newZextTo(curBB, m, new IntType(32));
                    }
                }
                if (op.isEQL()) {
                    m0 = IRPort.newIcmp(curBB, m0, m, Icmp.IcmpType.EQ);
                }
                else {
                    m0 = IRPort.newIcmp(curBB, m0, m, Icmp.IcmpType.NE);
                }
            }
        }
        if (((IntType) m0.getType()).getBits() == 32) {
            if (m0 instanceof ConstantInt) {
                if (((ConstantInt) m0).getVal() != 0) {
                    return new ConstantInt(1, 1);
                }
                else {
                    return new ConstantInt(1, 0);
                }
            }
            else {
                m0 = IRPort.newIcmp(curBB, m0, new ConstantInt(32, 0), Icmp.IcmpType.NE);
            }
        }
        return m0;
    }

    private Value visitRelExp(RelExp relExp) {
        int nums = relExp.getAddExps().size();
        ArrayList<AddExp> adds = relExp.getAddExps();
        ArrayList<Word> ops = relExp.getWords();
        Value m0 = visitAddExp(adds.get(0));
        for (int i = 1; i < nums; i++) {
            Value m = visitAddExp(adds.get(i));
            Word op = ops.get(i - 1);
            if ((m0 instanceof ConstantInt) && (m instanceof ConstantInt)) {
                int a = ((ConstantInt) m0).getVal();
                int b = ((ConstantInt) m).getVal();
                if (op.isGEQ()) {
                    if (a >= b) {
                        m0 = new ConstantInt(1, 1);
                    }
                    else {
                        m0 = new ConstantInt(1, 0);
                    }
                }
                else if (op.isGRE()) {
                    if (a > b) {
                        m0 = new ConstantInt(1, 1);
                    }
                    else {
                        m0 = new ConstantInt(1, 0);
                    }
                }
                else if (op.isLEQ()) {
                    if (a <= b) {
                        m0 = new ConstantInt(1, 1);
                    }
                    else {
                        m0 = new ConstantInt(1, 0);
                    }
                }
                else {
                    if (a < b) {
                        m0 = new ConstantInt(1, 1);
                    }
                    else {
                        m0 = new ConstantInt(1, 0);
                    }
                }
            }
            else {
                if (((IntType) m0.getType()).getBits() == 1) {
                    if (m0 instanceof ConstantInt) {
                        m0 = new ConstantInt(32, ((ConstantInt) m0).getVal());
                    }
                    else {
                        m0 = IRPort.newZextTo(curBB, m0, new IntType(32));
                    }
                }
                if (((IntType) m.getType()).getBits() == 1) {
                    if (m instanceof ConstantInt) {
                        m = new ConstantInt(32, ((ConstantInt) m).getVal());
                    }
                    else {
                        m = IRPort.newZextTo(curBB, m, new IntType(32));
                    }
                }
                if (op.isGEQ()) {
                    m0 = IRPort.newIcmp(curBB, m0, m, Icmp.IcmpType.SGE);
                }
                else if (op.isGRE()) {
                    m0 = IRPort.newIcmp(curBB, m0, m, Icmp.IcmpType.SGT);
                }
                else if (op.isLEQ()) {
                    m0 = IRPort.newIcmp(curBB, m0, m, Icmp.IcmpType.SLE);
                }
                else {
                    m0 = IRPort.newIcmp(curBB, m0, m, Icmp.IcmpType.SLT);
                }
            }

        }
        return m0;

    }

    private Value visitAddExp(AddExp addExp) {
        int nums = addExp.getMulExps().size();
        ArrayList<Word> syms = addExp.getWords();
        ArrayList<MulExp> muls = addExp.getMulExps();
        Value m0 = visitMulExp(muls.get(0));
        for (int i = 1; i < nums; i++) {
            Value m = visitMulExp(muls.get(i));
            if (syms.get(i - 1).isPLUS()) {
                m0 = IRPort.newAdd(curBB, m0, m);
            }
            else {
                m0 = IRPort.newSub(curBB, m0, m);
            }
        }
        return m0;
    }

    private Value visitMulExp(MulExp mulExp) {
        int nums = mulExp.getUnaryExps().size();
        ArrayList<Word> syms = mulExp.getWords();
        ArrayList<UnaryExp> unarys = mulExp.getUnaryExps();
        Value u0 = visitUnaryExp(unarys.get(0));
        for (int i = 1; i < nums; i++) {
            Value u = visitUnaryExp(unarys.get(i));
            if (syms.get(i - 1).isMULT()) {
                u0 = IRPort.newMul(curBB, u0, u);
            }
            else if (syms.get(i - 1).isDIV()) {
                u0 = IRPort.newSdiv(curBB, u0, u);
            }
            else {
                u0 = IRPort.newSrem(curBB, u0, u);
            }
        }
        return u0;
    }

    private Value visitUnaryExp(UnaryExp unaryExp) {
        if (unaryExp.getIdent() != null) {
            String fname = unaryExp.getIdent().getWord();
            ArrayList<Value> values = new ArrayList<>();
            for (Exp e : unaryExp.getFuncRParams()) {
                Value v = visitExp(e);
                values.add(v);
            }
            return CallFunc(fname, values);
        }
        else if (unaryExp.getPrimaryExp() != null) {
            return visitPrimaryExp(unaryExp.getPrimaryExp());
        }
        else {
            Word sym = unaryExp.getUnaryOp().getWord();
            if (sym.isPLUS()) {
                return visitUnaryExp(unaryExp.getUnaryExp());
            }
            else if (sym.isNOT()) {
                Value unary = visitUnaryExp(unaryExp.getUnaryExp());
                Value icmp = IRPort.newIcmp(curBB, new ConstantInt(32, 0), unary, Icmp.IcmpType.EQ);
                return IRPort.newZextTo(curBB, icmp, new IntType(32));
            }
            else {
                Value unary = visitUnaryExp(unaryExp.getUnaryExp());
                return IRPort.newSub(curBB, new ConstantInt(32, 0), unary);
            }
        }
    }

    private Value visitPrimaryExp(PrimaryExp primaryExp) {
        if (primaryExp.getExp() != null) {
            return visitExp(primaryExp.getExp());
        }
        else if (primaryExp.getlVal() != null) {
            return LoadLVal(primaryExp.getlVal());
        }
        else {
            return new ConstantInt(32, primaryExp.getVal());
        }
    }


    private Value LoadLVal(LVal lVal) {
        //得到指针
        Value p = curST.getPointer(lVal.getIdent().getWord());
        //得到要加载的值的类型
        Type t = ((PointerType) p.getType()).getPointtoType();
        if (t instanceof PointerType) {
            if (lVal.getExps().size() == 1) {
                p = IRPort.newLoad(t, curBB, p);
                Value index1 = visitExp(lVal.getExps().get(0));
                p = IRPort.newGep(curBB, (PointerType) t, p, index1);
                t = ((PointerType) t).getPointtoType();
            }
            else if (lVal.getExps().size() == 2) {
                p = IRPort.newLoad(t, curBB, p);
                Value index1 = visitExp(lVal.getExps().get(0));
                p = IRPort.newGep(curBB, (PointerType) t, p, index1);
                t = ((PointerType) t).getPointtoType();
                Value index2 = visitExp(lVal.getExps().get(1));
                p = IRPort.newGep(curBB, (ArrayType) t, p, new ConstantInt(32, 0), index2);
                t = ((ArrayType) t).getElemType();
            }
        }
        else {
            if (lVal.getExps().size() == 1) {
                Value index1 = visitExp(lVal.getExps().get(0));
                p = IRPort.newGep(curBB, (ArrayType) t, p, new ConstantInt(32, 0), index1);
                t = ((ArrayType) t).getElemType();
            }
            else if (lVal.getExps().size() == 2) {
                Value index1 = visitExp(lVal.getExps().get(0));
                p = IRPort.newGep(curBB, (ArrayType) t, p, new ConstantInt(32, 0), index1);
                t = ((ArrayType) t).getElemType();
                Value index2 = visitExp(lVal.getExps().get(1));
                p = IRPort.newGep(curBB, (ArrayType) t, p, new ConstantInt(32, 0), index2);
                t = ((ArrayType) t).getElemType();
            }
        }
        if (t instanceof ArrayType) {
            return IRPort.newGep(curBB, (PointerType) p.getType(), p, new ConstantInt(32, 0), new ConstantInt(32, 0));
        }
        return IRPort.newLoad(t, curBB, p);
    }

    private Value StoreLVal(LVal lVal, Value v) {
        Value p = curST.getPointer(lVal.getIdent().getWord());
        Type t = ((PointerType) p.getType()).getPointtoType();
        if (t instanceof PointerType) {
            if (lVal.getExps().size() == 1) {
                p = IRPort.newLoad(t, curBB, p);
                Value index1 = visitExp(lVal.getExps().get(0));
                p = IRPort.newGep(curBB, (PointerType) t, p, index1);
                t = ((PointerType) t).getPointtoType();
            }
            else if (lVal.getExps().size() == 2) {
                p = IRPort.newLoad(t, curBB, p);
                Value index1 = visitExp(lVal.getExps().get(0));
                p = IRPort.newGep(curBB, (PointerType) t, p, index1);
                t = ((PointerType) t).getPointtoType();
                Value index2 = visitExp(lVal.getExps().get(1));
                p = IRPort.newGep(curBB, (ArrayType) t, p, new ConstantInt(32, 0), index2);
                t = ((ArrayType) t).getElemType();
            }
        }
        else {
            if (lVal.getExps().size() == 1) {
                Value index1 = visitExp(lVal.getExps().get(0));
                p = IRPort.newGep(curBB, (ArrayType) t, p, new ConstantInt(32, 0), index1);
                t = ((ArrayType) t).getElemType();
            }
            else if (lVal.getExps().size() == 2) {
                Value index1 = visitExp(lVal.getExps().get(0));
                p = IRPort.newGep(curBB, (ArrayType) t, p, new ConstantInt(32, 0), index1);
                t = ((ArrayType) t).getElemType();
                Value index2 = visitExp(lVal.getExps().get(1));
                p = IRPort.newGep(curBB, (ArrayType) t, p, new ConstantInt(32, 0), index2);
                t = ((ArrayType) t).getElemType();
            }
        }
        return IRPort.newStore(curBB, v, p);
    }

    public void addLibs() {
        ArrayList<Type> args = new ArrayList<Type>();
        module.addDeclFunc(IRPort.newFunc(new FuncType(args, new IntType(32), false), "getint"));

        args = new ArrayList<Type>();
        args.add(new IntType(32));
        module.addDeclFunc(IRPort.newFunc(new FuncType(args, new VoidType(), false), "putint"));

        args = new ArrayList<Type>();
        args.add(new IntType(32));
        module.addDeclFunc(IRPort.newFunc(new FuncType(args, new VoidType(), false), "putch"));

        args = new ArrayList<Type>();
        args.add(new PointerType(new IntType(8)));
        module.addDeclFunc(IRPort.newFunc(new FuncType(args, new VoidType(), false), "putstr"));
    }
}
