import generated.tinyR4BaseListener;
import generated.tinyR4Parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

public class tinyR4UglyPrintListener extends tinyR4BaseListener implements ParseTreeListener {
    private static String output;
    ParseTreeProperty<String> r4Tree = new ParseTreeProperty<>();

    public static int deepth = 0;

    public static String getOutput() {
        return output;
    }

    @Override public void exitProgram(tinyR4Parser.ProgramContext ctx) {
        StringBuilder program = new StringBuilder();
        for (int i = 0; i < ctx.decl().size(); i++)
            program.append(r4Tree.get(ctx.decl(i)));
        output = program.toString();
    }

    @Override public void exitDecl(tinyR4Parser.DeclContext ctx) {
        String fun_decl = r4Tree.get(ctx.fun_decl());
        r4Tree.put(ctx, fun_decl);
    }

    @Override public void exitFun_decl(tinyR4Parser.Fun_declContext ctx) {
        String fun = ctx.FUNC().getText();
        String id = r4Tree.get(ctx.id());
        String params = r4Tree.get(ctx.params());
        String ret_type_spec = r4Tree.get(ctx.ret_type_spec());
        String compound_stmt = r4Tree.get(ctx.compound_stmt());
        r4Tree.put(ctx, fun + " " + id + "(" + params + ") " + ret_type_spec + compound_stmt);
    }

    @Override public void exitParams(tinyR4Parser.ParamsContext ctx) {
        int count = ctx.getChildCount();
        if(count == 0)
            r4Tree.put(ctx, "");
        else {
            r4Tree.put(ctx, r4Tree.get(ctx.param(0)));
        }
    }

    @Override public void exitParam(tinyR4Parser.ParamContext ctx) {
        String id = r4Tree.get(ctx.id());
        String type_spec = r4Tree.get(ctx.type_spec());
        r4Tree.put(ctx, id + ctx.getChild(1).getText() + type_spec);
    }

    @Override public void exitType_spec(tinyR4Parser.Type_specContext ctx) {
        r4Tree.put(ctx, ctx.getChild(0).getText());
    }

    @Override public void exitRet_type_spec(tinyR4Parser.Ret_type_specContext ctx) {
        if(ctx.getChildCount() == 0)
            r4Tree.put(ctx, "");
        else {
            String type_spec = r4Tree.get(ctx.type_spec());
            r4Tree.put(ctx, " -> " + type_spec + " ");
        }
    }

    //Todo
    @Override public void exitCompound_stmt(tinyR4Parser.Compound_stmtContext ctx) {
        StringBuilder result = new StringBuilder();
        int local_count = ctx.local_decl().size();
        int stmt_count = ctx.stmt().size();
        for (int i = 0; i < local_count; i++)
            result.append(r4Tree.get(ctx.local_decl(i)));
        for (int i = 0; i < stmt_count; i++)
            result.append(r4Tree.get(ctx.stmt(i)));
        deepth--;
        r4Tree.put(ctx, "{\n" + result + getSpace() + "}\n");
    }

    @Override public void exitVal(tinyR4Parser.ValContext ctx) {
        String result = "";
        if(ctx.literal() != null)
            result = r4Tree.get(ctx.literal());
        else if(ctx.id() != null)
            result = r4Tree.get(ctx.id());
        r4Tree.put(ctx, result);
    }

    @Override public void exitStmt(tinyR4Parser.StmtContext ctx) {
        String result = getSpace();
        if(ctx.expr_stmt() != null)
            result += r4Tree.get(ctx.expr_stmt());
        else if(ctx.return_stmt() != null)
            result += r4Tree.get(ctx.return_stmt());
        else if(ctx.for_stmt() != null)
            result += r4Tree.get(ctx.for_stmt());
        else if(ctx.if_stmt() != null)
            result += r4Tree.get(ctx.if_stmt());
        else if(ctx.break_stmt() != null)
            result += r4Tree.get(ctx.break_stmt());
        r4Tree.put(ctx, result);
    }

    //Todo
    @Override public void exitExpr_stmt(tinyR4Parser.Expr_stmtContext ctx) {
        String expr = r4Tree.get(ctx.expr());
        if(expr != null)
            r4Tree.put(ctx, "" + expr + ";\n");
    }

    @Override public void exitExpr(tinyR4Parser.ExprContext ctx) {
        String result = "";
        if(ctx.additive_expr() != null)
            result = r4Tree.get(ctx.additive_expr());
        else if(ctx.relative_expr() != null)
            result = r4Tree.get(ctx.relative_expr());
        else if(ctx.id() != null)
            result = r4Tree.get(ctx.id()) + " = " + r4Tree.get(ctx.expr());
        r4Tree.put(ctx, result);
    }

    @Override public void exitAdditive_expr(tinyR4Parser.Additive_exprContext ctx) {
        String result = "";
        if(ctx.additive_expr() != null) {
            String left = r4Tree.get(ctx.additive_expr());
            String op = ctx.getChild(1).getText();
            String right = r4Tree.get(ctx.multiplicative_expr());
            result = left + " " + op + " " + right;
        } else
            result = r4Tree.get(ctx.multiplicative_expr());
        r4Tree.put(ctx, result);
    }

    @Override public void exitMultiplicative_expr(tinyR4Parser.Multiplicative_exprContext ctx) {
        String result = "";
        if(ctx.multiplicative_expr() != null) {
            String left = r4Tree.get(ctx.multiplicative_expr());
            String op = ctx.getChild(1).getText();
            String right = r4Tree.get(ctx.unary_expr());
            result = left + " " + op + " " + right;
        } else
            result = r4Tree.get(ctx.unary_expr());
        r4Tree.put(ctx, result);
    }

