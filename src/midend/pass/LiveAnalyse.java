package midend.pass;

import midend.ir.values.BasicBlock;
import midend.ir.values.Function;
import midend.ir.values.Value;
import midend.ir.values.constants.ConstantInt;
import midend.ir.values.instructions.Instruction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LiveAnalyse {
    private Function func;

    public LiveAnalyse(Function func) {
        this.func = func;
    }


}
