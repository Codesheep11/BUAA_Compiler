package midend.ir.values.instructions.terminator;

import midend.ir.types.VoidType;
import midend.ir.values.BasicBlock;
import midend.ir.values.constants.ConstantInt;
import midend.ir.values.instructions.Instruction;
import midend.ir.values.Value;

import java.util.HashSet;

public class Br extends Instruction {

    private boolean isCondition;

//    private BasicBlock toBB = null;
//
//    private BasicBlock trueBB = null;
//    private Value cond = null;
//    private BasicBlock falseBB = null;

    // 无条件跳转
    public Br(BasicBlock parent, BasicBlock toBB) {
        super("", new VoidType(), parent, toBB);
        isCondition = false;
    }

    // 条件跳转
    public Br(BasicBlock parent, Value cond, BasicBlock trueBB, BasicBlock falseBB) {
        super("", new VoidType(), parent, cond, trueBB, falseBB);
        isCondition = true;
    }

    public boolean isCondition() {
        return isCondition;
    }

    public BasicBlock getToBB() {
        return (BasicBlock) getUses().get(0);
    }

    public BasicBlock getTrueBB() {
        return (BasicBlock) getUses().get(1);
    }

    public BasicBlock getFalseBB() {
        return (BasicBlock) getUses().get(2);
    }

    public Value getCond() {
        return getUses().get(0);
    }

    public void update() {
        if (getCond() instanceof ConstantInt) {
            if (((ConstantInt) getCond()).getVal() == 1) {
                isCondition = false;
                ((BasicBlock) getParent()).removeNxtBB(getFalseBB());
                getUses().remove(getFalseBB());
                getUses().remove(getCond());
            }
            else if (((ConstantInt) getCond()).getVal() == 0) {
                isCondition = false;
                ((BasicBlock) getParent()).removeNxtBB(getTrueBB());
                getUses().remove(getTrueBB());
                getUses().remove(getCond());
            }

        }
    }

    @Override
    public String toString() {
        if (this.isCondition) {
            return "br " + getCond().getType() + " " + getCond().getName() + ", "
                    + getTrueBB().getType() + " %" + getTrueBB().getName() + ", "
                    + getFalseBB().getType() + " %" + getFalseBB().getName();
        }
        else {
            return "br " + getToBB().getType() + " %" + getToBB().getName();
        }
    }
}

