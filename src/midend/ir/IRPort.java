package midend.ir;

import midend.ir.types.*;
import midend.ir.values.Argument;
import midend.ir.values.BasicBlock;
import midend.ir.values.Function;
import midend.ir.values.Value;
import midend.ir.values.constants.ConstantInt;
import midend.ir.values.instructions.binary.*;
import midend.ir.values.instructions.memory.*;
import midend.ir.values.instructions.terminator.*;

import java.awt.*;
import java.util.ArrayList;

public class IRPort {
    private static int nameCounter = 0;
    private static int blockCounter = 0;


    public static Function newFunc(FuncType ft, String name) {
        nameCounter = 0;
        blockCounter = 0;
        return new Function(ft, name);
    }


    public static BasicBlock newBB(Function parent) {
        int cnt = blockCounter;
        blockCounter++;
        BasicBlock block = new BasicBlock(parent.getName() + "_BB_" + cnt, parent);
        parent.addBB(block);
        return block;
    }

    public static void deleteBB(BasicBlock bb, Function function) {
        function.deleteBB(bb);
    }


    public static Argument newArg(Function parent, Type type) {
        int cnt = nameCounter;
        nameCounter++;
        Argument argument = new Argument("%_" + cnt, type, parent);
        parent.addArg(argument);
        return argument;
    }
    //new instructions

    //binary
    public static Value newAdd(BasicBlock parent, Value op1, Value op2) {
        if (op1 instanceof ConstantInt && op2 instanceof ConstantInt) {
            return new ConstantInt(32, ((ConstantInt) op1).getVal() + ((ConstantInt) op2).getVal());
        }
        int cnt = nameCounter;
        nameCounter++;
        Add add = new Add("%_" + cnt, parent, op1, op2);
        parent.addInst(add);
        return add;
    }

    public static Value newSub(BasicBlock parent, Value op1, Value op2) {
        if (op1 instanceof ConstantInt && op2 instanceof ConstantInt) {
            return new ConstantInt(32, ((ConstantInt) op1).getVal() - ((ConstantInt) op2).getVal());
        }
        int cnt = nameCounter;
        nameCounter++;
        Sub sub = new Sub("%_" + cnt, parent, op1, op2);
        parent.addInst(sub);
        return sub;
    }

    public static Value newMul(BasicBlock parent, Value op1, Value op2) {
        if (op1 instanceof ConstantInt && op2 instanceof ConstantInt) {
            return new ConstantInt(32, ((ConstantInt) op1).getVal() * ((ConstantInt) op2).getVal());
        }
        int cnt = nameCounter;
        nameCounter++;
        Mul mul = new Mul("%_" + cnt, parent, op1, op2);
        parent.addInst(mul);
        return mul;
    }

    public static Value newSdiv(BasicBlock parent, Value op1, Value op2) {
        if (op1 instanceof ConstantInt && op2 instanceof ConstantInt) {
            return new ConstantInt(32, ((ConstantInt) op1).getVal() / ((ConstantInt) op2).getVal());
        }
        int cnt = nameCounter;
        nameCounter++;
        Sdiv sdiv = new Sdiv("%_" + cnt, parent, op1, op2);
        parent.addInst(sdiv);
        return sdiv;
    }

    public static Value newSrem(BasicBlock parent, Value op1, Value op2) {
        if (op1 instanceof ConstantInt && op2 instanceof ConstantInt) {
            return new ConstantInt(32, ((ConstantInt) op1).getVal() % ((ConstantInt) op2).getVal());
        }
        int cnt = nameCounter;
        nameCounter++;
        Srem srem = new Srem("%_" + cnt, parent, op1, op2);
        parent.addInst(srem);
        return srem;
    }

    public static And newAnd(BasicBlock parent, Value op1, Value op2) {
        int cnt = nameCounter;
        nameCounter++;
        And and = new And("%_" + cnt, parent, op1, op2);
        parent.addInst(and);
        return and;
    }

    public static Or newOr(BasicBlock parent, Value op1, Value op2) {
        int cnt = nameCounter;
        nameCounter++;
        Or or = new Or("%_" + cnt, parent, op1, op2);
        parent.addInst(or);
        return or;
    }

    public static Value newIcmp(BasicBlock parent, Value op1, Value op2, Icmp.IcmpType icmpType) {
        if (op1 instanceof ConstantInt && op2 instanceof ConstantInt) {
            int val;
            switch (icmpType) {
                case EQ -> {
                    val = ((ConstantInt) op1).getVal() == ((ConstantInt) op2).getVal() ? 1 : 0;
                    break;
                }
                case NE -> {
                    val = ((ConstantInt) op1).getVal() != ((ConstantInt) op2).getVal() ? 1 : 0;
                    break;
                }
                case SLE -> {
                    val = ((ConstantInt) op1).getVal() <= ((ConstantInt) op2).getVal() ? 1 : 0;
                    break;
                }
                case SLT -> {
                    val = ((ConstantInt) op1).getVal() < ((ConstantInt) op2).getVal() ? 1 : 0;
                    break;
                }
                case SGT -> {
                    val = ((ConstantInt) op1).getVal() > ((ConstantInt) op2).getVal() ? 1 : 0;
                    break;
                }
                case SGE -> {
                    val = ((ConstantInt) op1).getVal() >= ((ConstantInt) op2).getVal() ? 1 : 0;
                    break;
                }
                default -> {
                    val = 0;
                }
            }
            return new ConstantInt(1, val);
        }
        int cnt = nameCounter;
        nameCounter++;
        Icmp icmp = new Icmp("%_" + cnt, parent, op1, op2, icmpType);
        parent.addInst(icmp);
        return icmp;
    }

