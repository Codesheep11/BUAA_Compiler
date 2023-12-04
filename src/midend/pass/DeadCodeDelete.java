package midend.pass;

import midend.ir.values.BasicBlock;
import midend.ir.values.Function;
import midend.ir.values.Value;
import midend.ir.values.constants.ConstantInt;
import midend.ir.values.instructions.Instruction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DeadCodeDelete {
    private Function func;
    private HashSet<Instruction> usefuls = new HashSet<>();
    private HashSet<Instruction> instructions = new HashSet<>();

    public DeadCodeDelete(Function func) {
        this.func = func;
        run();
    }

    public void run() {
        genUsefuls();
        Delete();
    }

//    public void runAnalysis() {
//        int cntBB = func.getBbs().size();
//        //gen use
//        for (int i = 0; i < cntBB; i++) {
//            BasicBlock bb = func.getBbs().get(i);
//            int cntIns = bb.getInsts().size();
//            for (int j = 0; j < cntIns; j++) {
//                Instruction ins = bb.getInsts().get(j);
//                ins.useValue = new HashSet<>();
//                for (Value v : ins.getUses()) {
//                    if (v instanceof ConstantInt) {
//                        continue;
//                    }
//                    ins.useValue.add(v);
//                }
//            }
//        }
//        //gen in out
//        boolean changed = true;
//        while (changed) {
//            changed = false;
//            for (int i = cntBB - 1; i >= 0; i--) {
//                BasicBlock bb = func.getBbs().get(i);
//                int cntIns = bb.getInsts().size();
//                for (int j = cntIns - 1; j >= 0; j--) {
//                    // out = ∪ (in of nxt)
//                    Instruction ins = bb.getInsts().get(j);
//                    for (Instruction nIns : ins.nxtInstr) {
//                        ins.outValue.addAll(nIns.inValue);
//                    }
//
//                    // in = use ∪ (out - def)
//                    Set<Value> inAns = new HashSet<>(ins.outValue);
//                    inAns.retainAll(ins.defValue);
//                    inAns.addAll(ins.useValue);
//                    if (!inAns.equals(ins.inValue)) {
//                        changed = true;
//                        ins.inValue = inAns;
//                    }
//                }
//            }
//        }
//        for (int i = 0; i < cntBB; i++) {
//            BasicBlock bb = func.getBbs().get(i);
//            int cntIns = bb.getInsts().size();
//            for (int j = 0; j < cntIns; j++) {
//                Instruction ins = bb.getInsts().get(j);
//                System.out.println(ins);
//                System.out.println("def:" + ins.defValue);
//                System.out.println("use:" + ins.useValue);
//                System.out.println("in:" + ins.inValue);
//                System.out.println("out:" + ins.outValue);
//                System.out.println();
//            }
//        }
//    }

    private void genUsefuls() {
        int cntBB = func.getBbs().size();
        for (int i = 0; i < cntBB; i++) {
            BasicBlock bb = func.getBbs().get(i);
            int cntIns = bb.getInsts().size();
            for (int j = 0; j < cntIns; j++) {
                Instruction ins = bb.getInsts().get(j);
                instructions.add(ins);
                if (ins.isUseFul()) {
                    HashMap<Instruction, Boolean> curUse = new HashMap<Instruction, Boolean>();
                    curUse.put(ins, false);
                    boolean changed = true;
                    while (changed) {
                        changed = false;
                        for (Instruction u : curUse.keySet()) {
                            if (!curUse.get(u)) {
                                for (Value v : u.getUses()) {
                                    if (v instanceof Instruction && !curUse.containsKey(v)) {
                                        curUse.put((Instruction) v, false);
                                        changed = true;
                                    }
                                }
                                curUse.put(u, true);
                            }
                            if (changed) {
                                break;
                            }
                        }
                    }
                    usefuls.addAll(curUse.keySet());
                }
            }
        }
    }

    private void Delete() {
        instructions.removeAll(usefuls);
        for (Instruction ins : instructions) {
            ins.remove();
        }
    }
}
