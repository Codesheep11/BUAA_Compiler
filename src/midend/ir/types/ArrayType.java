package midend.ir.types;

public class ArrayType extends Type {
    private int size;
    private Type elemType;

    public ArrayType(int size, Type elemType) {
        this.size = size;
        this.elemType = elemType;
    }

    public Type getElemType() {
        return elemType;
    }

    public int getElemNum() {
        return size;
    }

    @Override
    public int getSize() {
        return elemType.getSize() * size;
    }

    @Override
    public String toString() {
        return "[" + size + " x " + elemType + "]";
    }
}
