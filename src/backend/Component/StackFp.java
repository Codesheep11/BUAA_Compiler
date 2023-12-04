package backend.Component;

import backend.Instructions.InstrJ;
import backend.Operand.VirReg;
import midend.ir.values.Value;

import java.util.ArrayList;
import java.util.HashMap;

public class StackFp {
    /*
    high

    args (allocate)

    $fp

    locals (codegen)

    spills (allocate)

    $sp

    low
     */
    private MipsFunction function;
    private HashMap<Value, Integer> allocPointers = new HashMap<>();
    private HashMap<VirReg, Integer> spillers = new HashMap<>();

    public ArrayList<InstrJ> calls = new ArrayList<>();
    public ArrayList<InstrJ> rets = new ArrayList<>();

    private int stack;
    private int paras;

    public StackFp(MipsFunction function) {
        this.function = function;
        stack = 0;
    }

    public void alloc(Value v, int size) {
        stack -= size;
        allocPointers.put(v, stack);
    }

    public int save(int size) {
        stack -= size;
        return stack;
    }

    public void para(int size) {
        if (size > paras) {
            paras = size;
        }
    }

    public void spill(VirReg v) {
        stack -= 4;
        spillers.put(v, stack);
    }

    private boolean algin = false;

    public void align() {
        algin = true;
        stack = stack - paras - 4;
        stack = (stack / 16 - (stack % 16 == 0 ? 0 : 1)) * 16;
    }

    public HashMap<Value, Integer> getAllocPointers() {
        return allocPointers;
    }

    public int getStack() {
        return stack;
    }

    public int getAlignStack() {
        if (!algin)
            align();
        return stack;
    }

    public HashMap<VirReg, Integer> getSpillers() {
        return spillers;
    }
}
