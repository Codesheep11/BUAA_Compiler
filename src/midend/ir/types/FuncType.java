package midend.ir.types;

import java.util.ArrayList;

public class FuncType extends Type {
    private ArrayList<Type> args;
    private Type retType;

    private boolean isDefine;

    public FuncType(ArrayList<Type> args, Type retType, boolean isDefine) {
        this.args = args;
        this.retType = retType;
        this.isDefine = isDefine;
    }

    public ArrayList<Type> getArgs() {
        return args;
    }

    public Type getRetType() {
        return retType;
    }

    public boolean isDefine() {
        return isDefine;
    }

    public boolean isRet() {
        return !(retType instanceof VoidType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(retType);
        return sb.toString();
    }

    @Override
    public int getSize() {
        return 0;
    }
}
