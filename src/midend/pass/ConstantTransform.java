package midend.pass;

import midend.ir.values.BasicBlock;
import midend.ir.values.Function;
import midend.ir.values.Value;
import midend.ir.values.constants.ConstantInt;
import midend.ir.values.instructions.Instruction;
import midend.ir.values.instructions.binary.Binary;
import midend.ir.values.instructions.terminator.Br;

import java.util.HashMap;
import java.util.HashSet;

public class ConstantTransform {

    private Function func;
    private HashSet<Instruction> deletes = new HashSet<>();

    public ConstantTransform(Function func) {
        this.func = func;
        run();
    }

    public void run() {
        for (BasicBlock bb : func.genBFSArray()) {
            for (Instruction ins : bb.getInsts()) {
                if (ins instanceof Binary) {
                    Value op1 = ((Binary) ins).getOp1();
                    Value op2 = ((Binary) ins).getOp2();
                    if (op1 instanceof ConstantInt && op2 instanceof ConstantInt) {
                        ConstantInt c = ((Binary) ins).genConst();
                        ins.replaceUseOf(c);
                        deletes.add(ins);
                    }
                }
                else if (ins.isBr()) {
                    if (((Br) ins).getCond() instanceof ConstantInt) {
                        ((Br) ins).update();
                    }
                }
            }
        }
        for (Instruction ins : deletes) {
            ins.remove();
        }
    }
}
