package midend.ir.values.instructions.terminator;

import midend.ir.types.VoidType;
import midend.ir.values.BasicBlock;
import midend.ir.values.instructions.Instruction;
import midend.ir.values.Value;

import java.util.HashSet;

public class Ret extends Instruction {
    private boolean isVoid;

//    private Value returnVal = null;

    public Ret(BasicBlock parent) {
        super("", new VoidType(), parent);
        isVoid = true;
    }

    public Ret(BasicBlock parent, Value returnVal) {
        super("", returnVal.getType(), parent, returnVal);
        isVoid = false;
//        this.returnVal = returnVal;
//        useValue.add(returnVal);
    }

//    @Override
//    public void update() {
//        returnVal = getUses().get(0);
//        useValue = new HashSet<>();
//        useValue.add(returnVal);
//    }

    public boolean isVoid() {
        return isVoid;
    }

    public Value getReturnVal() {
        return getUses().get(0);
    }

    @Override
    public String toString() {
        if (isVoid) {
            return "ret void";
        }
        else {
            return "ret " + getReturnVal().getType() + " " + getReturnVal().getName();
        }
    }
}
