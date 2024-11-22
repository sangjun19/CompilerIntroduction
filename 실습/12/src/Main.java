import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.ParseTree;
import generated.*;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class Main {
    public static void main(String[] args) throws Exception {
        CharStream code = CharStreams.fromFileName("./src/test.tr");
        tinyR3Lexer lexer = new tinyR3Lexer(code);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        try {
            tinyR3Parser parser = new tinyR3Parser(tokens);
            ParseTree tree = parser.program();
            
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(new tinyR3PrintListener(), tree);

            BufferedWriter writer = new BufferedWriter(new FileWriter("./src/Test.j"));
            writer.write(tinyR3PrintListener.getOutput());
            System.out.println(tinyR3PrintListener.getOutput());
            writer.close();
        } catch (RuntimeException e) {
            System.out.println("Error");
        }
    }

}