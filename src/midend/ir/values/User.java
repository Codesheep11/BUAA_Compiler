package midend.ir.values;

import midend.ir.types.Type;

import java.util.ArrayList;

public class User extends Value {

    private ArrayList<Value> values = new ArrayList<>();

    public User(String name, Type type, Value parent, Value... values) {
        super(name, type, parent);
        for (Value v : values) {
            if (v != null) {
                this.values.add(v);
                v.addUser(this);
            }
        }
    }

    public ArrayList<Value> getUses() {
        return values;
    }

    public Value getIndexValue(int index) {
        return values.get(index);
    }


    public void addUseValue(Value v) {
        values.add(v);
        v.addUser(this);
    }

    public void updateUseValue(int index, Value v) {
        Value r = values.remove(index);
        r.deleteUser(this);
        values.add(index, v);
        v.addUser(this);
    }

    public int getValueNum() {
        return values.size();
    }
}
