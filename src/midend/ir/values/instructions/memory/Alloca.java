package midend.ir.values.instructions.memory;

import midend.ir.types.PointerType;
import midend.ir.types.Type;
import midend.ir.values.BasicBlock;
import midend.ir.values.instructions.Instruction;

public class Alloca extends Instruction {
    public Alloca(String name, Type allocatedType, BasicBlock parent) {
        super(name, new PointerType(allocatedType), parent);
        defValue.add(this);
    }

    @Override
    public String toString() {
        return this.getName() + " = alloca " + ((PointerType) this.getType()).getPointtoType();
    }
}
