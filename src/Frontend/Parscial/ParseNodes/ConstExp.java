package Frontend.Parscial.ParseNodes;

public class ConstExp extends Node {
    private AddExp addExp;

    private int val;

    public ConstExp(AddExp addExp) {
        this.addExp = addExp;
        val = addExp.getVal();
    }

    public AddExp getAddExp() {
        return addExp;
    }

    public int getVal() {
        return val;
    }
}
