import Frontend.MyError;
import Frontend.Lexical.Lexer;
import Frontend.Parscial.ParseNodes.CompUnit;
import Frontend.Parscial.Parser;
import Frontend.Visitor;
import backend.Allocate;
import backend.Codegen;
import backend.Component.Program;
import midend.ir.Module;
import midend.pass.Pass;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;

import static Frontend.MyError.errors;

public class Compiler {

    public static void main(String[] args) {
        //frontend
        try {
            changeSystemOutToFile("output.txt");
            changeSystemErrToFile("error.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }


        Lexer lexer = new Lexer();
        lexer.lex("testfile.txt");
        Parser parser = new Parser(lexer.getTokenArray());
        CompUnit ast = parser.parse();

        //error
        ErrorPrint();

        //midend
        try {
            changeSystemOutToFile("llvm_ir.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Visitor visitor = new Visitor(ast);
        Module module = visitor.visit();
        //pass
        Pass pass = new Pass(module);

        //backend

        Codegen codegen = new Codegen(module);
        Program program = codegen.genCode();

//        try {
//            changeSystemOutToFile("mips_vr.txt");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(program);


        Allocate allocate = new Allocate(program);
        allocate.allocate();
        try {
            changeSystemOutToFile("mips.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(program);
    }

    public static void ErrorPrint() {
        Collections.sort(errors, new Comparator<MyError>() {
            @Override
            public int compare(MyError error1, MyError error2) {
                return Integer.compare(error1.line, error2.line);
            }
        });

        for (MyError e : errors) {
            e.print();
        }
    }

    public static void changeSystemOutToFile(String outputPath) throws IOException {
        PrintStream out = new PrintStream(outputPath);
        System.setOut(out);
    }

    public static void changeSystemErrToFile(String outputPath) throws IOException {
        PrintStream err = new PrintStream(outputPath);
        System.setErr(err);
    }
}
