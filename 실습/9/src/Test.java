//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Test {
    public Test() {

    }

    public static int cal(int a, int b) {
        return (a * b - 2) / 3;
    }

    public static void main(String[] args) {
        int a = 13;
        System.out.println(cal(2, a));
    }
}