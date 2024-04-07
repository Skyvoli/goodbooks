package io.skyvoli.goodbooks.model;

import java.util.Objects;

public class Book {
    private final String name;
    private final String isbn;
    private boolean resolved;

    public Book(String name, String isbn, boolean resolved) {
        this.name = name;
        this.isbn = isbn;
        this.resolved = resolved;
    }

    public String getName() {
        return name;
    }

    public String getIsbn() {
        return isbn;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
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