    //memory
    public static Alloca newAlloca(Type allocatedType, BasicBlock parent) {
        int cnt = nameCounter;
        nameCounter++;
        Alloca alloca = new Alloca("%_" + cnt, allocatedType, parent);
        parent.addInst(alloca);
        return alloca;
    }

    public static Load newLoad(Type type, BasicBlock parent, Value pointer) {
        int cnt = nameCounter;
        nameCounter++;
        Load load = new Load("%_" + cnt, type, parent, pointer);
        parent.addInst(load);
        return load;
    }

    public static Store newStore(BasicBlock parent, Value storeVal, Value pointer) {
        Store store = new Store(parent, storeVal, pointer);
        parent.addInst(store);
        return store;
    }

    public static Value newZextTo(BasicBlock parent, Value value, Type toType) {
        if (value instanceof ConstantInt) {
            int bit = ((IntType) toType).getBits();
            return new ConstantInt(bit, ((ConstantInt) value).getVal());
        }
        int cnt = nameCounter;
        nameCounter++;
        ZextTo zextTo = new ZextTo("%_" + cnt, parent, value, toType);
        parent.addInst(zextTo);
        return zextTo;
    }

    //terminator
    public static Br newBrWithNoCond(BasicBlock parent, BasicBlock toBB) {
        Br br = new Br(parent, toBB);
        if (parent.addInst(br)) {
            parent.addNxtBB(toBB);
            toBB.addPreBB(parent);
        }
        return br;
    }

    public static Br newBrWithCond(BasicBlock parent, Value cond, BasicBlock trueBB, BasicBlock falseBB) {
        Br br;
        if (cond instanceof ConstantInt) {
            if (((ConstantInt) cond).getVal() == 0) {
                br = new Br(parent, falseBB);
                if (parent.addInst(br)) {
                    parent.addNxtBB(falseBB);
                    falseBB.addPreBB(parent);
                }
            }
            else {
                br = new Br(parent, trueBB);
                if (parent.addInst(br)) {
                    parent.addNxtBB(trueBB);
                    trueBB.addPreBB(parent);
                }
            }
        }
        else {
            br = new Br(parent, cond, trueBB, falseBB);
            if (parent.addInst(br)) {
                parent.addNxtBB(falseBB);
                falseBB.addPreBB(parent);
                parent.addNxtBB(trueBB);
                trueBB.addPreBB(parent);
            }
        }

        return br;
    }

    public static Call newUnVoidCall(BasicBlock parent, Function function, ArrayList<Value> args) {
        int cnt = nameCounter;
        nameCounter++;
        Call call = new Call("%_" + cnt, parent, function, args);
        parent.addInst(call);
        return call;
    }

    public static Gep newGep(BasicBlock parent, ArrayType baseType, Value base, Value pointIndex, Value arrayIndex) {
        int cnt = nameCounter;
        nameCounter++;
        Gep gep = new Gep("%_" + cnt, baseType, parent, base, pointIndex, arrayIndex);
        parent.addInst(gep);
        return gep;
    }

    public static Gep newGep(BasicBlock parent, PointerType baseType, Value base, Value pointIndex) {
        int cnt = nameCounter;
        nameCounter++;
        Gep gep = new Gep("%_" + cnt, baseType, parent, base, pointIndex);
        parent.addInst(gep);
        return gep;
    }

    public static Gep newGep(BasicBlock parent, PointerType baseType, Value base, Value pointIndex, Value arrayIndex) {
        int cnt = nameCounter;
        nameCounter++;
        Gep gep = new Gep("%_" + cnt, baseType, parent, base, pointIndex, arrayIndex);
        parent.addInst(gep);
        return gep;
    }

    public static Call newVoidCall(BasicBlock parent, Function function, ArrayList<Value> args) {
        Call call = new Call(parent, function, args);
        parent.addInst(call);
        return call;
    }

    public static Ret newUnVoidRet(BasicBlock parent, Value returnVal) {
        Ret ret = new Ret(parent, returnVal);
        parent.addInst(ret);
        return ret;
    }

    public static Ret newVoidRet(BasicBlock parent) {
        Ret ret = new Ret(parent);
        parent.addInst(ret);
        return ret;
    }
}
