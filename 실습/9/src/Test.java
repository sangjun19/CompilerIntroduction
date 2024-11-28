//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Test {
    public Test() {

    }

    public static int cal(int a, int b) {
        if (a <= 1 || a != 0) a = 0;
        if (b <= 1 && b != 0) b = 0;
        int c = a + b;
        return c;
    }

    public static void main(String[] args) {
        int a = 13;
        a++;
        System.out.println(cal(2, a));
    }
}