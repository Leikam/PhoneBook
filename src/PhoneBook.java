import java.util.Arrays;
import java.util.Scanner;

public class PhoneBook {

    public static final String STRING_DIVIDER = ",";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLUE = "\u001B[34m";

    public static final String[] TERMINAL_PROMPTER = new String[] {
        "Введите фамилию:",
        "Введите имя:",
        "Введите отчество:",
        };
    public static final Scanner SCANNER = new Scanner(System.in);

    public static String[][] phoneBook = new String[5][2];
    public static int totalRecords;

    public static void main(String[] args) {

        // Предзаполняем книжку
        add(phoneBook, "Тарковский Игорь Николаевич", formatPhoneNumber("89650157766"));
        add(phoneBook, "Ыть Екатерина Сергеевна", formatPhoneNumber("+79608003652"));
        add(phoneBook, "Бычок Ольга Федоровна", formatPhoneNumber("81236547788"));
        add(phoneBook, "Алмазов Б В", formatPhoneNumber("01112223344"));
        add(phoneBook, "Януш Д Е", formatPhoneNumber("21112223344"));


        String userFullName;
        do {
            /* Узнаем ФИО */
            userFullName = promptUserFullName();
        }
        /* проверяем на формат: 3 слова, эвристику на тип слова делать пока не будем ;) */
        while(!checkName(userFullName));

        logInfo("\n" + userFullName + "\n");

        /* проверяем есть ли такая птица в нашей книге */
        String phone = getPhoneNumberByFullName(phoneBook, userFullName);

        if (phone == null) {
            System.out.println("Введите номер телефона:");

            /* Читаем номер телефона пока не введется соответствующий нашим высоким стандартам (> 10 && < 11) */
            do {
                phone = cleanPhoneNumber(SCANNER.nextLine());
            }
            while (!checkPhoneNumber(phone));

            String phoneFormatted = formatPhoneNumber(phone);

            logInfo(phoneFormatted);

            /* Создаем новый массив, в 2 раза большего размера */
            if (phoneBook.length == totalRecords) {
                logInfo("Телефонная книга заканчивается.. приклеиваем новые странички.");
                phoneBook = Arrays.copyOf(phoneBook, Math.min(totalRecords << 1, Integer.MAX_VALUE));
            }

            add(phoneBook, userFullName, phoneFormatted);

            list(phoneBook);

        } else {
            logInfo(String.format("Пользователь найден. Номер телефона: %s", phone));
        }
    }

    private static String getPhoneNumberByFullName(String[][] phoneBook, String userFullName) {
        for (String[] userRow : phoneBook) {
            if (userFullName.equals(userRow[0])) {
                return userRow[1];
            }
        }

        return null;
    }

    private static String promptUserFullName() {
        String[] userCred = new String[3];
        for (int i = 0; i < userCred.length; i++) {
            System.out.println(TERMINAL_PROMPTER[i]);
            String s = SCANNER.nextLine().trim();
            userCred[i] = s.substring(0, 1).toUpperCase() + s.substring(1);
        }

        return createFullName(userCred);
    }

    public static String createFullName(String[] cred) {
        return String.format("%s %s %s", cred[0], cred[1], cred[2]).trim();
    }

    /**
     * Чистим номер от мусора
     */
    public static String cleanPhoneNumber(String number) {
        return number.replaceAll("[\\D]", "");
    }

    /**
     * Принимаем уже очищенный номер, состоящий из одних цифр
     */
    public static boolean checkPhoneNumber(String cleanPhoneNumber) {
        if (cleanPhoneNumber.length() < 10 || cleanPhoneNumber.length() > 11) {
            logError("Неправильный номер!");
            return false;
        }

        return true;
    }

    public static boolean checkName(String name) {
        if (name.split("\\s").length == 3) {
            return true;
        }

        logError("Неверный формат имени. Ждем от вас инициалы в формате: Фамилия Имя Отчество");

        return false;
    }

    public static String formatPhoneNumber(String number) {
        String phoneClean = number.replaceAll("[\\D]", "");
        if (phoneClean.length() == 10) {
            phoneClean = "7" + phoneClean;
        } else if ('8' == phoneClean.charAt(0)) {
            phoneClean = "7" + phoneClean.substring(1);
        }

        return "+" +
               phoneClean.substring(0, 1) + " " +
               phoneClean.substring(1, 4) + " " +
               phoneClean.substring(4, 7) + " " +
               phoneClean.substring(7, 9) + " " +
               phoneClean.substring(9);
    }

    /**
     * Добавление записи в книгу
     */
    public static void add(String[][] book, String name, String number) {
        if (book[totalRecords] == null) {
            book[totalRecords] = new String[2];
        }
        book[totalRecords][0] = name;
        book[totalRecords][1] = number;

        totalRecords++;
    }

    /**
     * Печатаем книгу
     */
    public static void list(String[][] book) {

        /* Собираем строку из имен, так проще получить массив без сложных структур */
        String names = "";
        for (String[] user : book) {
            if (user != null) {
                names += user[0] + STRING_DIVIDER;
            }
        }
        String[] namesArray = names.split(STRING_DIVIDER);

        Arrays.sort(namesArray);

        for (String name : namesArray) {
            System.out.printf("%s: %s\n", name, getPhoneNumberByFullName(book, name));
        }

    }

    private static void logInfo(String info) {
        System.out.println(ANSI_BLUE + info + ANSI_RESET);
    }

    private static void logError(String info) {
        System.out.println(ANSI_RED + info + ANSI_RESET);
    }
}
