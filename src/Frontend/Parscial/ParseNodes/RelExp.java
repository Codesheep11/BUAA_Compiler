package Frontend.Parscial.ParseNodes;

import Frontend.Lexical.Word;

import java.util.ArrayList;

public class RelExp extends Node{
    private ArrayList<AddExp> addExps;
    private ArrayList<Word> words;

    public RelExp(ArrayList<AddExp> addExps, ArrayList<Word> words) {
        this.addExps = addExps;
        this.words = words;
    }

    public ArrayList<Word> getWords() {
        return words;
    }

    public ArrayList<AddExp> getAddExps() {
        return addExps;
    }
}
