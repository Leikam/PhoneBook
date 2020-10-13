import java.util.Arrays;
import java.util.Scanner;

public class PhoneBook {

    public static final String[] TERMINAL_PROMPTER = new String[]{
        "Введите фамилию",
        "Введите имя",
        "Введите отчество",
        };

    public static void main(String[] args) {
        //Добавить считывание ввода пользователя в цикле

        String[] user = new String [3];
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < user.length; i++) {
            System.out.println(TERMINAL_PROMPTER[i]);
            String s = scanner.nextLine();
            user[i] = s;
        }

        System.out.println(Arrays.toString(user));
    }

    public static boolean checkPhoneNumber(String phoneNumber) {
        return true;
    }

    public static boolean checkName(String name) {
        return true;
    }

    public static String formatName(String name) {
        return "";
    }

    public static String formatPhoneNumber(String number) {
        return "";
    }

    public static void add(String[][] book, String name, String number) {
        //add logic
    }

    public static void list(String[][] book) {
        //print phone book
    }
}
