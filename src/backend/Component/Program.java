package backend.Component;

import java.util.ArrayList;

public class Program {
    private ArrayList<MipsFunction> functions = new ArrayList<>();
    private ArrayList<MipsGlobalValue> globalValues = new ArrayList<>();
    private MipsFunction mainFunc;

    public Program() {

    }

    public void addFunction(MipsFunction function) {
        functions.add(function);
    }

    public void addGlobalValue(MipsGlobalValue gv) {
        globalValues.add(gv);
    }

    public void setMainFunc(MipsFunction mainFunc) {
        this.mainFunc = mainFunc;
    }

    public MipsFunction getMainFunc() {
        return mainFunc;
    }

    public ArrayList<MipsFunction> getFunctions() {
        return functions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(".data\n");
        for (MipsGlobalValue gv : globalValues) {
            sb.append(gv);
        }
        sb.append("\n.text\n");
        sb.append("\tli $fp, 268697600\n" + "\tmove $sp, $fp\n");
        for (MipsFunction function : functions) {
            if (function.isMain()) {
                sb.append(function + "\n");
                break;
            }
        }
        for (MipsFunction function : functions) {
            if (function.isMain()) {
                continue;
            }
            sb.append(function + "\n");
        }
        return sb.toString();
    }
}
