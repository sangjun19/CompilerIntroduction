import java.sql.SQLOutput;
import java.util.Scanner;

public class Main {

    public static String[] word;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String str = sc.next();
        word = str.split("");
        System.out.println(start());
    }

    public static String funcA(int i) { // non-terminal A
        // A 가 terminal a 일 경우
        if (word[i].equals("a")) {
            // B가 terminal d, e, f 일 경우
            if (funcB(i + 1).equals("Ok") && word[i + 2].equals("b")) {
                return "Ok";
            }
        }
        // A 가 terminal b 일 경우
        if (word[i].equals("b")) {
            // B가 terminal d, e, f 일 경우
            if (funcB(i + 1).equals("Ok") && word[i + 2].equals("b")) {
                return "Ok";
            }
        }
        // A 가 terminal c 일 경우
        if (word[i].equals("c")) {
            // B가 terminal d, e, f 일 경우
            if (funcB(i + 1).equals("Ok") && word[i + 2].equals("b")) {
                return "Ok";
            }
        }
        // 그 외 Fail
        return "Fail";
    }

    public static String funcB(int i) { // non-terminal B
        // B 가 terminal d, e, f 일 경우
        if (word[i].equals("d") || word[i].equals("e") || word[i].equals("f")) {
            return "Ok";
        }
        // 그 외 Fail
        return "Fail";
    }

    public static String start() { // 시작 S
        if (word[0].equals("a")) { // terminal a
            return funcA(1);
        }
        if (word[0].equals("b")) { // terminal b
            return funcB(1);
        }
        return "Fail"; // 그 외 Fail
    }
}