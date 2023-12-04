package Frontend;

import java.util.ArrayList;

public class MyError extends Exception {
    public static ArrayList<MyError> errors = new ArrayList<>();

    public int line;

    private ErrorType et;

    public MyError(int line, ErrorType et) {
        this.line = line;
        this.et = et;
    }

    public enum ErrorType {
        A, B, C, D, E, F, G, H, I, J, K, L, M;
    }

    public void print() {
        if (et == ErrorType.A) {
            System.err.println(line + " a");
//        System.err.println(line+":illegal character");
        } else if (et == ErrorType.B) {
            System.err.println(line + " b");
//        System.err.println(line+":redefinition");
        } else if (et == ErrorType.C) {
            System.err.println(line + " c");
//        System.err.println(line+":undefined ident");
        } else if (et == ErrorType.D) {
            System.err.println(line + " d");
//        System.err.println(line+":The number of function parameters does not match");
        } else if (et == ErrorType.E) {
            System.err.println(line + " e");
//        System.err.println(line+":Function parameter types do not match");
        } else if (et == ErrorType.F) {
            System.err.println(line + " f");
//        System.err.println(line+":Functions with no return value have mismatched return statements");
        } else if (et == ErrorType.G) {
            System.err.println(line + " g");
//        System.err.println(line+":Functions with return values are missing the return statement");
        } else if (et == ErrorType.H) {
            System.err.println(line + " h");
//        System.err.println(line+":You cannot change the value of a constant");
        } else if (et == ErrorType.I) {
            System.err.println(line + " i");
//        System.err.println(line+":Missing semicolon");
        } else if (et == ErrorType.J) {
            System.err.println(line + " j");
//        System.err.println(line+":Missing rparent");
        } else if (et == ErrorType.K) {
            System.err.println(line + " k");
//        System.err.println(line+":Missing rbrack");
        } else if (et == ErrorType.L) {
            System.err.println(line + " l");
//        System.err.println(line+":param cnt not match in printf");
        } else if (et == ErrorType.M) {
            System.err.println(line + " m");
//        System.err.println(line+":param cnt not match in printf");
        }
    }

    public void gather() {
        errors.add(this);
    }
}
