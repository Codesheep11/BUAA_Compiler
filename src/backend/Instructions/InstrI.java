package backend.Instructions;

import backend.Component.MipsBlock;
import backend.Operand.Imm;
import backend.Operand.Reg;
import backend.Operand.VirReg;

import java.util.ArrayList;

public class InstrI extends Instr {
    private IType iType;

    public enum IType {

        addi,
        subi,
        andi,
        ori,
        sll;

        @Override
        public String toString() {
            switch (this) {
                case addi: {
                    return "addiu";
                }
                case subi: {
                    return "subiu";
                }
                case andi: {
                    return "andi";
                }
                case ori: {
                    return "ori";
                }
                case sll: {
                    return "sll";
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

    public InstrI(IType iType, Reg rt, Reg rs, Imm imm, MipsBlock mb) {
        this.iType = iType;
        this.rt = rt;
        this.rs = rs;
        this.imm = imm;
        this.mb = mb;
        if (rt instanceof VirReg) {
            defRegs.add((VirReg) rt);
            ((VirReg) rt).instrs.add(this);
        }
        if (rs instanceof VirReg) {
            useRegs.add((VirReg) rs);
            ((VirReg) rs).instrs.add(this);
        }

    }


    @Override
    public String toString() {
        return iType.toString() + " " + rt.toString() + ", " + rs.toString() + ", " + imm;
    }

}
