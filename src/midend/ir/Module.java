package midend.ir;

import midend.ir.types.FuncType;
import midend.ir.values.Function;
import midend.ir.values.constants.GlobalValue;

import java.util.ArrayList;

public class Module {
    private ArrayList<Function> functions;
    private ArrayList<Function> DeclFuncs;
    private ArrayList<GlobalValue> globalValues;

    public Module() {
        functions = new ArrayList<>();
        DeclFuncs = new ArrayList<>();
        globalValues = new ArrayList<>();
    }

    public Function getFunc(String fname) {
        for (Function f : functions) {
            if (f.getName().equals(fname)) {
                return f;
            }
        }
        for (Function f : DeclFuncs) {
            if (f.getName().equals(fname)) {
                return f;
            }
        }
        return null;
    }

    public void addFunction(Function function) {
        functions.add(function);
    }

    public void addDeclFunc(Function function) {
        DeclFuncs.add(function);
    }

    public void addGlobalVariable(GlobalValue globalValue) {
        globalValues.add(globalValue);
    }

    public ArrayList<GlobalValue> getGlobalValues() {
        return globalValues;
    }

    public ArrayList<Function> getDeclFuncs() {
        return DeclFuncs;
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Function f : DeclFuncs) {
            sb.append(f);
        }
        sb.append("\n");
        for (GlobalValue gv : globalValues) {
            if (!gv.isString())
                sb.append(gv + "\n");
        }
        sb.append("\n");
        for (Function f : functions) {
            sb.append(f);
        }
        return sb.toString();
    }
}
