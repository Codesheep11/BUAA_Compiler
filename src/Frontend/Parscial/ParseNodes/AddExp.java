package Frontend.Parscial.ParseNodes;

import Frontend.Lexical.Word;
import Frontend.Parscial.Symbol;

import java.util.ArrayList;

public class AddExp extends Node {
    private ArrayList<MulExp> mulExps;
    private ArrayList<Word> words;


    public AddExp(ArrayList<MulExp> mulExps, ArrayList<Word> words) {
        this.mulExps = mulExps;
        this.words = words;
    }

    public Symbol.SymbolType getDataType() {
        return mulExps.get(0).getDataType();
    }

    public int getVal() {
        int val = mulExps.get(0).getVal();
        for (int i = 1; i < mulExps.size(); i++) {
            if (words.get(i - 1).isPLUS()) {
                val += mulExps.get(i).getVal();
            } else {
                val -= mulExps.get(i).getVal();
            }
        }
        return val;
    }

    public ArrayList<Word> getWords() {
        return words;
    }

    public ArrayList<MulExp> getMulExps() {
        return mulExps;
    }

}
