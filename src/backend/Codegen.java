package backend;

import backend.Component.MipsBlock;
import backend.Component.MipsFunction;
import backend.Component.MipsGlobalValue;
import backend.Component.Program;
import backend.Instructions.*;
import backend.Operand.*;
import midend.ir.Module;
import midend.ir.types.ArrayType;
import midend.ir.types.PointerType;
import midend.ir.values.*;
import midend.ir.values.constants.ConstantInt;
import midend.ir.values.constants.GlobalValue;
import midend.ir.values.instructions.Instruction;
import midend.ir.values.instructions.binary.Binary;
import midend.ir.values.instructions.binary.Icmp;
import midend.ir.values.instructions.memory.*;
import midend.ir.values.instructions.terminator.*;

import java.util.ArrayList;
import java.util.HashMap;

import static midend.pass.Pass.pass;


public class Codegen {
    private Module module;
    private Program program;

    private MipsFunction curMF;
    private MipsBlock curMB;
    public static HashMap<Function, MipsFunction> f2mf = new HashMap<>();
    public static HashMap<BasicBlock, MipsBlock> b2mb = new HashMap<>();

    public ArrayList<BasicBlock> genArray;


    public Codegen(Module module) {
        this.module = module;
        this.program = new Program();
    }

    public Program genCode() {
        for (GlobalValue gv : module.getGlobalValues()) {
            genGlobal(gv);
        }
        for (Function f : module.getFunctions()) {
            genFunc(f);
        }
        return program;
    }

    public void genGlobal(GlobalValue gv) {
        program.addGlobalValue(new MipsGlobalValue(gv));
    }

    public void genFunc(Function function) {
        curMF = new MipsFunction(function);
        f2mf.put(function, curMF);
        if (function.getName().equals("main")) {
            program.setMainFunc(curMF);
        }
        program.addFunction(curMF);

        genArray = function.genBFSArray();
        for (BasicBlock bb : function.genBFSArray()) {
            curMB = new MipsBlock(bb);
            inherit();
            b2mb.put(bb, curMB);
            genBlock(bb);
            curMF.addBB(curMB);
        }
        if (pass) {
            RemovePhi();
        }
    }

    public void RemovePhi() {
        for (BasicBlock bb : curMF.getFunction().getBbs()) {
            MipsBlock mb = b2mb.get(bb);
            for (Instruction instr : bb.getInsts()) {
                if (!(instr.isPhi())) {
                    break;
                }
                for (BasicBlock pre : bb.getPreBBs()) {
                    curMB = b2mb.get(pre);
                    Value v = ((Phi) instr).getParentValue(pre);
                    curMB.addInstrBeforeTerm(new Move(mb.val2vir.get(instr), value2VirReg(v), curMB));
                }
            }
        }
    }

    public void inherit() {
        if (curMB.getBb().myIdom == null) {
            curMB.val2vir = new HashMap<>();
            curMB.vir2val = new HashMap<>();
        }
        else {
            curMB.val2vir = new HashMap<>(b2mb.get(curMB.getBb().myIdom).val2vir);
            curMB.vir2val = new HashMap<>(b2mb.get(curMB.getBb().myIdom).vir2val);
        }
    }

    public void genBlock(BasicBlock bb) {
        int nums = bb.getInsts().size();
        for (int i = 0; i < nums; i++) {
            Instruction ins = bb.getInsts().get(i);
            if (ins.isAdd() || ins.isSub() || ins.isMul() || ins.isSdiv() || ins.isSrem()) {
                buildBinary((Binary) ins);
            }
            else if (ins.isIcmp()) {
                if (i != bb.getInsts().size() - 1 && bb.getInsts().get(i + 1).isBr()) {
                    Br br = (Br) bb.getInsts().get(i + 1);
                    buildBr((Icmp) ins, br);
                    i++;
                }
                else {
                    buildBinary((Binary) ins);
                }
            }
            else if (ins.isAlloca()) {
                buildAlloc((Alloca) ins);

            }
            else if (ins.isLoad()) {
                buildLoad((Load) ins);
            }
            else if (ins.isStore()) {
                buildStore((Store) ins);
            }
            else if (ins.isGep()) {
                buildGep((Gep) ins);
            }
            else if (ins.isZextTo()) {
                buildZextTo((ZextTo) ins);
            }
            else if (ins.isBr()) {
                buildBr(null, (Br) ins);
            }
            else if (ins.isRet()) {
                buildRet((Ret) ins);
            }
            else if (ins.isCall()) {
                buildCall((Call) ins);
            }
            else if (ins.isPhi()) {
                buildPhi((Phi) ins);
            }
        }
    }

