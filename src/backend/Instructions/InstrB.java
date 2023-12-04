package backend.Instructions;

import backend.Component.MipsBlock;
import backend.Operand.Imm;
import backend.Operand.Operand;
import backend.Operand.Reg;
import backend.Operand.VirReg;

import java.util.ArrayList;

public class InstrB extends Instr {

    public BType bType;


    public enum BType {

        beq,
        bne,
        bgt,
        bge,
        ble,
        blt;

        @Override
        public String toString() {
            switch (this) {
                case beq: {
                    return "beq";
                }
                case bne: {
                    return "bne";
                }
                case bgt: {
                    return "bgt";
                }
                case bge: {
                    return "bge";
                }
                case blt: {
                    return "blt";
                }
                case ble: {
                    return "ble";
                }
                default: {
                    return null;
                }
            }
        }

    }

    private Reg rd;
    private Operand rs;
    private String label;

    public InstrB(BType bType, Reg rd, Operand rs, String label, MipsBlock mb) {
        this.bType = bType;
        this.rd = rd;
        this.rs = rs;
        this.label = label;
        this.mb = mb;

        if (rd instanceof VirReg) {
            useRegs.add((VirReg) rd);
            ((VirReg) rd).instrs.add(this);
        }
        if (rs instanceof VirReg) {
            useRegs.add((VirReg) rs);
            ((VirReg) rs).instrs.add(this);
        }
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return bType.toString() + " " + rd.toString() + ", " + rs.toString() + ", " + label.toString();
    }
}
