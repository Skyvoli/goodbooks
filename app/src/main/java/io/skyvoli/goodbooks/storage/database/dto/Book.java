package io.skyvoli.goodbooks.storage.database.dto;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Ignore;

import java.util.Objects;
import java.util.Optional;

import io.skyvoli.goodbooks.storage.database.entities.BookEntity;

public class Book {
    private final String isbn;
    private String title;
    private String subtitle;
    private Integer part;
    private String author;
    @Ignore
    private Drawable cover;
    private boolean resolved;
    private long seriesId;

    @Ignore
    public Book(@NonNull String isbn) {
        this.title = "Unbekannt";
        this.subtitle = null;
        this.part = null;
        this.isbn = isbn;
        this.author = "Unbekannt";
        this.cover = null;
        this.resolved = false;
        this.seriesId = 0;
    }

    @Ignore
    public Book(String title, String subtitle, Integer part, @NonNull String isbn, String author, Drawable cover, boolean resolved, long seriesId) {
        this.title = title;
        this.subtitle = subtitle;
        this.part = part;
        this.isbn = isbn;
        this.author = author;
        this.cover = cover;
        this.resolved = resolved;
        this.seriesId = seriesId;
    }

    public Book(String title, String subtitle, Integer part, @NonNull String isbn, String author, boolean resolved, long seriesId) {
        this.title = title;
        this.subtitle = subtitle;
        this.part = part;
        this.isbn = isbn;
        this.author = author;
        this.cover = null;
        this.resolved = resolved;
        this.seriesId = seriesId;
    }

    public String buildTitle() {
        StringBuilder stringBuilder = new StringBuilder(title);
        if (subtitle != null) {
            stringBuilder.append(" - ").append(subtitle);
        } else if (part != null) {
            stringBuilder.append(" ").append(part);
        }

        return stringBuilder.toString();

    }

    public String buildCompleteTitle() {
        StringBuilder stringBuilder = new StringBuilder(title);
        if (subtitle != null) {
            stringBuilder.append(" - ").append(subtitle);
        }
        if (part != null) {
            stringBuilder.append(" (").append(part).append(")");
        }

        return stringBuilder.toString();

    }

    public int comparePart(Integer otherPart) {
        if (part == null && otherPart == null)
            return 0;
        else if (part == null)
            return 1;
        else if (otherPart == null)
            return -1;
        else
            return Integer.compare(part, otherPart);
    }

    public Book createClone() {
        return new Book(title, subtitle, part, isbn, author, cover, resolved, seriesId);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPart() {
        return part;
    }

    public void setPart(Integer part) {
        this.part = part;
    }

    @NonNull
    public String getIsbn() {
        return isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Optional<Drawable> getCover() {
        return Optional.ofNullable(cover);
    }

    public @Nullable Drawable getNullableCover() {
        return cover;
    }

    public void setCover(Drawable cover) {
        this.cover = cover;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public boolean sameIsbn(String isbn) {
        return this.isbn.equals(isbn);
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return resolved == book.resolved && Objects.equals(isbn, book.isbn) && Objects.equals(title, book.title) && Objects.equals(subtitle, book.subtitle) && Objects.equals(part, book.part) && Objects.equals(author, book.author) && Objects.equals(cover, book.cover);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn, title, subtitle, part, author, cover, resolved);
    }

    public long getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(long seriesId) {
        this.seriesId = seriesId;
    }

    public BookEntity getEntity() {
        return new BookEntity(subtitle, part, isbn, author, resolved, seriesId);
    }
}