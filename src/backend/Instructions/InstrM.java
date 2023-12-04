package backend.Instructions;

import backend.Component.MipsBlock;
import backend.Operand.Imm;
import backend.Operand.Reg;
import backend.Operand.VirReg;

import java.util.ArrayList;

public class InstrM extends Instr {
    private MType mType;


    public enum MType {

        lw,
        sw;

        @Override
        public String toString() {
            switch (this) {
                case lw: {
                    return "lw";
                }
                case sw: {
                    return "sw";
                }
                default: {
                    return null;
                }
            }
        }

    }

    private Reg rt;
    private Reg rs;
    private Imm imm;

    private String gvName = null;

    public InstrM(MType mType, Reg rt, Reg rs, Imm imm, MipsBlock mb) {
        this.mType = mType;
        this.rt = rt;
        this.rs = rs;
        this.imm = imm;
        this.mb = mb;
        if (mType == MType.lw && rt instanceof VirReg) {
            defRegs.add((VirReg) rt);
            ((VirReg) rt).instrs.add(this);
        }
        if (mType == MType.sw && rt instanceof VirReg) {
            useRegs.add((VirReg) rt);
            ((VirReg) rt).instrs.add(this);
        }
        if (rs instanceof VirReg) {
            useRegs.add((VirReg) rs);
            ((VirReg) rs).instrs.add(this);
        }
    }

    public InstrM(MType mType, Reg rt, String gvName, MipsBlock mb) {
        this.mType = mType;
        this.rt = rt;
        this.gvName = gvName;
        this.mb = mb;
        if (mType == MType.lw && rt instanceof VirReg) {
            defRegs.add((VirReg) rt);
        }
        if (mType == MType.sw && rt instanceof VirReg) {
            useRegs.add((VirReg) rt);
        }
    }


    @Override
    public String toString() {
        if (gvName == null) {
            return mType.toString() + " " + rt.toString() + ", " + imm + "(" + rs.toString() + ")";
        }
        else {
            return mType.toString() + " " + rt.toString() + ", " + gvName;
        }
    }
}
