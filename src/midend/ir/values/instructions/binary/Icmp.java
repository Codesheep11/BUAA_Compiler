package midend.ir.values.instructions.binary;

import midend.ir.types.IntType;
import midend.ir.values.BasicBlock;
import midend.ir.values.Value;

public class Icmp extends Binary {

    public enum IcmpType {
        EQ, NE, SGT, SGE, SLT, SLE;

        @Override
        public String toString() {
            switch (this) {
                case EQ: {
                    return "eq";
                }
                case NE: {
                    return "ne";
                }
                case SGE: {
                    return "sge";
                }
                case SGT: {
                    return "sgt";
                }
                case SLE: {
                    return "sle";
                }
                case SLT: {
                    return "slt";
                }
            }
            return null;
        }
    }

    private IcmpType icmpType;  // 比较类型

    public Icmp(String name, BasicBlock parent, Value op1, Value op2, IcmpType icmpType) {
        super(name, new IntType(1), parent, op1, op2);
        this.icmpType = icmpType;
    }

    public IcmpType getIcmpType() {
        return icmpType;
    }

    @Override
    public String toString() {
        return this.getName() + " = icmp " + this.icmpType.toString() + " " + getOp1().getType() + " " + getOp1().getName() + ", " + getOp2().getName();
    }

}