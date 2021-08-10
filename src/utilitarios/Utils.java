package utilitarios;

import java.util.List;
import java.util.Scanner;

import java.nio.file.*;

public final class Utils {
    private static final Scanner SCAN = new Scanner(System.in);

    public static List<String> LerTxt(String path) {
        try {
            return Files.readAllLines(Paths.get(path));
        } catch (Exception e) {
            return null;
        }
    }

    public static void sleep(int segundos) {
        try {
            Thread.sleep(segundos * 1000);
        } catch (Exception e) {
        }
    }

    public static String IOString(String mensagem) {
        System.out.print(mensagem);
        return SCAN.nextLine();
    }
}