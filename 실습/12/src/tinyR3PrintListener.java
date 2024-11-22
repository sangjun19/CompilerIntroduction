import generated.tinyR3BaseListener;
import generated.tinyR3Parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.*;

public class tinyR3PrintListener extends tinyR3BaseListener implements ParseTreeListener {
    private static String output;
    ParseTreeProperty<String> r3Tree = new ParseTreeProperty<>();
    private static List<String> values = new ArrayList<>();
    private static Map<String, Integer> indexMap = new HashMap<>();

    public static String getOutput() {
        return output;
    }

    @Override public void exitProgram(tinyR3Parser.ProgramContext ctx) {
        StringBuilder program = new StringBuilder();
        for (int i = 0; i < ctx.decl().size(); i++)
            program.append(r3Tree.get(ctx.decl(i)));
        output = program.toString();
    }

    @Override public void exitDecl(tinyR3Parser.DeclContext ctx) {
        String fun_decl = r3Tree.get(ctx.main_decl());
        r3Tree.put(ctx, fun_decl);
    }

    @Override public void exitMain_decl(tinyR3Parser.Main_declContext ctx) {
        String id = String.valueOf(ctx.getChild(1));
        String compound_stmt = r3Tree.get(ctx.compound_stmt());
        String prologue = getPrologue(id);
        String epilogue = getEpilogue();
        String main_decl = prologue + compound_stmt + epilogue;
        r3Tree.put(ctx, main_decl);
    }

    public String getPrologue(String id) {
        String result = "";
        result += ".class public Test\n";
        result += ".super java/lang/Object\n";
        result += "; standard initializer\n";
        result += ".method public <init>()V\n";
        result += "aload_0\n";
        result += "invokenonvirtual java/lang/Object/<init>()V\n";
        result += "return\n";
        result += ".end method\n\n\n";
        result += ".method public static " + id + "([Ljava/lang/String;)V\n";
        result += ".limit stack 32\n";
        result += ".limit locals 32\n";
        return result;
    }

    public static String getEpilogue() {
        return ".end method\n";
    }

    @Override public void exitCompound_stmt(tinyR3Parser.Compound_stmtContext ctx) {
        StringBuilder compound_stmt = new StringBuilder();
        for (int i = 0; i < ctx.local_decl().size(); i++)
            compound_stmt.append(r3Tree.get(ctx.local_decl(i)));
        for (int i = 0; i < ctx.stmt().size(); i++)
            compound_stmt.append(r3Tree.get(ctx.stmt(i)));
        r3Tree.put(ctx, compound_stmt.toString());
    }

    @Override public void exitLocal_decl(tinyR3Parser.Local_declContext ctx) {
        String dec_spec = r3Tree.get(ctx.dec_spec());
        String id = r3Tree.get(ctx.id());
        String val = r3Tree.get(ctx.val());
        String literal = r3Tree.get(ctx.val().literal());
        String result = "";
        //Todo
        if(literal != null)
            result += "bipush " + val + "\n";
        else
            result += "iload_" + val + "\n";
        result += "istore_" + id + "\n";
        r3Tree.put(ctx, result);
    }

    @Override public void exitVal(tinyR3Parser.ValContext ctx) {
        String result = r3Tree.get(ctx.literal());
        if(result == null) result = r3Tree.get(ctx.id());
        r3Tree.put(ctx, result);
    }

    @Override public void exitId(tinyR3Parser.IdContext ctx) {
        String id = String.valueOf(ctx.ID());
        String val = getVal(id) + "";
        r3Tree.put(ctx, val);
    }

    @Override public void exitLiteral(tinyR3Parser.LiteralContext ctx) {
        String literal = String.valueOf(ctx.LITERAL());
        r3Tree.put(ctx, literal);
    }

    @Override public void exitStmt(tinyR3Parser.StmtContext ctx) {
        String stmt = "";
        if(ctx.expr_stmt()!=null) stmt = r3Tree.get(ctx.expr_stmt());
        else if(ctx.print_stmt()!=null) stmt = r3Tree.get(ctx.print_stmt());
        else if(ctx.return_stmt()!=null) stmt = r3Tree.get(ctx.return_stmt());
        r3Tree.put(ctx, stmt);
    }

    @Override public void exitExpr_stmt(tinyR3Parser.Expr_stmtContext ctx) {
        String expr = r3Tree.get(ctx.expr());
        r3Tree.put(ctx, expr);
    }

    @Override public void exitExpr(tinyR3Parser.ExprContext ctx) {
        String result = "";
        if(ctx.getChildCount() == 3) {
            String id = r3Tree.get(ctx.id());
            String expr = r3Tree.get(ctx.expr());
            result += expr;
            result += "istore_" + id + "\n";
        }
        else {
            String additive_expr = r3Tree.get(ctx.additive_expr());
            result += additive_expr;
        }
        r3Tree.put(ctx, result);
    }

    @Override public void exitAdditive_expr(tinyR3Parser.Additive_exprContext ctx) {
        String result = "";
        String additive_expr = r3Tree.get(ctx.additive_expr());
        String multiplicative_expr = r3Tree.get(ctx.multiplicative_expr());
        if(ctx.getChildCount() == 1) {
            result += multiplicative_expr;
        }
        else {
            String op = ctx.getChild(1).getText();

            if(Objects.equals(op, "+")) op = "iadd";
            else if(Objects.equals(op, "-")) op = "isub";
            else if(Objects.equals(op, "*")) op = "isub";
            else if(Objects.equals(op, "/")) op = "idiv";
            else op = "irem";

            if(additive_expr.contains("\n")) result += additive_expr;
            else result += "iload_" + additive_expr + "\n";
            if(r3Tree.get(ctx.multiplicative_expr().unary_expr().factor().id()) != null)
                result += "iload_" + multiplicative_expr + "\n";
            else
                result += "bipush " + multiplicative_expr + "\n";
            result += op + "\n";
        }
        r3Tree.put(ctx, result);
    }

    @Override public void exitMultiplicative_expr(tinyR3Parser.Multiplicative_exprContext ctx) {
        String result = "";
        result += r3Tree.get(ctx.unary_expr());
        r3Tree.put(ctx, result);
    }

    @Override public void exitUnary_expr(tinyR3Parser.Unary_exprContext ctx) {
        String result = "";
        if(ctx.getChildCount() == 1)
            result += r3Tree.get(ctx.factor());
        else {
            result += r3Tree.get(ctx.getChild(0)) + " ";
            String expr = r3Tree.get(ctx.expr());
            result += expr;
        }
        r3Tree.put(ctx, result);
    }

    @Override public void exitFactor(tinyR3Parser.FactorContext ctx) {
        String result = r3Tree.get(ctx.id());
        if(result == null)
            result = r3Tree.get(ctx.literal());
        r3Tree.put(ctx, result);
    }

    @Override public void exitPrint_stmt(tinyR3Parser.Print_stmtContext ctx) {
        String result = "";
        result += "getstatic java/lang/System/out Ljava/io/PrintStream;\n";
        result += "iload_" + r3Tree.get(ctx.id()) + "\n";
        result += "invokevirtual java/io/PrintStream/println(I)V\n";
        r3Tree.put(ctx, result);
    }

    @Override public void exitReturn_stmt(tinyR3Parser.Return_stmtContext ctx) {
        String result = "";
        result += "return\n";
        r3Tree.put(ctx, result);
    }

    public static int getVal(String id) {
        if (indexMap.containsKey(id)) {
            return indexMap.get(id);
        }

        values.add(id);
        int index = values.size() - 1;
        indexMap.put(id, index);

        return index;
    }
}
