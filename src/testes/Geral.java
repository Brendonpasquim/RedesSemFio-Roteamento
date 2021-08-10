package testes;

import java.util.HashSet;

public class Geral {
    public static void main(String[] args) {
        HashSet<String> list1 = new HashSet<>();
        HashSet<String> list2 = new HashSet<>();

        list1.add("1");
        list1.add("2");
        list1.add("3");

        list2.add("3");
        list2.add("4");

        System.out.println(list1);
        System.out.println(list2);
        list1.addAll(list2);
        System.out.println(list1);
    }
}
