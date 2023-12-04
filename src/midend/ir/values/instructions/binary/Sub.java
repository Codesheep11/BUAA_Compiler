package midend.ir.values.instructions.binary;

import midend.ir.types.IntType;
import midend.ir.values.BasicBlock;
import midend.ir.values.Value;

public class Sub extends Binary {
    public Sub(String name, BasicBlock parent, Value op1, Value op2) {
        super(name, new IntType(32), parent, op1, op2);
    }
    @Override
    public String toString() {
        return this.getName() + " = sub " + this.getType() + " " + getOp1().getName() + ", " + getOp2().getName();
    }
}
