package backend.Instructions;

import backend.Component.MipsBlock;
import backend.Operand.Reg;
import backend.Operand.VirReg;

import java.util.ArrayList;

public class Move extends Instr {
    private Reg rd;
    private Reg rs;

    public Move(Reg rd, Reg rs, MipsBlock mb) {
        this.rd = rd;
        this.rs = rs;
        this.mb = mb;
        if (rd instanceof VirReg) {
            defRegs.add((VirReg) rd);
            ((VirReg) rd).instrs.add(this);
        }
        if (rs instanceof VirReg) {
            useRegs.add((VirReg) rs);
            ((VirReg) rs).instrs.add(this);
        }
    }

    public boolean isPhiMove() {
        return getRd() instanceof VirReg && getRs() instanceof VirReg;
    }

    public Reg getRd() {
        return rd;
    }

    public Reg getRs() {
        return rs;
    }

    @Override
    public String toString() {
        return "move " + rd.toString() + ", " + rs.toString();
    }
}
