import java.io.*;
import java.util.Objects;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {
        // BufferedReader 객체 생성
        BufferedReader reader = new BufferedReader(new FileReader("./src/test.imlg"));
        // BufferedWriter 객체 생성
        BufferedWriter writer = new BufferedWriter(new FileWriter("./src/test.c"));
        // 기본 코드 작성
        writer.write("#include <stdio.h>\n");
        writer.write("#include <stdlib.h>\n");
        writer.write("void main()\n");
        writer.write("{\n");
        writer.write("int a;\n");
        writer.write("int b;\n");
        writer.write("int c;\n");
        writer.write("char *as = malloc(100 * sizeof(char));\n");
        writer.write("char *bs = malloc(100 * sizeof(char));\n");
        writer.write("char *cs = malloc(100 * sizeof(char));\n");

        String str, temp;
        // 파일 한 줄씩 읽어오기
        while ((str = reader.readLine()) != null) {
            // findPattern 함수를 통해 패턴 분석
            temp = findPattern(str);
            // test.c에 작성
            writer.write(temp);
        }

        // 기본 코드 작성
        writer.write("}\n");
        // reader, writer 닫기
        writer.close();
        reader.close();
    }

    // 패턴을 분석하여 해당 복호화 하기
    public static String findPattern(String str) {
        String scan_int = ":\\)"; // 정수 입력
        String scan_str = ":\\)\\:]"; // 문자열 입력
        String value = "\\^{1,3}"; // 변수
        String print_int = ":\\)\\)"; // 정수 출력
        String print_str = ":\\)\\):]"; // 문자열 출력
        String print_nl = ":\\)\\):]]"; // 줄바꿈 출력
        String plus = ":}"; // 덧셈
        String minus = ":}}"; // 뺄셈
        String save_int = ":\\(\\)"; // 연산 후 대입
        String save_str = ":\\(\\):]"; // 문자열 대입

        // 공백 단위로 문자열을 쪼개어 배열에 저장
        String[] word = str.split(" ");

        // 정수 scanf에 해당할 경우
        if (Pattern.matches(scan_int, word[0])) {
            return "scanf(\"%d\", &" + getValue(word[1]) + ");\n";
        }
        // 문자열 scanf에 해당할 경우
        else if(Pattern.matches(scan_str, word[0])) {
            return "scanf(\"%s\", " + getValue(word[1]) + "s);\n";
        }
        // 정수 출력에 해당할 경우
        else if (Pattern.matches(print_int, word[0])) {
            return "printf(\"%d\", " + getValue(word[1]) + ");\n";
        }
        // 문자열 출력에 해당할 경우
        else if (Pattern.matches(print_str, word[0])) {
            // 문자열 변수 출력에 해당할 경우
            if (Pattern.matches(value, word[1])) {
                return "printf(\"%s\", " + getValue(word[1]) + "s);\n";
            }
            // 문자열 출력에 해당할 경우
            return "printf(\"" + getValue(word[1]) + "\");\n";
        }
        // 줄바꿈 출력에 해당할 경우
        else if (Pattern.matches(print_nl, word[0])) {
            return "printf(\"\\n\");\n";
        }
        // 연산자에 해당할 경우
        else if (Pattern.matches(save_int, word[0])) {
            // 덧셈에 해당할 경우
            if (Pattern.matches(plus, word[2])) {
                return getValue(word[1]) + " = " + getValue(word[3]) + " + " + getValue(word[4]) + ";\n";
            }
            // 뺄셈에 해당할 경우
            else if (Pattern.matches(minus, word[2])) {
                return getValue(word[1]) + " = " + getValue(word[3]) + " - " + getValue(word[4]) + ";\n";
            }
            // 일반 대입에 해당할 경우
            return getValue(word[1]) + " = " + getValue(word[2]) + ";\n";
        }
        // 문자열 대입에 해당할 경우
        else if (Pattern.matches(save_str, word[0])) {
            return getValue(word[1]) + "s = \"" + getValue(word[2]) + "\";\n";
        }
        return "";
    }

    // 변수에 해당하는 문자열 반환
    public static String getValue(String str) {

        // ^일 경우
        if (Objects.equals(str, "^")) {
            return "a";
        }
        // ^^일 경우
        else if (Objects.equals(str, "^^")) {
            return "b";
        }
        // ^^^일 경우
        else if (Objects.equals(str, "^^^")) {
            return "c";
        }
        // 상수인 경우
        return str;
    }
}