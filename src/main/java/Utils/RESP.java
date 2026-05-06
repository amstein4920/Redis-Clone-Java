package Utils;

public class RESP {
    public static String simpleString(String s) {
        return String.format("+%s\r\n", s);
    }

    public static String simpleErrorString(String s) {
        return String.format("-%s\r\n", s);
    }

    public static String bulkString(String s) {
        return String.format("$%d\r\n%s\r\n", s.length(), s);
    }

    public static String nullBulkString() {
        return "$-1\r\n";
    }

    public static String array(String... elements) {
        StringBuilder sb = new StringBuilder(String.format("*%d\r\n", elements.length));
        for (String element : elements) {
            sb.append(RESP.bulkString(element));
        }

        return sb.toString();
    }
}
