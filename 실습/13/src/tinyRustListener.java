import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import gen.tinyRustBaseListener;
import gen.tinyRustParser;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Objects;

public class tinyRustListener extends tinyRustBaseListener implements ParseTreeListener{
    ParseTreeProperty<String> rustTree = new ParseTreeProperty<>();

    private static FileWriter fw;
    static HashMap<String, Integer> localVarMap;
    static int localVar_curIdx = 0;
    static int labelCnt = 1;
    static int compareType = 0;
    static int firstOr = 0;

    private static void assignLocalVar(String VarName){
        if(!(localVarMap.containsKey(VarName))) localVarMap.put(VarName, localVar_curIdx);
        localVar_curIdx++;
    }

    private static int getLocalVarTableIdx(String VarName) {
        return localVarMap.get(VarName);
    }

    @Override public void enterProgram(tinyRustParser.ProgramContext ctx) {
        // 파일 출력
        File outputFile = new File("./src/Test.j");
        //변수 테이블
        localVarMap = new HashMap<>();

        try {
            if (!outputFile.exists()) {
                if(!outputFile.createNewFile()) throw new Exception("파일 생성 실패");
            }

            fw = new FileWriter(outputFile);
            fw.write("""
                .class public Test
                .super java/lang/Object
                ; strandard initializer
                .method public <init>()V
                aload_0
                invokenonvirtual java/lang/Object/<init>()V
                return
                .end method
                
                """);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Test 클래스 정의 및 기본 생성자

    }

    @Override public void exitProgram(tinyRustParser.ProgramContext ctx) {
        StringBuilder program = new StringBuilder();
        for (int i = 0; i < ctx.decl().size(); i++)
            program.append(rustTree.get(ctx.decl(i)));
        // 프로그램 끝 : output 파일에 write
        try {
            fw.write(program.toString());
            fw.flush();

            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override public void exitDecl(tinyRustParser.DeclContext ctx) {
        String main_decl = rustTree.get(ctx.main_decl());
        rustTree.put(ctx, main_decl);
    }

    @Override public void enterMain_decl(tinyRustParser.Main_declContext ctx) {
        // Main_decl은 main 함수이므로 main을 위한 자료구조 및 변수 초기화
        try {
            fw.write("""
                                
                .method public static main([Ljava/lang/String;)V
                .limit stack 32
                .limit locals 32
                """);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override public void exitMain_decl(tinyRustParser.Main_declContext ctx) {
        String compound_stmt = rustTree.get(ctx.compound_stmt());
        rustTree.put(ctx, compound_stmt + "\n" + ".end method\n\n");
    }

    @Override public void exitCompound_stmt(tinyRustParser.Compound_stmtContext ctx) {
        StringBuilder result = new StringBuilder();
        int local_count = ctx.local_decl().size();
        int stmt_count = ctx.stmt().size();
        for (int i = 0; i < local_count; i++)
            result.append(rustTree.get(ctx.local_decl(i)));
        for (int i = 0; i < stmt_count; i++)
            result.append(rustTree.get(ctx.stmt(i)));
        rustTree.put(ctx, result.toString());
    }

    @Override public void exitLocal_decl(tinyRustParser.Local_declContext ctx) {//변수 할당(Assignment)
        String result = "";
        String val = rustTree.get(ctx.val());
        String id = rustTree.get(ctx.id());
        if(localVarMap.containsKey(id))
            result = "istore_" + getLocalVarTableIdx(id);
        else {
            result = "istore_" + localVar_curIdx;
            assignLocalVar(id);
        }
        rustTree.put(ctx, val + result + "\n");
    }

    @Override public void exitVal(tinyRustParser.ValContext ctx) {//변수 할당 우변의, 할당될 값
        String result = "";
        if(ctx.literal() != null)
            result = "bipush " + rustTree.get(ctx.literal());
        else if(ctx.id() != null)
            result = "iload_" + getLocalVarTableIdx(rustTree.get(ctx.id()));
        rustTree.put(ctx, result + "\n");
    }

    @Override public void exitStmt(tinyRustParser.StmtContext ctx) {
        String result = "";
        if(ctx.expr_stmt() != null)
            result = rustTree.get(ctx.expr_stmt());
        else if(ctx.assignment_stmt() != null)
            result = rustTree.get(ctx.assignment_stmt());
        else if(ctx.compound_stmt() != null)
            result = rustTree.get(ctx.compound_stmt());
        else if(ctx.return_stmt() != null)
            result = rustTree.get(ctx.return_stmt());
        else if(ctx.print_stmt() != null)
            result = rustTree.get(ctx.print_stmt());
        else if(ctx.if_stmt() != null)
            result = rustTree.get(ctx.if_stmt());
        rustTree.put(ctx, result);
    }

    @Override public void exitExpr_stmt(tinyRustParser.Expr_stmtContext ctx) {
        rustTree.put(ctx, rustTree.get(ctx.expr()));
    }

    @Override public void exitExpr(tinyRustParser.ExprContext ctx) {
        rustTree.put(ctx, rustTree.get(ctx.relative_expr()));
    }

    @Override public void exitAdditive_expr(tinyRustParser.Additive_exprContext ctx) {
        String result = "";
        String op, left, right;
        if(ctx.additive_expr() != null) {
            left = rustTree.get(ctx.additive_expr());
            op = ctx.getChild(1).getText();
            if(op.equals("+"))
                op = "iadd\n";
            else if(op.equals("-"))
                op = "isub\n";
            right = rustTree.get(ctx.multiplicative_expr());
            result = left + right + op;
        } else
            result = rustTree.get(ctx.multiplicative_expr());
        rustTree.put(ctx, result);
    }

    @Override public void exitMultiplicative_expr(tinyRustParser.Multiplicative_exprContext ctx) {
        String result = "";
        String op, left, right;
        if(ctx.multiplicative_expr() != null) {
            left = rustTree.get(ctx.multiplicative_expr());
            op = ctx.getChild(1).getText();
            switch (op) {
                case "*" -> op = "imul\n";
                case "/" -> op = "idiv\n";
                case "%" -> op = "irem\n";
            }
            right = rustTree.get(ctx.unary_expr());
            result = left + right + op;
        } else
            result = rustTree.get(ctx.unary_expr());
        rustTree.put(ctx, result);
    }

    @Override public void exitUnary_expr(tinyRustParser.Unary_exprContext ctx) {
        String result = rustTree.get(ctx.factor());
        rustTree.put(ctx, result);
    }

    @Override public void exitFactor(tinyRustParser.FactorContext ctx) {//expr 막바지에 호출, literal, id 터미널 호출하거나 괄호 연산
        String result = "";
        if (ctx.id() != null)
            result = "iload_" + getLocalVarTableIdx(rustTree.get(ctx.id()));
        else if (ctx.literal() != null)
            result = "bipush " + rustTree.get(ctx.literal());
        rustTree.put(ctx, result + "\n");
    }

    //Todo
    @Override public void exitComparative_expr(tinyRustParser.Comparative_exprContext ctx) {
        if(ctx.getChildCount() == 1) {
            rustTree.put(ctx, rustTree.get(ctx.additive_expr()));
            return;
        }
        String result = "";
        String left = rustTree.get(ctx.comparative_expr());
        String right = rustTree.get(ctx.additive_expr());
        String op = ctx.getChild(1).getText();
        if(compareType > 0) {
            switch(op) {
                case "<" -> op = ">=";
                case "<=" -> op = ">";
                case ">" -> op = "<=";
                case ">=" -> op = "<";
                case "==" -> op = "!=";
                case "!=" -> op = "==";
            }
        }
        switch (op) {
            case "<" -> op = "if_icmpge ";
            case "<=" -> op = "if_icmpgt ";
            case ">" -> op = "if_icmple ";
            case ">=" -> op = "if_icmplt ";
            case "==" -> op = "if_icmpne ";
            case "!=" -> op = "if_icmpeq ";
        }
//        int temp = labelCnt + compareType;
        result = left + right + op + "L" + labelCnt + "\n";
        rustTree.put(ctx, result);
//        System.out.println(compareType);
        if(compareType > 0) {
            labelCnt++;
            compareType--;
        }
    }

    @Override public void exitRelative_expr(tinyRustParser.Relative_exprContext ctx) {
        if(ctx.getChildCount() == 1) {
            rustTree.put(ctx, rustTree.get(ctx.comparative_expr()));
        }
        else {
            String result = "";
            String relative_expr = rustTree.get(ctx.relative_expr());
            String comparative_expr = rustTree.get(ctx.comparative_expr());
            String op = ctx.getChild(1).getText();
            if(Objects.equals(op, "||")) {
                int temp = labelCnt - 1;
                if(firstOr > 1) {
                    temp--;
                }
                result += relative_expr + comparative_expr + "L" + temp + ":\n";
            }
            else result += relative_expr + comparative_expr;
            firstOr = 0;
            rustTree.put(ctx, result);
        }

    }

    @Override public void enterRelative_expr(tinyRustParser.Relative_exprContext ctx) {
        if(ctx.getChildCount() != 1) {
            String op = ctx.getChild(1).getText();
            if(Objects.equals(op, "||")) {
                compareType++;
                firstOr = compareType;
            }
        }
    }

    @Override public void exitAssignment_stmt(tinyRustParser.Assignment_stmtContext ctx) {
        String expr = rustTree.get(ctx.expr());
        String id = rustTree.get(ctx.id());
        String result = "";
        if(localVarMap.containsKey(id)) result = "istore_" + getLocalVarTableIdx(id);
        expr += result + "\n";
        rustTree.put(ctx, expr);
    }

    @Override
    public void exitPrint_stmt(tinyRustParser.Print_stmtContext ctx) {
        String result = "getstatic java/lang/System/out Ljava/io/PrintStream;\n";
        result += "iload_" + getLocalVarTableIdx(rustTree.get(ctx.id())) + "\n";
        result += "invokevirtual java/io/PrintStream.println(I)V\n";
        rustTree.put(ctx, result);
    }

    @Override public void exitReturn_stmt(tinyRustParser.Return_stmtContext ctx) {
        rustTree.put(ctx, "return\n");
    }

    @Override public void exitLiteral(tinyRustParser.LiteralContext ctx) {
        rustTree.put(ctx, ctx.LITERAL().getText());
    }

    @Override public void exitId(tinyRustParser.IdContext ctx) {
        rustTree.put(ctx, ctx.ID().getText());
    }

    @Override public void exitIf_stmt(tinyRustParser.If_stmtContext ctx) {
        String result = "";
        String relative_expr = rustTree.get(ctx.relative_expr());
        String compound_stmt = rustTree.get(ctx.compound_stmt(0));
        result += relative_expr + compound_stmt + "L" + labelCnt + ":\n";
        if(ctx.getChildCount() > 3) {
            String compound_stmt2 = rustTree.get(ctx.compound_stmt(1));
            result += compound_stmt2;
        }
        labelCnt++;
        rustTree.put(ctx, result);
        compareType = 0;
    }
}
