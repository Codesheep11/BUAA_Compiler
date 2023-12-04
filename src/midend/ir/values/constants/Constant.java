package midend.ir.values.constants;

import midend.ir.types.ArrayType;
import midend.ir.types.IntType;
import midend.ir.types.Type;
import midend.ir.values.BasicBlock;
import midend.ir.values.User;
import midend.ir.values.Value;

public class Constant extends User {
    public Constant(String name, Type type, BasicBlock parent, Value... values) {
        super(name, type, parent, values);
    }


    public static Constant getZeroConstant(Type type) {
        if (type instanceof IntType) {
            return new ConstantInt(32, 0);
        } else {
            return new ValueArray((ArrayType) type);
        }
    }
}
