package midend.ir.values.instructions.memory;

import midend.ir.types.Type;
import midend.ir.values.BasicBlock;
import midend.ir.values.instructions.Instruction;
import midend.ir.values.Value;

public class Load extends Instruction {
//    private Value pointer;

    public Load(String name, Type type, BasicBlock parent, Value pointer) {
        super(name, type, parent, pointer);
//        this.pointer = pointer;
        defValue.add(this);
//        useValue.add(pointer);
    }

    public Value getPointer() {
        return getUses().get(0);
    }

    @Override
    public String toString() {
        return this.getName() + " = load " + this.getType() + ", " + getPointer().getType() + " " + getPointer().getName();
    }
}
