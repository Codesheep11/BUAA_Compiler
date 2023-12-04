package midend.ir.types;

public class VoidType extends Type {

    public VoidType() {
    }

    @Override
    public String toString() {
        return "void";
    }

    public int getSize() {
        return 0;
    }
}