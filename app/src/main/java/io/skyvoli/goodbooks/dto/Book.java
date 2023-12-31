package io.skyvoli.goodbooks.dto;

import java.util.Objects;

public class Book {
    private final String name;
    private final String isbn;

    public Book(String name, String isbn) {
        this.name = name;
        this.isbn = isbn;
    }

    public String getName() {
        return name;
    }

    public String getIsbn() {
        return isbn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
}
