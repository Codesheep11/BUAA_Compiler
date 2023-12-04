package Frontend.Lexical;

import Frontend.MyError;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Lexer {
    private String input;
    private String token;
    private Word.Identity type;
    private int pos = 0;
    private boolean end = false;
    private boolean note = false;
    private ArrayList<Word> tokenArray;

    private int line = 1;

    private boolean islegalChar = true;

    public Lexer() {
        token = new String();
        input = new String();
        type = null;
        tokenArray = new ArrayList<>();
    }

    public ArrayList<Word> getTokenArray() {
        return tokenArray;
    }

    public ArrayList<Word> lex(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String inputLine;
            line = 1;
            while ((inputLine = reader.readLine()) != null) {
                input = inputLine;
                pos = 0;
                end = false;
                next();
                while (!end) {
                    if (type != null)
                        tokenArray.add(new Word(token, type, line));
                    next();
                }
                line++;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tokenArray;
    }

    private void next() {
        if (note) {
            if (noting())
                return;
        }
        if (pos == input.length()) {
            end = true;
            return;
        }
        getToken();
        type = null;
        switch (token) {
            case "main": {
                type = Word.Identity.MAINTK;
                return;
            }
            case "const": {
                type = Word.Identity.CONSTTK;
                return;
            }
            case "int": {
                type = Word.Identity.INTTK;
                return;
            }
            case "break": {
                type = Word.Identity.BREAKTK;
                return;
            }
            case "continue": {
                type = Word.Identity.CONTINUETK;
                return;
            }
            case "if": {
                type = Word.Identity.IFTK;
                return;
            }
            case "else": {
                type = Word.Identity.ELSETK;
                return;
            }
            case "!": {
                type = Word.Identity.NOT;
                return;
            }
            case "&&": {
                type = Word.Identity.AND;
                return;
            }
            case "||": {
                type = Word.Identity.OR;
                return;
            }
            case "for": {
                type = Word.Identity.FORTK;
                return;
            }
            case "getint": {
                type = Word.Identity.GETINTTK;
                return;
            }
            case "printf": {
                type = Word.Identity.PRINTFTK;
                return;
            }
            case "return": {
                type = Word.Identity.RETURNTK;
                return;
            }
            case "+": {
                type = Word.Identity.PLUS;
                return;
            }
            case "-": {
                type = Word.Identity.MINU;
                return;
            }
            case "void": {
                type = Word.Identity.VOIDTK;
                return;
            }
            case "*": {
                type = Word.Identity.MULT;
                return;
            }
            case "/": {
                type = Word.Identity.DIV;
                return;
            }
            case "%": {
                type = Word.Identity.MOD;
                return;
            }
            case "<": {
                type = Word.Identity.LSS;
                return;
            }
            case "<=": {
                type = Word.Identity.LEQ;
                return;
            }
            case ">": {
                type = Word.Identity.GRE;
                return;
            }
            case ">=": {
                type = Word.Identity.GEQ;
                return;
            }
            case "==": {
                type = Word.Identity.EQL;
                return;
            }
            case "!=": {
                type = Word.Identity.NEQ;
                return;
            }
            case "=": {
                type = Word.Identity.ASSIGN;
                return;
            }
            case ";": {
                type = Word.Identity.SEMICN;
                return;
            }
            case ",": {
                type = Word.Identity.COMMA;
                return;
            }
            case "(": {
                type = Word.Identity.LPARENT;
                return;
            }
            case ")": {
                type = Word.Identity.RPARENT;
                return;
            }
            case "[": {
                type = Word.Identity.LBRACK;
                return;
            }
            case "]": {
                type = Word.Identity.RBRACK;
                return;
            }
            case "{": {
                type = Word.Identity.LBRACE;
                return;
            }
            case "}": {
                type = Word.Identity.RBRACE;
                return;
            }
            case "//": {
                end = true;
                return;
            }
            case "/*": {
                note = true;
                noting();
                token = "";
                return;
            }
            case "*/": {
                note = false;
                token = "";
                return;
            }
            case "": {
                end = true;
                token = "";
                return;
            }
        }
        char c = token.charAt(0);
        if (c == '"') {
            checkErrorA();
            type = Word.Identity.STRCON;
        }
        else if (Character.isDigit(c)) {
            type = Word.Identity.INTCON;
        }
        else if (c == '_' || Character.isLetter(c)) {
            type = Word.Identity.IDENFR;
        }
        assert type != null;
    }

    private void getToken() {
        token = new String();
        Character c = getChar();
        while (c == null || c == '\n' || c == ' ' || c == '\t') {
            if (c == null) {
                end = true;
                return;
            }
            c = getChar();
        }
        if (c == '}' || c == '{' || c == '[' || c == ']' || c == '(' || c == ')' || c == ',' || c == ';' || c == '%' || c == '+' || c == '-') {
            token += c;
        }
        else if (c == '!' || c == '>' || c == '<' || c == '=') {
            token += c;
            c = getChar();
            if (c == null) {
//                end = true;
                return;
            }
            if (c == '=') {
                token += c;
            }
            else {
                unGetChar();
            }
        }
        else if (c == '/') {
            token += c;
            c = getChar();
            if (c == null) {
                end = true;
                return;
            }
            if (c == '/' || c == '*') {
                token += c;
            }
            else {
                unGetChar();
            }
        }
        else if (c == '*') {
            token += c;
            c = getChar();
            if (c == null) {
                end = true;
                return;
            }
            if (c == '/') {
                token += c;
            }
            else {
                unGetChar();
            }
        }
        else if (c == '&') {
            token += c;
            c = getChar();
            if (c == null) {
                end = true;
                return;
            }
            if (c == '&') {
                token += c;
            }
            else {
                unGetChar();
            }
        }
        else if (c == '|') {
            token += c;
            c = getChar();
            if (c == null) {
                end = true;
                return;
            }
            if (c == '|') {
                token += c;
            }
            else {
                unGetChar();
            }
        }
        else if (c == '"') {
            islegalChar = true;
            token += c;
            c = getChar();
            while (c != '"') {
                if (islegal(c)) {
                    token += c;
                }
                c = getChar();
            }
            token += c;
        }
        else if (Character.isDigit(c)) {
            token += c;
            c = getChar();
            if (c == null) {
                return;
            }
            while (Character.isDigit(c)) {
                token += c;
                c = getChar();
                if (c == null) {
                    return;
                }
            }
            unGetChar();
        }
        else if (c == '_' || Character.isLetter(c)) {
            token += c;
            c = getChar();
            while (c == '_' || Character.isLetter(c) || Character.isDigit(c)) {
                token += c;
                c = getChar();
                if (c == null) {
//                    end = true;
                    return;
                }
            }
            unGetChar();
        }
    }

    private boolean noting() {
        while (true) {
            Character ch1 = getChar();
            Character ch2 = getChar();
            if (ch1 == null || ch2 == null) {
                end = true;
                return end;
            }
            if (ch1 == '*' && ch2 == '/') {
                note = false;
                Character ch3 = getChar();
                if (ch3 == null) {
                    end = true;
                    return false;
                }
                else {
                    unGetChar();
                    return false;
                }
            }
            unGetChar();
        }
    }

    private Character getChar() {
        if (pos < input.length()) {
            pos++;
            return input.charAt(pos - 1);
        }
        return null;
    }

    private void unGetChar() {
        pos--;
    }

    private void checkErrorA() {
        try {
            if (!islegalChar) {
                islegalChar = true;
                throw new MyError(line, MyError.ErrorType.A);
            }
        } catch (MyError e) {
            e.gather();
        }
    }

    private boolean islegal(Character c) {
        if (c.charValue() == 32 || c.charValue() == 33
                || (c.charValue() >= 40 && c.charValue() <= 126))
        {
            if (c.charValue() == 92) {
                Character ch = getChar();
                unGetChar();
                if (ch != 'n') {
                    islegalChar = false;
                    return false;
                }
            }
            return true;
        }
        if (c == '%') {
            Character ch = getChar();
            unGetChar();
            if (ch != 'd') {
                islegalChar = false;
                return false;
            }
            return true;
        }
        islegalChar = false;
        return false;

    }
}
