package midend.ir.values.instructions.memory;

import midend.ir.types.Type;
import midend.ir.values.BasicBlock;
import midend.ir.values.instructions.Instruction;
import midend.ir.values.Value;

public class ZextTo extends Instruction {
//    private Value value;

    public ZextTo(String name, BasicBlock parent, Value value, Type toType) {
        super(name, toType, parent, value);
//        this.value = value;
        defValue.add(this);
//        useValue.add(value);
    }

    public Value getValue() {
        return getUses().get(0);
    }
//    @Override
//    public void update() {
//        value = getUses().get(0);
//    }

    @Override
    public String toString() {
        return this.getName() + " = zext " + getValue().getType() + " " + getValue().getName() + " to " + this.getType();
    }
}
