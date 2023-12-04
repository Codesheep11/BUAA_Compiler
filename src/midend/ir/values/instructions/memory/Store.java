package midend.ir.values.instructions.memory;

import midend.ir.types.VoidType;
import midend.ir.values.BasicBlock;
import midend.ir.values.instructions.Instruction;
import midend.ir.values.Value;

public class Store extends Instruction {

//    private Value storeVal;
//    private Value pointer;

    public Store(BasicBlock parent, Value storeVal, Value pointer) {
        super("", new VoidType(), parent, storeVal, pointer);
//        this.storeVal = storeVal;
//        this.pointer = pointer;
//        defValue.add(this);
//        useValue.add(storeVal);
//        useValue.add(pointer);
    }

    public Value getPointer() {
        return getUses().get(1);
    }

    public Value getStoreVal() {
        return getUses().get(0);
    }
//    @Override
//    public void update() {
//        storeVal = getUses().get(0);
//    }

    @Override
    public String toString() {
        return "store " + getStoreVal().getType() + " " + getStoreVal().getName() + ", " + getPointer().getType() + " " + getPointer().getName();
    }
}