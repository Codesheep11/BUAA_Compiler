package midend.pass;

import midend.ir.Module;
import midend.ir.types.VoidType;
import midend.ir.values.*;
import midend.ir.values.Function;
import midend.ir.values.instructions.Instruction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Pass {
    private Module module;

    public static boolean pass = true;

    public Pass(Module module) {
        this.module = module;
        DomTree domTree = new DomTree(module);
        domTree.run();
        if (pass) {
            Mem2Reg mem2Reg = new Mem2Reg(module);
            mem2Reg.run();
            for (Function func : module.getFunctions()) {
                ConstantTransform ct = new ConstantTransform(func);
                DeadCodeDelete dcd1 = new DeadCodeDelete(func);
            }
        }
        System.out.println(module);
    }


}