    private void buildBinary(Binary ins) {
        Value op1 = ins.getOp1();
        Value op2 = ins.getOp2();
        if (ins.isAdd()) {
            if (op1 instanceof ConstantInt) {
                Operand imm = value2Imm((ConstantInt) op1);
                Operand r = value2VirReg(op2);
                curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.addu, value2VirReg(ins), (Reg) r, imm, curMB));
            }
            else if (op2 instanceof ConstantInt) {
                Operand imm = value2Imm((ConstantInt) op2);
                Operand r = value2VirReg(op1);
                curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.addu, value2VirReg(ins), (Reg) r, imm, curMB));
            }
            else {
                Operand r1 = value2VirReg(op1);
                Operand r2 = value2VirReg(op2);
                curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.addu, value2VirReg(ins), (Reg) r1, r2, curMB));
            }
        }
        else if (ins.isSub()) {
            if (op2 instanceof ConstantInt) {
                Operand imm = value2Imm((ConstantInt) op2);
                Operand r = value2VirReg(op1);
                curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.subu, value2VirReg(ins), (Reg) r, imm, curMB));
            }
            else {
                Operand r1 = value2VirReg(op1);
                Operand r2 = value2VirReg(op2);
                curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.subu, value2VirReg(ins), (Reg) r1, r2, curMB));
            }
        }
        else if (ins.isMul()) {
            if (op1 instanceof ConstantInt) {
                Operand imm = value2Imm((ConstantInt) op1);
                Operand r = value2VirReg(op2);
                curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.mulu, value2VirReg(ins), (Reg) r, imm, curMB));
            }
            else if (op2 instanceof ConstantInt) {
                Operand imm = value2Imm((ConstantInt) op2);
                Operand r = value2VirReg(op1);
                curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.mulu, value2VirReg(ins), (Reg) r, imm, curMB));
            }
            else {
                Operand r1 = value2VirReg(op1);
                Operand r2 = value2VirReg(op2);
                curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.mulu, value2VirReg(ins), (Reg) r1, r2, curMB));
            }
        }
        else if (ins.isSdiv()) {
            if (op2 instanceof ConstantInt) {
                Operand imm = value2Imm((ConstantInt) op2);
                Operand r = value2VirReg(op1);
                curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.divu, value2VirReg(ins), (Reg) r, imm, curMB));
            }
            else {
                Operand r1 = value2VirReg(op1);
                Operand r2 = value2VirReg(op2);
                curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.divu, value2VirReg(ins), (Reg) r1, r2, curMB));
            }
        }
        else if (ins.isSrem()) {
            if (op2 instanceof ConstantInt) {
                Operand imm = value2Imm((ConstantInt) op2);
                Operand r = value2VirReg(op1);
                curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.remu, value2VirReg(ins), (Reg) r, imm, curMB));
            }
            else {
                Operand r1 = value2VirReg(op1);
                Operand r2 = value2VirReg(op2);
                curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.remu, value2VirReg(ins), (Reg) r1, r2, curMB));
            }
        }
        else if (ins.isIcmp()) {
            Operand r1, r2;
            if (op2 instanceof ConstantInt) {
                r1 = value2VirReg(op1);
                r2 = value2Imm((ConstantInt) op2);
            }
            else {
                r1 = value2VirReg(op1);
                r2 = value2VirReg(op2);
            }
            switch (((Icmp) ins).getIcmpType()) {
                case SGE -> {
                    curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.sge, value2VirReg(ins), (Reg) r1, r2, curMB));
                }
                case SGT -> {
                    curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.sgt, value2VirReg(ins), (Reg) r1, r2, curMB));
                }
                case SLT -> {
                    curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.slt, value2VirReg(ins), (Reg) r1, r2, curMB));
                }
                case SLE -> {
                    curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.sle, value2VirReg(ins), (Reg) r1, r2, curMB));
                }
                case NE -> {
                    curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.sne, value2VirReg(ins), (Reg) r1, r2, curMB));
                }
                case EQ -> {
                    curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.seq, value2VirReg(ins), (Reg) r1, r2, curMB));
                }
            }
        }
    }

    private void buildBr(Icmp icmp, Br br) {
        if (!br.isCondition()) {
            curMB.addInstr(new InstrJ(InstrJ.JType.j, br.getToBB().getName(), curMB));
        }
        else {
            String tName = br.getTrueBB().getName();
            String fName = br.getFalseBB().getName();
            if (icmp != null) {
                Reg r1 = value2VirReg(icmp.getOp1());
                Reg r2 = value2VirReg(icmp.getOp2());
                switch (icmp.getIcmpType()) {
                    case EQ -> {
                        curMB.addInstr(new InstrB(InstrB.BType.beq, r1, r2, tName, curMB));
                        curMB.addInstr(new InstrJ(InstrJ.JType.j, fName, curMB));
                    }
                    case NE -> {
                        curMB.addInstr(new InstrB(InstrB.BType.bne, r1, r2, tName, curMB));
                        curMB.addInstr(new InstrJ(InstrJ.JType.j, fName, curMB));
                    }
                    case SLE -> {
                        curMB.addInstr(new InstrB(InstrB.BType.ble, r1, r2, tName, curMB));
                        curMB.addInstr(new InstrJ(InstrJ.JType.j, fName, curMB));
                    }
                    case SLT -> {
                        curMB.addInstr(new InstrB(InstrB.BType.blt, r1, r2, tName, curMB));
                        curMB.addInstr(new InstrJ(InstrJ.JType.j, fName, curMB));
                    }
                    case SGT -> {
                        curMB.addInstr(new InstrB(InstrB.BType.bgt, r1, r2, tName, curMB));
                        curMB.addInstr(new InstrJ(InstrJ.JType.j, fName, curMB));
                    }
                    case SGE -> {
                        curMB.addInstr(new InstrB(InstrB.BType.bge, r1, r2, tName, curMB));
                        curMB.addInstr(new InstrJ(InstrJ.JType.j, fName, curMB));
                    }
                }
            }
            else {
                curMB.addInstr(new InstrB(InstrB.BType.beq, value2VirReg(br.getCond()), new Imm(1), tName, curMB));
                curMB.addInstr(new InstrJ(InstrJ.JType.j, fName, curMB));
            }
        }
    }

    private void buildRet(Ret ret) {
        if (curMF.isMain()) {
            curMB.addInstr(new InstrL(phyReg(2), new Imm(10), curMB));
            curMB.addInstr(new Syscall(curMB));
            return;
        }
        if (!ret.isVoid()) {
            if (ret.getReturnVal() instanceof ConstantInt) {
                int val = ((ConstantInt) ret.getReturnVal()).getVal();
                curMB.addInstr(new InstrL(phyReg(2), new Imm(val), curMB));
            }
            else {
                curMB.addInstr(new Move(phyReg(2), value2VirReg(ret.getReturnVal()), curMB));
            }
        }
        InstrJ r = new InstrJ(InstrJ.JType.jr, null, curMB);
        curMF.getStackFp().rets.add(r);
        curMB.addInstr(r);
    }

    private void buildAlloc(Alloca alloca) {
        curMF.addAllocFP(alloca);
    }

    private void buildLoad(Load load) {
        if (curMF.getStackFp().getAllocPointers().containsKey(load.getPointer())) {
            int offset = curMF.getStackFp().getAllocPointers().get(load.getPointer());
            curMB.addInstrBeforeTerm(new InstrM(InstrM.MType.lw, value2VirReg(load), phyReg(30), new Imm(offset), curMB));
        }
        else if (load.getPointer() instanceof GlobalValue) {
            curMB.addInstrBeforeTerm(new InstrM(InstrM.MType.lw, value2VirReg(load), load.getPointer().getName().substring(1), curMB));
        }
        else {
            curMB.addInstrBeforeTerm(new InstrM(InstrM.MType.lw, value2VirReg(load), value2VirReg(load.getPointer()), new Imm(0), curMB));
        }
    }

    private void buildStore(Store store) {
        if (curMF.getStackFp().getAllocPointers().containsKey(store.getPointer())) {
            int offset = curMF.getStackFp().getAllocPointers().get(store.getPointer());
            curMB.addInstrBeforeTerm(new InstrM(InstrM.MType.sw, value2VirReg(store.getStoreVal()), phyReg(30), new Imm(offset), curMB));
        }
        else if (store.getPointer() instanceof GlobalValue) {
            curMB.addInstrBeforeTerm(new InstrM(InstrM.MType.sw, value2VirReg(store.getStoreVal()), store.getPointer().getName().substring(1), curMB));
        }
        else {
            curMB.addInstrBeforeTerm(new InstrM(InstrM.MType.sw, value2VirReg(store.getStoreVal()), value2VirReg(store.getPointer()), new Imm(0), curMB));
        }
    }

    private void buildZextTo(ZextTo zextTo) {
        curMB.addInstrBeforeTerm(new Move(value2VirReg(zextTo), value2VirReg(zextTo.getValue()), curMB));
    }

    private void buildGep(Gep gep) {
        Value base = gep.getBase();

        Value size;
        int num;

        if (gep.getArrayIndex() != null) { //数组定位 局部+全局
            size = gep.getArrayIndex();
            num = ((ArrayType) gep.getBaseType()).getElemType().getSize();
        }
        else {
            size = gep.getPointIndex();
            num = gep.getBaseType().getSize();
        }

        if (size instanceof ConstantInt) {
            curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.addu, value2VirReg(gep), value2VirReg(base), new Imm(num * ((ConstantInt) size).getVal()), curMB));
        }
        else {
            curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.mulu, phyReg(3), value2VirReg(size), new Imm(num), curMB));
            curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.addu, value2VirReg(gep), value2VirReg(base), phyReg(3), curMB));
        }
    }

    private void buildCall(Call call) {
        Function function = call.getFunction();
        if (function.getName().equals("putint")) {
            Value a = call.getArgs().get(0);
            if (a instanceof ConstantInt) {
                curMB.addInstrBeforeTerm(new InstrL(phyReg(4), new Imm(((ConstantInt) a).getVal()), curMB));
            }
            else {
                curMB.addInstrBeforeTerm(new Move(phyReg(4), value2VirReg(a), curMB));
            }
            curMB.addInstrBeforeTerm(new InstrL(phyReg(2), new Imm(1), curMB));
            curMB.addInstrBeforeTerm(new Syscall(curMB));
            curMB.addInstrBeforeTerm(new Move(value2VirReg(call), new PhyReg(2), curMB));

        }
        else if (function.getName().equals("putch")) {
            Value a = call.getArgs().get(0);
            if (a instanceof ConstantInt) {
                curMB.addInstrBeforeTerm(new InstrL(phyReg(4), new Imm(((ConstantInt) a).getVal()), curMB));
            }
            else {
                curMB.addInstrBeforeTerm(new Move(phyReg(4), value2VirReg(a), curMB));
            }
            curMB.addInstrBeforeTerm(new InstrL(phyReg(2), new Imm(11), curMB));
            curMB.addInstrBeforeTerm(new Syscall(curMB));
            curMB.addInstrBeforeTerm(new Move(value2VirReg(call), new PhyReg(2), curMB));
        }
        else if (function.getName().equals("putstr")) {

        }
        else if (function.getName().equals("getint")) {
            curMB.addInstrBeforeTerm(new InstrL(phyReg(2), new Imm(5), curMB));
            curMB.addInstrBeforeTerm(new Syscall(curMB));
            curMB.addInstrBeforeTerm(new Move(value2VirReg(call), phyReg(2), curMB));
        }
        else {
            // 传参
            int fp = 0;
            for (Value v : call.getArgs()) {
                curMB.addInstrBeforeTerm(new InstrM(InstrM.MType.sw, value2VirReg(v), phyReg(29), new Imm(fp), curMB));
                fp += v.getType().getSize();
            }
            curMF.getStackFp().para(fp);
            // 移动帧指针
//            curMB.addInstr(new Move(phyReg(30), phyReg(29), curMB));

            // 调用函数
            InstrJ jal = new InstrJ(InstrJ.JType.jal, function.getName(), curMB);
            curMF.getStackFp().calls.add(jal);
            curMB.addInstrBeforeTerm(jal);
            // 移动帧指针
//            curMB.addInstr(new InstrR(InstrR.RType.add, phyReg(30), phyReg(30), new Imm(-1 * fp), curMB));

            //处理返回值
            if (!call.isVoid()) {
                curMB.addInstrBeforeTerm(new Move(value2VirReg(call), new PhyReg(2), curMB));
            }
        }
    }

    public void buildPhi(Phi phi) {
        value2VirReg(phi);
    }


    private Reg phyReg(int id) {
        return new PhyReg(id);
    }

    private Reg value2VirReg(Value v) {
        if (curMB.val2vir.containsKey(v)) {
            return curMB.val2vir.get(v);
        }
        VirReg vr = new VirReg();
        curMB.val2vir.put(v, vr);
        curMB.vir2val.put(vr, v);
        if (v instanceof ConstantInt) {
            curMB.addInstrBeforeTerm(new InstrL(vr, (Imm) value2Imm((ConstantInt) v), curMB));
        }
        else if (v instanceof GlobalValue) {
            if (((PointerType) v.getType()).getPointtoType() instanceof ArrayType) {
                curMB.addInstrBeforeTerm(new InstrL(vr, v.getName().substring(1), curMB));
            }
        }
        else if (curMF.getStackFp().getAllocPointers().containsKey(v)) {
            int offset = curMF.getStackFp().getAllocPointers().get(v);
            if (!(v instanceof Argument) && (((PointerType) v.getType()).getPointtoType() instanceof ArrayType)) {
                curMB.addInstrBeforeTerm(new InstrR(InstrR.RType.addu, vr, new PhyReg(30), new Imm(offset), curMB));
            }
            else {
                curMB.addInstrBeforeTerm(new InstrM(InstrM.MType.lw, vr, new PhyReg(30), new Imm(offset), curMB));
            }
        }
        return vr;
    }

    private Operand value2Imm(ConstantInt v) {
        return new Imm(v.getVal());
    }
}
