package midend.ir.values.constants;

import midend.ir.types.*;
import midend.ir.values.Value;

public class GlobalValue extends Constant {
    private boolean isConst;

    private Value initVal;

    private boolean isString = false;

    private String stringValue = null;

    private static int StringId = 0;

    public GlobalValue(String name, Type type, boolean isConst, Value initVal) {
        super("@" + name, new PointerType(type), null, initVal);
        this.isConst = isConst;
        if (initVal == null) {
            this.initVal = Constant.getZeroConstant(type);
        }
        else {
            this.initVal = initVal;
        }
    }

    public GlobalValue(String str) {
        super("str_" + StringId++, new PointerType(new ArrayType(str.length() + 1, new IntType(8))), null);
        this.isString = true;
        this.isConst = true;
        this.stringValue = str;
    }

    public Value getInitVal() {
        return initVal;
    }

    public boolean isString() {
        return isString;
    }

    public String getStringValue() {
        return stringValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName() + " = dso_local ");
        if (isConst) {
            sb.append("constant ");
        }
        else {
            sb.append("global ");
        }
        sb.append(initVal);
        return sb.toString();
    }
}
