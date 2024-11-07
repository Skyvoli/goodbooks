package io.skyvoli.goodbooks.helper;

public class ISBNChecker {

    private ISBNChecker() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isbnIsNotBook(String isbn) {
        return !isbnIsBook(isbn);
    }

    public static boolean isbnIsBook(String isbn) {
        String prefix = isbn.substring(0, 3);
        return isbn.toCharArray().length == 13 && (prefix.equals("978") || prefix.equals("979"));
    }
}