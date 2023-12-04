package Frontend.Parscial.ParseNodes;

import Frontend.Lexical.Word;

import java.util.ArrayList;

public class EqExp extends Node{

    private ArrayList<RelExp> relExps;
    private ArrayList<Word> words;

    public EqExp(ArrayList<RelExp> relExps, ArrayList<Word> words) {
        this.relExps = relExps;
        this.words = words;
    }

    public ArrayList<Word> getWords() {
        return words;
    }

    public ArrayList<RelExp> getRelExps() {
        return relExps;
    }
}
