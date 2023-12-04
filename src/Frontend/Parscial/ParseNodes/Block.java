package Frontend.Parscial.ParseNodes;

import java.util.ArrayList;

public class Block extends Node{
    private ArrayList<BlockItem> blockItems;

    public Block(ArrayList<BlockItem> blockItems) {
        this.blockItems = blockItems;
    }

    public ArrayList<BlockItem> getBlockItems() {
        return blockItems;
    }
}
