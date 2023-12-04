package midend.ir.values;

import midend.ir.types.LabelType;
import midend.ir.values.instructions.Instruction;
import midend.ir.values.instructions.memory.Phi;
import midend.ir.values.instructions.terminator.Br;
import midend.ir.values.instructions.terminator.Ret;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BasicBlock extends Value {
    private ArrayList<Instruction> insts;

    private ArrayList<BasicBlock> nxtBBs;
    private ArrayList<BasicBlock> preBBs;

    public HashSet<BasicBlock> doms;
    public HashSet<BasicBlock> idoms;
    public BasicBlock myIdom;

    public HashSet<BasicBlock> domFrontier;

    public HashSet<Phi> phis = new HashSet<Phi>();


    public BasicBlock(String name, Function function) {
        super(name, new LabelType(), function);
        insts = new ArrayList<>();
        nxtBBs = new ArrayList<>();
        preBBs = new ArrayList<>();
    }

    public void addNxtBB(BasicBlock bb) {
        nxtBBs.add(bb);
    }

    public void remove() {
        ((Function) getParent()).getBbs().remove(this);
        for (BasicBlock nxt : getNxtBBs()) {
            nxt.getPreBBs().remove(this);
        }
        nxtBBs = new ArrayList<>();
    }

    public void addPreBB(BasicBlock bb) {
        preBBs.add(bb);
    }

    public ArrayList<BasicBlock> getNxtBBs() {
        return nxtBBs;
    }

    public ArrayList<BasicBlock> getPreBBs() {
        return preBBs;
    }


    public boolean addInst(Instruction inst) {
        if (!insts.isEmpty()) {
            Instruction pins = insts.get(insts.size() - 1);
            if (pins instanceof Br || pins instanceof Ret) {
                return false;
            }
            inst.preInstr.add(pins);
            pins.nxtInstr.add(inst);
        }
        insts.add(inst);
        return true;
    }

    public void deleteInst(Instruction inst) {
        insts.remove(inst);
    }

    public void insertPhi(Instruction phi) {
        insts.add(0, phi);
        phis.add((Phi) phi);
    }

    public void removeNxtBB(BasicBlock nxtBB) {
        int index = nxtBB.preBBs.indexOf(this);
        if (index == -1) {
            return;
        }
        for (Phi phi : nxtBB.phis) {
            phi.getUses().remove(index);
        }
        nxtBBs.remove(nxtBB);
        nxtBB.preBBs.remove(this);
    }

    public Instruction getFirst() {
        return insts.get(0);
    }

    public Instruction getLast() {
        if (insts.isEmpty()) {
            return null;
        }
        return insts.get(insts.size() - 1);
    }

    public ArrayList<Instruction> getInsts() {
        return insts;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName() + ":" + "\n");
        for (Instruction inst : insts) {
            sb.append("    " + inst + "\n");
        }
        return sb.toString();
    }
}
