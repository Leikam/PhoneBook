import java.util.Arrays;
import java.util.Scanner;

public class PhoneBook {

    /* энумы пока не в счет */
    public static final String NAME = "NAME";
    public static final String PHONE = "PHONE";

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

        list(phoneBook);

        System.out.println("\nИскать по номеру телефона? (\"+\" – да, \"-\" – нет)");
        if ("+".equals(SCANNER.nextLine().trim())) {
            System.out.println("Поиск по телефону\n");

            String phone = formatPhoneNumber(askUserPhoneNumber());
            logInfo("Телефон: " + phone);

            /* проверяем есть ли такая птица в нашей книге */
            String user = getUserInfoByType(PHONE, phone, phoneBook);

            if (user == null) {
                String userFullName = askUserFullName();

                logInfo(userFullName);

                add(phoneBook, userFullName, phone);

            } else {
                logInfo(String.format("Номер телефона %s найден.\nИмя пользователя: %s", phone, user));

            }
        } else {
            System.out.println("Поиск по имени\n");

            String userFullName = askUserFullName();
            logInfo("Пользователь: " + userFullName);

            /* проверяем есть ли такой телефон в нашей книге */
            String phone = getUserInfoByType(NAME, userFullName, phoneBook);

            if (phone == null) {
                phone = formatPhoneNumber(askUserPhoneNumber());

                logInfo(phone);

                add(phoneBook, userFullName, phone);

            } else {
                logInfo(String.format("Пользователь %s найден.\nНомер телефона: %s", userFullName, phone));
            }
        }

        list(phoneBook);
    }
    /** Спрашиваем номер телефона */
    private static String askUserPhoneNumber() {
        System.out.println("Введите номер телефона:");
        String phone;/* Читаем номер телефона пока не введется соответствующий нашим высоким стандартам (> 10 && < 11) */
        do {
            phone = cleanPhoneNumber(SCANNER.nextLine());
        }
        while (!checkPhoneNumber(phone));
        return phone;
    }

    /** Спрашиваем ФИО пользователя */
    private static String askUserFullName() {
        String userFullName;
        do {
            /* Узнаем ФИО */
            userFullName = promptUserFullName();
        }
        /* проверяем на формат: 3 слова, эвристику на тип слова делать пока не будем ;) */
        while(!checkName(userFullName));

        return userFullName;
    }

    /** Ищем по типу данных. Типов у нас два и в рамах задачи они стринговые: "NAME" и "PHONE" */
    private static String getUserInfoByType(String dataType, String key, String[][] phoneBook) {
        boolean isLookupByName = NAME.equalsIgnoreCase(dataType);
        for (String[] userRow : phoneBook) {
            if (key.equals(userRow[isLookupByName ? 0 : 1])) {
                return userRow[isLookupByName ? 1 : 0];
            }
        }

        return null;
    }

    /** Спрашиваем и читаем ФИО пользователя */
    private static String promptUserFullName() {
        String[] userCred = new String[3];
        for (int i = 0; i < userCred.length; i++) {
            System.out.println(TERMINAL_PROMPTER[i]);
            String s = SCANNER.nextLine().trim();
            userCred[i] = s.substring(0, 1).toUpperCase() + s.substring(1);
        }

        return createFullName(userCred);
    }

    /** Создаем строку с полными инициалами пользователя */
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

    /** Проверка имени по количеству слов. Так себе проверка, но пока хватит */
    public static boolean checkName(String name) {
        if (name.split("\\s").length == 3) {
            return true;
        }

        logError("Неверный формат имени. Ждем от вас инициалы в формате: Фамилия Имя Отчество");

        return false;
    }

    /** Форматируем телефон по шаблону +7 (ххх) ххх-хх-хх */
    public static String formatPhoneNumber(String number) {
        String phoneClean = number.replaceAll("[\\D]", "");
        if (phoneClean.length() == 10) {
            phoneClean = "7" + phoneClean;
        } else if ('8' == phoneClean.charAt(0)) {
            phoneClean = "7" + phoneClean.substring(1);
        }

        return String.format(
            "+%s (%s) %s-%s-%s",
            phoneClean.substring(0, 1),
            phoneClean.substring(1, 4),
            phoneClean.substring(4, 7),
            phoneClean.substring(7, 9),
            phoneClean.substring(9)
        );
    }

    /**
     * Добавление записи в книгу c проверкой массива на достаточный размер
     */
    public static void add(String[][] book, String name, String number) {

        book = resizeArrayWhenFull(book);

        if (book[totalRecords] == null) {
            book[totalRecords] = new String[2];
        }

        book[totalRecords][0] = name;
        book[totalRecords][1] = number;

        totalRecords++;
    }

    private static String[][] resizeArrayWhenFull(String[][] book) {
        /* Создаем новый массив, в 2 раза большего размера */
        if (book.length == totalRecords) {
            logInfo("Телефонная книга заканчивается.. приклеиваем новые странички.");
            return PhoneBook.phoneBook = Arrays.copyOf(book, Math.min(totalRecords << 1, Integer.MAX_VALUE));
        }

        return PhoneBook.phoneBook;
    }

    /**
     * Печатаем книгу
     */
    public static void list(String[][] book) {

        /* Собираем строку из имен, так будет проще затем получить массив без сложных структур с помощью split() */
        String names = "";
        for (String[] user : book) {
            if (user != null) {
                names += user[0] + STRING_DIVIDER;
            }
        }
        String[] namesArray = names.split(STRING_DIVIDER);

        Arrays.sort(namesArray);

        System.out.print("\nТелефонная книга:\n===========\n");
        for (String name : namesArray) {
            System.out.printf("%s: %s\n", name, getUserInfoByType(NAME, name, book));
        }

    }

    private static void logInfo(String info) {
        System.out.println(ANSI_BLUE + info + ANSI_RESET);
    }

    private static void logError(String info) {
        System.out.println(ANSI_RED + info + ANSI_RESET);
    }

}
