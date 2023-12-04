package midend.ir.values;

import midend.ir.IRPort;
import midend.ir.types.FuncType;
import midend.ir.types.Type;
import midend.ir.types.VoidType;
import midend.ir.values.instructions.Instruction;
import midend.ir.values.instructions.terminator.Br;
import midend.ir.values.instructions.terminator.Ret;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Function extends Value {
    private ArrayList<Argument> args;
    private ArrayList<BasicBlock> bbs;

    public HashMap<Value, HashSet<Value>> CG;//冲突图

    public HashMap<BasicBlock, HashSet<BasicBlock>> CFG;//控制流图


    public Function(FuncType ft, String name) {
        super(name, ft, null);
        args = new ArrayList<>();
        bbs = new ArrayList<>();
    }


    public void addBB(BasicBlock bb) {
        bbs.add(bb);
    }

    public void addArg(Argument arg) {
        args.add(arg);
    }

    public void deleteBB(BasicBlock bb) {
        bbs.remove(bb);
    }

    public ArrayList<Argument> getArgs() {
        return args;
    }

    public BasicBlock getEntry() {
        return bbs.get(0);
    }

    public void retCheck() {
        if (((FuncType) getType()).getRetType() instanceof VoidType) {
            for (BasicBlock bb : bbs) {
                if (bb.getInsts().size() == 0) {
                    if (bbs.size() == 1) {
                        IRPort.newVoidRet(bb);
                        break;
                    }
                    else if (bb.getUsers().isEmpty()) {
                        continue;
                    }
                }
                Instruction ins = bb.getLast();
                if (!(ins instanceof Ret || ins instanceof Br)) {
                    IRPort.newVoidRet(bb);
                }
            }
        }
    }

    public ArrayList<BasicBlock> getBbs() {
        return bbs;
    }

    public ArrayList<BasicBlock> genBFSArray() {
        ArrayList<BasicBlock> array = new ArrayList<>();
        ArrayList<BasicBlock> flag = new ArrayList<>();
        flag.add(getEntry());
        while (!flag.isEmpty()) {
            BasicBlock bb = flag.get(0);
            flag.remove(bb);
            array.add(bb);
            for (BasicBlock idom : bb.idoms) {
                flag.add(idom);
            }
        }
        return array;
    }

    public ArrayList<BasicBlock> genDFSArray() {
        ArrayList<BasicBlock> array = new ArrayList<>();
        ArrayList<BasicBlock> flag = new ArrayList<>();
        flag.add(getEntry());
        while (!flag.isEmpty()) {
            BasicBlock bb = flag.get(0);
            flag.remove(bb);
            array.add(0, bb);
            for (BasicBlock idom : bb.idoms) {
                flag.add(idom);
            }
        }
        return array;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (((FuncType) getType()).isDefine()) {
            sb.append("define ");
        }
        else {
            sb.append("declare ");
        }
        sb.append("dso_local " + getType() + " @" + getName());
        sb.append("(");
        if (!((FuncType) getType()).isDefine()) {
            ArrayList<Type> types = ((FuncType) getType()).getArgs();
            if (types.size() != 0) {
                sb.append(types.get(0));
            }
            for (int i = 1; i < types.size(); i++) {
                sb.append(", " + types.get(i));
            }
            sb.append(")\n");
            return sb.toString();
        }
        if (args.size() != 0) {
            sb.append(args.get(0));
        }
        for (int i = 1; i < args.size(); i++) {
            sb.append(", " + args.get(i));
        }
        sb.append(") ");
        sb.append("{\n");
        for (BasicBlock bb : bbs) {
            sb.append(bb);
        }
        sb.append("}\n");
        return sb.toString();
    }

}
