package Frontend.Parscial.ParseNodes;

import Frontend.Lexical.Word;

import java.util.ArrayList;

public class LOrExp extends Node{
    private ArrayList<LAndExp> lAndExps;
    private ArrayList<Word> words;

    public LOrExp(ArrayList<LAndExp> lAndExps, ArrayList<Word> words) {
        this.lAndExps = lAndExps;
        this.words = words;
    }

    public ArrayList<Word> getWords() {
        return words;
    }

    public ArrayList<LAndExp> getlAndExps() {
        return lAndExps;
    }
}