package midend.ir.values.instructions.memory;

import midend.ir.types.ArrayType;
import midend.ir.types.PointerType;
import midend.ir.types.Type;
import midend.ir.values.BasicBlock;
import midend.ir.values.instructions.Instruction;
import midend.ir.values.Value;

public class Gep extends Instruction {
    private Type baseType;

//    private Value base;
//
//    private Value pointIndex;
//    private Value arrayIndex = null;


    //数组定位 局部+全局
    public Gep(String name, ArrayType baseType, BasicBlock parent, Value base, Value pointIndex, Value arrayIndex) {
        super(name, new PointerType(baseType.getElemType()), parent, base, pointIndex, arrayIndex);
        this.baseType = baseType;
//        this.base = base;
//        this.arrayIndex = arrayIndex;
//        this.pointIndex = pointIndex;
        defValue.add(this);
//        useValue.add(pointIndex);
    }

    //数组实参
    public Gep(String name, PointerType baseType, BasicBlock parent, Value base, Value pointIndex, Value arrayIndex) {
        super(name, new PointerType(((ArrayType) baseType.getPointtoType()).getElemType()),
                parent, base, pointIndex, arrayIndex);
        this.baseType = baseType.getPointtoType();
//        this.base = base;
//        this.arrayIndex = arrayIndex;
//        this.pointIndex = pointIndex;
        defValue.add(this);
//        useValue.add(pointIndex);
    }

    //数组形参
    public Gep(String name, PointerType baseType, BasicBlock parent, Value base, Value pointIndex) {
        super(name, baseType, parent, base, pointIndex);
        this.baseType = baseType.getPointtoType();
//        this.base = base;
//        this.pointIndex = pointIndex;
        defValue.add(this);
//        useValue.add(pointIndex);
    }

    public Type getBaseType() {
        return baseType;
    }

    public Value getBase() {
        return getUses().get(0);
    }

    public Value getArrayIndex() {
        if (getValueNum() == 3) {
            return getUses().get(2);
        }
        else {
            return null;
        }
    }

    public Value getPointIndex() {
        return getUses().get(1);
    }

//    @Override
//    public void update() {
//        base = getUses().get(0);
//        pointIndex = getUses().get(1);
//        if (getUses().size() == 3)
//            arrayIndex = getUses().get(2);
//    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName() + " = getelementptr " + baseType + ", ");
        sb.append(baseType + "* " + getBase().getName() + ", " + getPointIndex().getType() + " " + getPointIndex().getName());
        if (getValueNum() == 3) {
            sb.append(", " + getArrayIndex().getType() + " " + getArrayIndex().getName());
        }
        return sb.toString();
    }
}
