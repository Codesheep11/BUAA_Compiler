package backend.Instructions;

import backend.Component.MipsBlock;
import backend.Operand.Imm;
import backend.Operand.Operand;
import backend.Operand.Reg;
import backend.Operand.VirReg;

public class InstrR extends Instr {
    private RType rType;

    public enum RType {

        addu,
        subu,
        mulu,
        divu,
        remu,
        and,
        or,
        sgt,
        sge,
        slt,
        sle,
        seq,
        sne,
        sllv;

        @Override
        public String toString() {
            switch (this) {
                case addu: {
                    return "addu";
                }
                case subu: {
                    return "subu";
                }
                case mulu: {
                    return "mul";
                }
                case divu: {
                    return "div";
                }
                case remu: {
                    return "rem";
                }
                case and: {
                    return "and";
                }
                case or: {
                    return "or";
                }
                case sgt: {
                    return "sgt";
                }
                case sge: {
                    return "sge";
                }
                case sle: {
                    return "sle";
                }
                case seq: {
                    return "seq";
                }
                case slt: {
                    return "slt";
                }
                case sne: {
                    return "sne";
                }
                case sllv: {
                    return "sllv";
                }
                default: {
                    return null;
                }
            }
        }

    }

    private Reg rd;
    private Reg rs;
    private Operand rt;

    public InstrR(RType rType, Reg rd, Reg rs, Operand rt, MipsBlock mb) {
        this.rType = rType;
        this.rd = rd;
        this.rs = rs;
        this.rt = rt;
        this.mb = mb;
        if (rd instanceof VirReg) {
            defRegs.add((VirReg) rd);
            ((VirReg) rd).instrs.add(this);
        }
        if (rs instanceof VirReg) {
            useRegs.add((VirReg) rs);
            ((VirReg) rs).instrs.add(this);
        }
        if (rt instanceof VirReg) {
            useRegs.add((VirReg) rt);
            ((VirReg) rt).instrs.add(this);
        }
    }


    @Override
    public String toString() {
        if(rType ==RType.slt && rt instanceof Imm){
            return rType.toString() + "i " + rd.toString() + ", " + rs.toString() + ", " + rt.toString();
        }
        return rType.toString() + " " + rd.toString() + ", " + rs.toString() + ", " + rt.toString();

    }
}
