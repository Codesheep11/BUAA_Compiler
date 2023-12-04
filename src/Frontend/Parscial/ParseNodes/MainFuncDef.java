package Frontend.Parscial.ParseNodes;

public class MainFuncDef extends Node{
    private Block block;

    public MainFuncDef(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
