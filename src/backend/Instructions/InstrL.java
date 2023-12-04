package backend.Instructions;

import backend.Component.MipsBlock;
import backend.Operand.Imm;
import backend.Operand.Reg;
import backend.Operand.VirReg;

import java.util.ArrayList;

public class InstrL extends Instr {
    private Reg rt;
    private Imm imm;

    private String gv = null;

    public InstrL(Reg rt, Imm imm, MipsBlock mb) {
        this.rt = rt;
        this.imm = imm;
        this.mb = mb;
        if (rt instanceof VirReg) {
            defRegs.add((VirReg) rt);
        }
    }

    public InstrL(Reg rt, String gv, MipsBlock mb) {
        this.rt = rt;
        this.gv = gv;
        this.mb = mb;
        if (rt instanceof VirReg) {
            defRegs.add((VirReg) rt);
            ((VirReg) rt).instrs.add(this);
        }
    }


    @Override
    public String toString() {
        if (gv == null) {
            return "li " + rt + ", " + imm;
        }
        else {
            return "la " + rt + ", " + gv;
        }
    }


}
