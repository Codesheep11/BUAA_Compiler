package midend.ir.values.instructions;

import backend.Instructions.Instr;
import backend.Operand.VirReg;
import midend.ir.types.Type;
import midend.ir.values.BasicBlock;
import midend.ir.values.User;
import midend.ir.values.Value;
import midend.ir.values.instructions.binary.*;
import midend.ir.values.instructions.memory.*;
import midend.ir.values.instructions.terminator.*;

import java.util.HashSet;
import java.util.Set;


public class Instruction extends User {
    public Instruction(String name, Type type, BasicBlock parent, Value... values) {
        super(name, type, parent, values);
    }

    public Set<Value> inValue = new HashSet<>();
    public Set<Value> outValue = new HashSet<>();
    public Set<Value> defValue = new HashSet<>();

    public Set<Value> useValue = new HashSet<>();
    public HashSet<Instruction> nxtInstr = new HashSet<>();
    public HashSet<Instruction> preInstr = new HashSet<>();

    public void remove() {
        ((BasicBlock) getParent()).getInsts().remove(this);
        for (Instruction pre : preInstr) {
            pre.nxtInstr.addAll(nxtInstr);
        }
        for (Instruction nxt : nxtInstr) {
            nxt.preInstr.addAll(preInstr);
        }
    }

    public void replaceUseOf(Value v) {
        for (User user : getUsers()) {
            int index = user.getUses().indexOf(this);
            user.getUses().add(index, v);
            v.addUser(user);
            user.getUses().remove(this);
        }
//        useValue.remove(this);
//        useValue.add(v);
    }

    public boolean isUseFul() {
        return this instanceof Ret || this instanceof Call || this instanceof Br
                || this instanceof Alloca || this instanceof Store;
    }

    public boolean isPhi() {
        return this instanceof Phi;
    }

    public boolean isAdd() {
        return this instanceof Add;
    }

    public boolean isSub() {
        return this instanceof Sub;
    }

    public boolean isMul() {
        return this instanceof Mul;
    }

    public boolean isSdiv() {
        return this instanceof Sdiv;
    }

    public boolean isSrem() {
        return this instanceof Srem;
    }

    public boolean isAnd() {
        return this instanceof And;
    }

    public boolean isOr() {
        return this instanceof Or;
    }

    public boolean isIcmp() {
        return this instanceof Icmp;
    }

    public boolean isAlloca() {
        return this instanceof Alloca;
    }

    public boolean isGep() {
        return this instanceof Gep;
    }

    public boolean isLoad() {
        return this instanceof Load;
    }

    public boolean isStore() {
        return this instanceof Store;
    }

    public boolean isZextTo() {
        return this instanceof ZextTo;
    }

    public boolean isBr() {
        return this instanceof Br;
    }

    public boolean isCall() {
        return this instanceof Call;
    }

    public boolean isRet() {
        return this instanceof Ret;
    }
}

