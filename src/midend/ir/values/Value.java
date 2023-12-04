package midend.ir.values;

import midend.ir.types.Type;
import midend.ir.values.constants.Constant;

import java.util.ArrayList;

public class Value {

    private String name;
    private Type type;

    private Value parent;

    private ArrayList<User> users = new ArrayList<>();

    public Value(String name, Type type, Value parent) {
        this.name = name;
        this.type = type;
        this.parent = parent;
        users = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public void addUser(User u) {
        users.add(u);
    }

    public void deleteUser(User u) {
        users.remove(u);
    }

    public Value getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return getType() + " " + getName();
    }


    public ArrayList<User> getUsers() {
        return users;
    }
}
