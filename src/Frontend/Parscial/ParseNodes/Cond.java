package Frontend.Parscial.ParseNodes;

public class Cond extends Node{
    private LOrExp lOrExp;

    public Cond(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }

    public LOrExp getlOrExp() {
        return lOrExp;
    }
}
