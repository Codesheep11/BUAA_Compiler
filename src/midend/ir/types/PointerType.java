package midend.ir.types;

public class PointerType extends Type {
    private Type pointtoType;

    public PointerType(Type type) {
        this.pointtoType = type;
    }

    public Type getPointtoType() {
        return pointtoType;
    }

    @Override
    public int getSize() {
        return 4;
    }

    @Override
    public String toString() {
        return pointtoType + "*";
    }
}
