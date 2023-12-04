package Frontend.Parscial.ParseNodes;

import Frontend.Lexical.Word;

import java.util.ArrayList;

public class LAndExp extends Node{
    private ArrayList<EqExp> eqExps;
    private ArrayList<Word> words;

    public LAndExp(ArrayList<EqExp> eqExps, ArrayList<Word> words) {
        this.eqExps = eqExps;
        this.words = words;
    }

    public ArrayList<EqExp> getEqExps() {
        return eqExps;
    }
}
