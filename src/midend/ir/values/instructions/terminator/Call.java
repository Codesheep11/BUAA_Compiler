package midend.ir.values.instructions.terminator;

import midend.ir.types.FuncType;
import midend.ir.types.VoidType;
import midend.ir.values.BasicBlock;
import midend.ir.values.Function;
import midend.ir.values.instructions.Instruction;
import midend.ir.values.Value;

import java.util.ArrayList;
import java.util.HashSet;

public class Call extends Instruction {
    private boolean isVoid;
    private int argCnt;

//    private Function function;
//    private ArrayList<Value> args;

    public Call(BasicBlock parent, Function function, ArrayList<Value> args) {
        super("", new VoidType(), parent, function);
        isVoid = true;
//        this.function = function;
//        this.args = args;
        for (Value v : args) {
            this.addUseValue(v);
        }
        argCnt = args.size();
        defValue.add(this);
//        useValue.addAll(args);
    }

    public Call(String name, BasicBlock parent, Function function, ArrayList<Value> args) {
        super(name, ((FuncType) function.getType()).getRetType(), parent, function);
        isVoid = false;
//        this.function = function;
//        this.args = args;
        for (Value v : args) {
            this.addUseValue(v);
        }
        argCnt = args.size();
        defValue.add(this);
//        useValue.addAll(args);
    }

//    @Override
//    public void update() {
//        ArrayList<Value> newArgs = new ArrayList<>();
//        for (int i = 0; i < args.size(); i++) {
//            newArgs.add(getUses().get(i + 1));
//        }
//        args = newArgs;
//        useValue = new HashSet<>();
//        useValue.addAll(args);
//    }

    public Function getFunction() {
        return (Function) getUses().get(0);
    }

    public ArrayList<Value> getArgs() {
        ArrayList<Value> args = new ArrayList<>();
        for (int i = 0; i < argCnt; i++) {
            args.add(getUses().get(i + 1));
        }
        return args;
    }

    public boolean isVoid() {
        return isVoid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!isVoid) {
            sb.append(getName() + " = ");
        }
        sb.append("call " + getFunction().getType() + " @" + getFunction().getName() + "(");
        for (int i = 0; i < getArgs().size() - 1; i++) {
            Value v = getArgs().get(i);
            sb.append(v.getType() + " " + v.getName() + ", ");
        }
        if (!getArgs().isEmpty()) {
            Value v = getArgs().get(getArgs().size() - 1);
            sb.append(v.getType() + " " + v.getName());
        }
        sb.append(")");
        return sb.toString();
    }
}
