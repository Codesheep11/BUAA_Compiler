package midend.ir.values.instructions.memory;

import midend.ir.types.Type;
import midend.ir.values.BasicBlock;
import midend.ir.values.Value;
import midend.ir.values.constants.ConstantInt;
import midend.ir.values.instructions.Instruction;

import java.util.ArrayList;

public class Phi extends Instruction {

    //%4 = phi i32 [ 1, %2 ], [ %6, %5 ]

    private static int cntPhi = 0;

    public Phi(Type type, BasicBlock parent) {
        super("%_phi" + cntPhi++, type, parent);
        defValue.add(this);
//        valuesFromPre = new ArrayList<>();
        for (BasicBlock pre : parent.getPreBBs()) {
            Value v = new ConstantInt(32, 0);
            addUseValue(v);
        }
    }

    public Value getParentValue(BasicBlock par) {
        Value v = null;
        for (int i = 0; i < ((BasicBlock) getParent()).getPreBBs().size(); i++) {
            if (par == ((BasicBlock) getParent()).getPreBBs().get(i)) {
                v = getUses().get(i);
                break;
            }
        }
        return v;
    }


    @Override
    public String toString() {
        BasicBlock bb = (BasicBlock) getParent();
        StringBuilder sb = new StringBuilder(getName() + " = phi " + getType().toString() + " ");
        for (int i = 0; i < bb.getPreBBs().size(); i++) {
            Value value = getUses().get(i);
            sb.append("[ " + value.getName() + ", %");
            sb.append(bb.getPreBBs().get(i).getName());
            sb.append(" ]");
            if (i != bb.getPreBBs().size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
