package io.skyvoli.goodbooks.model;

import android.graphics.drawable.Drawable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Book {
    private final String name;
    private final String isbn;
    private final String author;
    private final Drawable cover;
    private boolean resolved;

    @JsonCreator
    public Book(@JsonProperty("name") String name, @JsonProperty("isbn") String isbn, @JsonProperty("author") String author, @JsonProperty("cover") Drawable cover, @JsonProperty("resolved") boolean resolved) {
        this.name = name;
        this.isbn = isbn;
        this.author = author;
        this.cover = cover;
        this.resolved = resolved;
    }

    public String getName() {
        return name;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getAuthor() {
        return author;
    }

    public Drawable getCover() {
        return cover;
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