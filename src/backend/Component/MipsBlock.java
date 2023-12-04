package backend.Component;

import backend.Instructions.Instr;
import backend.Instructions.InstrB;
import backend.Instructions.InstrJ;
import backend.Operand.VirReg;
import midend.ir.values.BasicBlock;
import midend.ir.values.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MipsBlock {
    public String label;

    private BasicBlock bb;
    private ArrayList<Instr> instrs;

    public MipsBlock(BasicBlock bb) {
        label = bb.getName();
        this.bb = bb;
        instrs = new ArrayList<>();
    }

    public HashMap<VirReg, Value> vir2val;
    public HashMap<Value, VirReg> val2vir;

    private Set<VirReg> useVirRegs = new HashSet<>();
    private Set<VirReg> defVirRegs = new HashSet<>();
    private Set<VirReg> inVirRegs = new HashSet<>();
    private Set<VirReg> outVirRegs = new HashSet<>();

    public void addInstr(Instr instr) {
        instrs.add(instr);
    }

    public void addInstrBeforeTerm(Instr instr) {
        Instr term = null;
        for (Instr ins : instrs) {
            if (ins instanceof InstrB || (ins instanceof InstrJ && ((InstrJ) ins).getjType() == InstrJ.JType.j)) {
                term = ins;
                break;
            }
        }
        if (term != null) {
            insetBeforeInstr(instr, term);
        }
        else {
            addInstr(instr);
        }
    }

    public void insetBeforeInstr(Instr instr, Instr node) {
        for (int i = 0; i < instrs.size(); i++) {
            if (instrs.get(i) == node) {
//                instr.preInstr = node.preInstr;
//                instr.nxtInstr.add(node);
//                node.preInstr = new HashSet<>();
//                node.preInstr.add(instr);
                instrs.add(i, instr);
                return;
            }
        }
    }

    public void insetAfterInstr(Instr instr, Instr node) {
        for (int i = 0; i < instrs.size(); i++) {
            if (instrs.get(i) == node) {
//                instr.nxtInstr = node.nxtInstr;
//                instr.preInstr.add(node);
//                node.nxtInstr = new HashSet<>();
//                node.nxtInstr.add(instr);
                instrs.add(i + 1, instr);
                return;
            }
        }
    }

    public ArrayList<Instr> getInstrs() {
        return instrs;
    }

    public Set<VirReg> getDefVirRegs() {
        return defVirRegs;
    }

    public Set<VirReg> getUseVirRegs() {
        return useVirRegs;
    }

    public Set<VirReg> getInVirRegs() {
        return inVirRegs;
    }

    public void setInVirRegs(Set<VirReg> inVirRegs) {
        this.inVirRegs = inVirRegs;
    }

    public Set<VirReg> getOutVirRegs() {
        return outVirRegs;
    }

    public BasicBlock getBb() {
        return bb;
    }

    public Instr getFirstIns() {
        return instrs.get(0);
    }

    public Instr getLastIns() {
        return instrs.get(getInstrs().size() - 1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(label + ":\n");
        for (Instr i : instrs) {
            sb.append("\t" + i + "\n");
        }
        return sb.toString();
    }

}
