package Frontend.Parscial.ParseNodes.Stmts;

import Frontend.Parscial.ParseNodes.Block;

public class StmtBlock extends Stmt{
    private Block block;

    public StmtBlock(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
