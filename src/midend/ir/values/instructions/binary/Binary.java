package midend.ir.values.instructions.binary;

import midend.ir.types.Type;
import midend.ir.values.BasicBlock;
import midend.ir.values.constants.ConstantInt;
import midend.ir.values.instructions.Instruction;
import midend.ir.values.Value;

import java.util.HashSet;

public class Binary extends Instruction {

    public Binary(String name, Type type, BasicBlock parent, Value op1, Value op2) {
        super(name, type, parent, op1, op2);
//        useValue.add(op1);
//        useValue.add(op2);
        defValue.add(this);
    }

    public Value getOp1() {
        return getUses().get(0);
    }

    public Value getOp2() {
        return getUses().get(1);
    }

    public ConstantInt genConst() {
        int op1 = ((ConstantInt) getOp1()).getVal();
        int op2 = ((ConstantInt) getOp2()).getVal();
        if (this.isAdd()) {
            return new ConstantInt(32, op1 + op2);
        }
        else if (this.isSub()) {
            return new ConstantInt(32, op1 - op2);
        }
        else if (this.isMul()) {
            return new ConstantInt(32, op1 * op2);
        }
        else if (this.isSdiv()) {
            return new ConstantInt(32, op1 / op2);
        }
        else if (this.isSrem()) {
            return new ConstantInt(32, op1 % op2);
        }
        else if (this.isIcmp()) {
            if (((Icmp) this).getIcmpType() == Icmp.IcmpType.EQ) {
                return new ConstantInt(1, op1 == op2 ? 1 : 0);
            }
            else if (((Icmp) this).getIcmpType() == Icmp.IcmpType.NE) {
                return new ConstantInt(1, op1 != op2 ? 1 : 0);
            }
            else if (((Icmp) this).getIcmpType() == Icmp.IcmpType.SGE) {
                return new ConstantInt(1, op1 >= op2 ? 1 : 0);
            }
            else if (((Icmp) this).getIcmpType() == Icmp.IcmpType.SGT) {
                return new ConstantInt(1, op1 > op2 ? 1 : 0);
            }
            else if (((Icmp) this).getIcmpType() == Icmp.IcmpType.SLE) {
                return new ConstantInt(1, op1 <= op2 ? 1 : 0);
            }
            else if (((Icmp) this).getIcmpType() == Icmp.IcmpType.SLT) {
                return new ConstantInt(1, op1 < op2 ? 1 : 0);
            }

        }
        return null;
    }
}
