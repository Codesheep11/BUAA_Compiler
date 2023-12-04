package midend.ir.values;

import midend.ir.types.Type;

public class Argument extends Value {

    public Argument(String name, Type type, Function function) {
        super(name, type, function);
    }

    @Override
    public String toString() {
        return getType().toString() + " " + getName();
    }
}
