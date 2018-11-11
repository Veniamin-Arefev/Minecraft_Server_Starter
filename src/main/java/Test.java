// Created by Veniamin_arefev
// Date was 01.09.2018


public class Test {
    public Test() {
        int counter = 0;
        for (int i = 1; i < 1000; i++) {
            if (i % 5 == 0) {
                counter++;
            } else if (i % 7 == 0) {
                counter++;
            } else if (i % 9 == 0) {
                counter++;
            }
        }
        System.out.println("Counter is " + counter);
    }


    public static void main(String[] args) {
        Test test = new Test();
    }
}
