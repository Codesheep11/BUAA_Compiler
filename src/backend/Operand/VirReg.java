package backend.Operand;

import backend.Instructions.Instr;

import java.util.ArrayList;

public class VirReg extends Reg {
    private static int Cnt = 0;

    private int virCnt;

    public ArrayList<Instr> instrs = new ArrayList<>();

    private PhyReg phyReg = null;
    public boolean merge;
    public VirReg MergeReg;

    public VirReg() {
        virCnt = Cnt++;
    }

    public void setPhyReg(PhyReg phyReg) {
        this.phyReg = phyReg;
    }


    public PhyReg getPhyReg() {
        return phyReg;
    }

    public int getVirCnt() {
        return virCnt;
    }

    @Override
    public String toString() {
        if (phyReg != null) {
            return phyReg.toString();
        }
        else {
            return "vr" + virCnt;
        }
    }
}
