package backend.Component;

import midend.ir.types.ArrayType;
import midend.ir.types.PointerType;
import midend.ir.values.constants.ConstantInt;
import midend.ir.values.constants.GlobalValue;
import midend.ir.values.constants.ValueArray;

import java.util.ArrayList;

public class MipsGlobalValue {
    private final GlobalValue gv;

    public MipsGlobalValue(GlobalValue gv) {
        this.gv = gv;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(gv.getName().substring(1) + ":\n");

        if (gv.isString()) {
            sb.append("\t.asciiz " + gv.getStringValue() + "\n");
        } else if (((PointerType) gv.getType()).getPointtoType() instanceof ArrayType) {
            if (((ValueArray) gv.getInitVal()).isZeroinitializer()) {
                sb.append("\t.space " + ((PointerType) gv.getType()).getPointtoType().getSize() + "\n");
            } else {
                for (int v : ((ValueArray) gv.getInitVal()).getVal()) {
                    sb.append("\t.word " + v + "\n");
                }
            }
        } else {
            sb.append("\t.word " + ((ConstantInt) gv.getInitVal()).getVal() + "\n");
        }
        return sb.toString();
    }
}
