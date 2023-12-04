package backend.Operand;

import backend.Component.StackFp;

public class Imm extends Operand {

    private int val;

    public Imm(int imm) {
        this.val = imm;
    }

    @Override
    public String toString() {
        return Integer.toString(val);
    }
}
