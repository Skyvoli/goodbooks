package io.skyvoli.goodbooks.helper;

public class TitleBuilder {

    private TitleBuilder() {
        throw new IllegalStateException("Utility class");
    }

    public static String buildWholeTitle(String title, String subtitle, Integer part) {
        if (subtitle != null) {
            return title + " - " + subtitle;
        }
        if (part != null) {
            return title + " " + part;
        }

        return title;

    }
}