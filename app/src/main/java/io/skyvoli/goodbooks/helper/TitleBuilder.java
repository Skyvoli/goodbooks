package io.skyvoli.goodbooks.helper;

public class TitleBuilder {

    private TitleBuilder() {
        throw new IllegalStateException("Utility class");
    }

    public static String buildTitle(String title, String subtitle, Integer part) {
        if (subtitle != null) {
            return title + " - " + subtitle;
        }
        if (part != null) {
            return title + " " + part;
        }

        return title;

    }

    public static String buildCompleteTitle(String title, String subtitle, Integer part) {
        StringBuilder stringBuilder = new StringBuilder(title);
        if (subtitle != null) {
            stringBuilder.append(" - ").append(subtitle);
        }
        if (part != null) {
            stringBuilder.append(" (").append(part).append(")");
        }

        return stringBuilder.toString();

    }
}