package Frontend.Parscial.ParseNodes;

import Frontend.Lexical.Word;
import Frontend.Parscial.Symbol;

import java.util.ArrayList;

public class MulExp extends Node {
    private ArrayList<UnaryExp> unaryExps;
    private ArrayList<Word> words;


    public MulExp(ArrayList<UnaryExp> unaryExps, ArrayList<Word> words) {
        this.unaryExps = unaryExps;
        this.words = words;
    }


    public Symbol.SymbolType getDataType() {
        return unaryExps.get(0).getDataType();
    }

    public int getVal() {
        int val = unaryExps.get(0).getVal();
        for (int i = 1; i < unaryExps.size(); i++) {
            if (words.get(i - 1).isMULT()) {
                val *= unaryExps.get(i).getVal();
            } else if (words.get(i - 1).isDIV()) {
                val /= unaryExps.get(i).getVal();
            } else {
                val %= unaryExps.get(i).getVal();
            }
        }
        return val;
    }

    public ArrayList<UnaryExp> getUnaryExps() {
        return unaryExps;
    }

    public ArrayList<Word> getWords() {
        return words;
    }
}
