package backend.Component;

import backend.Operand.PhyReg;
import backend.Operand.VirReg;
import midend.ir.types.PointerType;
import midend.ir.values.Argument;
import midend.ir.values.Function;
import midend.ir.values.Value;
import midend.ir.values.instructions.memory.Alloca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MipsFunction {
    private String name;
    private Function function;

    private boolean isMain;
    private StackFp stackFp;

    private ArrayList<MipsBlock> bbs;

//    public HashSet<PhyReg> regs;


    public MipsFunction(Function function) {
        this.name = function.getName();
        if (name.equals("main")) {
            isMain = true;
        }
        this.function = function;
        this.bbs = new ArrayList<>();
        stackFp = new StackFp(this);
        int offset = 0;
        for (Argument arg : function.getArgs()) {
            stackFp.getAllocPointers().put(arg, offset);
            offset += arg.getType().getSize();
        }
    }

    public boolean isMain() {
        return isMain;
    }

    public void addAllocFP(Alloca alloca) {
        stackFp.alloc(alloca, ((PointerType) alloca.getType()).getPointtoType().getSize());
    }

    public StackFp getStackFp() {
        return stackFp;
    }

    public void addBB(MipsBlock mb) {
        bbs.add(mb);
    }

    public ArrayList<MipsBlock> getBbs() {
        return bbs;
    }


    public Function getFunction() {
        return function;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name + ":\n");
        sb.append("\taddi $sp, $sp, " + stackFp.getStack() + "\n");
        for (MipsBlock mb : bbs) {
            sb.append(mb + "\n");
        }
        return sb.toString();
    }
}
