package Frontend.Parscial.ParseNodes;

import Frontend.Lexical.Word;

public class UnaryOp extends Node{
    private Word word;

    public UnaryOp(Word word) {
        this.word = word;
    }

    public Word getWord() {
        return word;
    }
}