    @Override public void exitUnary_expr(tinyR4Parser.Unary_exprContext ctx) {
        String result = "";
        if(ctx.expr() != null) {
            String op = ctx.getChild(0).getText();
            String expr = r4Tree.get(ctx.expr());
            result = op + expr;
        } else
            result = r4Tree.get(ctx.factor());
        r4Tree.put(ctx, result);
    }

    @Override public void exitFactor(tinyR4Parser.FactorContext ctx) {
        String result = "";
        if(ctx.expr() != null)
            result = "(" + r4Tree.get(ctx.expr()) + ")";
        else {
            if(ctx.literal() != null)
                result = r4Tree.get(ctx.literal());
            else if(ctx.args() != null) {
                String args = r4Tree.get(ctx.args());
                result = r4Tree.get(ctx.id());
                if(ctx.getChildCount() > 4)
                    result += ctx.getChild(1).getText() + "(" + args + ")";
                else
                    result += "(" + args + ")";
            }
            else {
                result = r4Tree.get(ctx.id());

            }

        }
        r4Tree.put(ctx, result);
    }

    @Override public void exitArgs(tinyR4Parser.ArgsContext ctx) {
        int count = ctx.getChildCount();
        if(count == 0)
            r4Tree.put(ctx, "");
        else {
            r4Tree.put(ctx, r4Tree.get(ctx.expr(0)));
        }
    }

    @Override public void exitRelative_expr(tinyR4Parser.Relative_exprContext ctx) {
        String result = "";
        if(ctx.relative_expr() != null) {
            String left = r4Tree.get(ctx.relative_expr());
            String op = ctx.getChild(1).getText();
            String right = r4Tree.get(ctx.comparative_expr());
            result = left + " " + op + " " + right;
        } else
            result = r4Tree.get(ctx.comparative_expr());
        r4Tree.put(ctx, result);
    }

    @Override public void exitComparative_expr(tinyR4Parser.Comparative_exprContext ctx) {
        String result = "";
        if(ctx.comparative_expr() != null) {
            String left = r4Tree.get(ctx.comparative_expr());
            String op = ctx.getChild(1).getText();
            String right = r4Tree.get(ctx.additive_expr());
            result = left + " " + op + " " + right;
        } else
            result = r4Tree.get(ctx.additive_expr());
        r4Tree.put(ctx, result);
    }

    @Override public void exitLiteral(tinyR4Parser.LiteralContext ctx) {
        r4Tree.put(ctx, ctx.LITERAL().getText());
    }

    @Override public void exitId(tinyR4Parser.IdContext ctx) {
        r4Tree.put(ctx, ctx.ID().getText());
    }

    //Todo
    @Override public void exitLocal_decl(tinyR4Parser.Local_declContext ctx) {
        String dec_spec = r4Tree.get(ctx.dec_spec());
        String id = r4Tree.get(ctx.id());
        String val = r4Tree.get(ctx.val());
        r4Tree.put(ctx, getSpace() + dec_spec + " " + id + " = "  + val + ";\n");
    }

    @Override public void exitDec_spec(tinyR4Parser.Dec_specContext ctx) {
        if(ctx.getChildCount() == 1)
            r4Tree.put(ctx, ctx.getChild(0).getText());
        else
            r4Tree.put(ctx, ctx.getChild(0).getText() + " " + ctx.getChild(1).getText());
    }

    //Todo
    @Override public void exitReturn_stmt(tinyR4Parser.Return_stmtContext ctx) {
        String result = "";
        if(ctx.expr() != null)
            result = r4Tree.get(ctx.expr());
        r4Tree.put(ctx, "return " + result + ";\n");
    }

    @Override public void exitFor_stmt(tinyR4Parser.For_stmtContext ctx) {
        String result = "";
        String id = r4Tree.get(ctx.id());
        String range = r4Tree.get(ctx.range());
        String compound_stmt = r4Tree.get(ctx.compound_stmt());
        result = "for " + id + " in " + range + " " + compound_stmt;
        r4Tree.put(ctx, result);
    }

    @Override public void exitRange(tinyR4Parser.RangeContext ctx) {
        String result = "";
        String literal = r4Tree.get(ctx.literal(0));
        String literal2 = r4Tree.get(ctx.literal(1));
        result = literal + ".." + literal2;
        r4Tree.put(ctx, result);
    }

    //Todo
    @Override public void exitIf_stmt(tinyR4Parser.If_stmtContext ctx) {
        String result = "";
        String relative_expr = r4Tree.get(ctx.relative_expr());
        String compound_stmt = r4Tree.get(ctx.compound_stmt(0));
        result = "if " + relative_expr + " ";
        if(ctx.getChildCount() > 3) {
            compound_stmt = compound_stmt.replaceAll("\n$", "");
            result += compound_stmt + " else " + r4Tree.get(ctx.compound_stmt(1));
        }
        else
            result += compound_stmt;

        r4Tree.put(ctx, result);
    }

    @Override public void exitBreak_stmt(tinyR4Parser.Break_stmtContext ctx) {
        r4Tree.put(ctx, "break;\n");
    }

    @Override public void enterCompound_stmt(tinyR4Parser.Compound_stmtContext ctx) {
        deepth++;
    }

    public String getSpace() {
        return "    ".repeat(Math.max(0, deepth));
    }
}
