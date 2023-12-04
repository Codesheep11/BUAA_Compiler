package midend.ir.values.constants;

import midend.ir.types.ArrayType;
import midend.ir.types.Type;
import midend.ir.values.Value;

import java.util.ArrayList;

public class ValueArray extends Constant {

    private ArrayList<Value> elements = new ArrayList<>();

    private boolean isZeroinitializer;

    public ValueArray(ArrayType arrayType) {
        super(null, arrayType, null);
        Type eType = arrayType.getElemType();
        isZeroinitializer = true;
        for (int i = 0; i < arrayType.getElemNum(); i++) {
            Constant zero = getZeroConstant(eType);
            addUseValue(zero);
            elements.add(zero);
        }
    }

    public ValueArray(ArrayList<Value> elements) {
        super(null, new ArrayType(elements.size(), elements.get(0).getType()), null);
        isZeroinitializer = false;
        this.elements = elements;
        for (Value v : elements) {
            addUseValue(v);
        }
    }


    public ArrayList<Integer> getVal() {
        ArrayList<Integer> val = new ArrayList<>();
        int cnt = ((ArrayType) getType()).getElemNum();
        for (int i = 0; i < cnt; i++) {
            if (getIndexValue(i) instanceof ValueArray) {
                val.addAll(((ValueArray) getIndexValue(i)).getVal());
            } else {
                val.add(((ConstantInt) getIndexValue(i)).getVal());
            }
        }
        return val;
    }

    public Value get(int index) {
        return elements.get(index);
    }

    public boolean isZeroinitializer() {
        return isZeroinitializer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getType().toString());
        if (isZeroinitializer) {
            sb.append(" zeroinitializer");
            return sb.toString();
        }
        sb.append(" [");
        for (int i = 0; i < elements.size(); i++) {
            sb.append(elements.get(i));
            if (i != elements.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
