import generated.tinyR4BaseListener;
import generated.tinyR4Parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

public class tinyR4PrintListener  extends tinyR4BaseListener implements ParseTreeListener {

    public static int count = 0;

    private static String output;
    ParseTreeProperty<String> r4Tree = new ParseTreeProperty<>();

    public static String getOutput() {
        return output;
    }

    @Override public void exitProgram(tinyR4Parser.ProgramContext ctx) {
        String program = "";
        for (int i = 0; i < ctx.decl().size(); i++) {
            program += r4Tree.get(ctx.decl(i));
        }
        output = program;
    }

    @Override public void exitDecl(tinyR4Parser.DeclContext ctx) {
        String fun_decl = r4Tree.get(ctx.fun_decl());
        r4Tree.put(ctx, fun_decl);
    }

    @Override public void exitFun_decl(tinyR4Parser.Fun_declContext ctx) {
        String compoundStmt = r4Tree.get(ctx.compound_stmt());
        String param = r4Tree.get(ctx.params());
        String name = r4Tree.get(ctx.id());
        String func = ctx.getChild(0).getText();
        if (param == null) param = "";

        count += 1;
        System.out.println("Fun decl: " + count);
        String funDecl = func + " " + name + "(" + param + ") {\n" + compoundStmt + "}";
//        count -= 1;
        r4Tree.put(ctx, funDecl);
    }

    @Override public void exitCompound_stmt(tinyR4Parser.Compound_stmtContext ctx) {
        String compoundStmt = "";
        System.out.println("Compound stmt: " + count);
        for (int i = 0; i < count; i++) {
            compoundStmt += "   ";
        }
        for (int i = 0; i < ctx.stmt().size(); i++) {
            String stmt = r4Tree.get(ctx.stmt(i));
            compoundStmt += stmt + "\n";
        }
        r4Tree.put(ctx, compoundStmt);
    }

    @Override public void exitStmt(tinyR4Parser.StmtContext ctx) {
        String stmt = r4Tree.get(ctx.getChild(0));
        r4Tree.put(ctx, stmt);
    }

    @Override public void exitExpr_stmt(tinyR4Parser.Expr_stmtContext ctx) {
        String expr = r4Tree.get(ctx.expr());
        String semi = ctx.getChild(1).getText();
        r4Tree.put(ctx, expr + semi);
    }

    @Override public void exitExpr(tinyR4Parser.ExprContext ctx) {
        String expr = r4Tree.get(ctx.getChild(0));
        r4Tree.put(ctx, expr);
    }

    @Override public void exitAdditive_expr(tinyR4Parser.Additive_exprContext ctx) {
        if(ctx.getChildCount() == 1) {
            String expr = r4Tree.get(ctx.getChild(0));
            r4Tree.put(ctx, expr);
        } else {
            String left = r4Tree.get(ctx.getChild(0));
            String right = r4Tree.get(ctx.getChild(2));
            String op = ctx.getChild(1).getText();
            r4Tree.put(ctx, left + " " + op + " " + right);
        }
    }

    @Override public void exitMultiplicative_expr(tinyR4Parser.Multiplicative_exprContext ctx) {
        if(ctx.getChildCount() == 1) {
            String expr = r4Tree.get(ctx.getChild(0));
            r4Tree.put(ctx, expr);
        } else {
            String left = r4Tree.get(ctx.getChild(0));
            String right = r4Tree.get(ctx.getChild(2));
            String op = ctx.getChild(1).getText();
            r4Tree.put(ctx, left + " " + op + " " + right);
        }
    }

    @Override public void exitUnary_expr(tinyR4Parser.Unary_exprContext ctx) {
        String expr = r4Tree.get(ctx.getChild(0));
        r4Tree.put(ctx, expr);
    }

    @Override public void exitFactor(tinyR4Parser.FactorContext ctx) {
        if(ctx.getChildCount() == 1) {
            String factor = r4Tree.get(ctx.getChild(0));
            r4Tree.put(ctx, factor);
        } else {
            String left = ctx.getChild(0).toString();
            String right = ctx.getChild(2).toString();
            String expr = r4Tree.get(ctx.getChild(1));
            r4Tree.put(ctx, left + expr + right);
        }
    }

    @Override public void exitLiteral(tinyR4Parser.LiteralContext ctx) {
        String literal = ctx.getText();
        r4Tree.put(ctx, literal);
    }

    @Override public void exitId(tinyR4Parser.IdContext ctx) {
        String id = ctx.ID().getText();
        r4Tree.put(ctx, id);
    }
}
