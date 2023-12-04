package midend.ir.types;

public class IntType extends Type {
    private int bits;

    public IntType(int bits) {
        this.bits = bits;
    }

    public int getBits() {
        return bits;
    }

    @Override
    public String toString() {
        return "i" + bits;
    }

    @Override
    public int getSize() {
        return 4;
    }
}
