package midend.ir.values.constants;

import midend.ir.types.IntType;

public class ConstantInt extends Constant {

    private int val;

    public ConstantInt(int bits, int val) {
        super(Integer.toString(val), new IntType(bits), null);
        this.val = val;
    }

    public int getVal() {
        return val;
    }

}
