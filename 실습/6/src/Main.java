import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.ParseTree;
import generated.*;

public class Main {
    public static void main(String[] args) throws Exception {
        CharStream code = CharStreams.fromFileName("./src/test.tr");
        tinyR4Lexer lexer = new tinyR4Lexer(code);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        try {
            tinyR4Parser parser = new tinyR4Parser(tokens);
            ParseTree tree = parser.program();

            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(new tinyR4UglyPrintListener(), tree);

            System.out.println(tinyR4UglyPrintListener.getOutput());
        } catch (RuntimeException e) {
            System.out.println("Error");
        }
    }
}