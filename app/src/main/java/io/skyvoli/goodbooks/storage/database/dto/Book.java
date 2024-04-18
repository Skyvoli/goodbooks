package io.skyvoli.goodbooks.storage.database.dto;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;
import java.util.Optional;

@Entity(tableName = "books")
public class Book {
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "part")
    private Integer part;

    @NonNull
    @PrimaryKey
    private final String isbn;
    @ColumnInfo(name = "author")
    private String author;
    //@ColumnInfo(name = "cover") too big
    @Ignore
    private Drawable cover;
    @ColumnInfo(name = "resolved")
    private boolean resolved;

    @Ignore
    public Book(@NonNull String isbn) {
        this.title = "Unbekannt";
        this.part = null;
        this.isbn = isbn;
        this.author = "Unbekannt";
        this.cover = null;
        this.resolved = false;
    }


    @Ignore
    public Book(String title, Integer part, @NonNull String isbn, String author, Drawable cover, boolean resolved) {
        this.title = title;
        this.part = part;
        this.isbn = isbn;
        this.author = author;
        this.cover = cover;
        this.resolved = resolved;
    }

    public Book(String title, Integer part, @NonNull String isbn, String author, boolean resolved) {
        this.title = title;
        this.part = part;
        this.isbn = isbn;
        this.author = author;
        cover = null;
        this.resolved = resolved;
    }

    public Book createClone() {
        return new Book(title, part, isbn, author, cover, resolved);
    }

    public String getTitle() {
        return title;
    }

    public Integer getPart() {
        return part;
    }

    @NonNull
    public String getIsbn() {
        return isbn;
    }

    public String getAuthor() {
        return author;
    }

    public Optional<Drawable> getCover() {
        return Optional.ofNullable(cover);
    }

    public boolean isResolved() {
        return resolved;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setPart(Integer part) {
        this.part = part;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public void setCover(Drawable cover) {
        this.cover = cover;
    }

    public boolean sameIsbn(String isbn) {
        return this.isbn.equals(isbn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return Objects.equals(title, book.title) && Objects.equals(part, book.part) && Objects.equals(isbn, book.isbn) && Objects.equals(author, book.author) && Objects.equals(cover, book.cover);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, part, isbn, author, cover);
    }
}