package io.skyvoli.goodbooks.storage.database.dto;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;
import java.util.Optional;

@Entity(tableName = "books",
        foreignKeys = @ForeignKey(entity = Series.class,
                parentColumns = "seriesId",
                childColumns = "seriesId",
                onDelete = ForeignKey.RESTRICT))
public class Book {
    @NonNull
    @PrimaryKey
    private final String isbn;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "subtitle")
    private String subtitle;
    @ColumnInfo(name = "part")
    private Integer part;
    @ColumnInfo(name = "author")
    private String author;
    //@ColumnInfo(name = "cover") too big
    @Ignore
    private Drawable cover;
    @ColumnInfo(name = "resolved")
    private boolean resolved;

    @ColumnInfo(name = "seriesId")
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
    }


    @Ignore
    public Book(String title, String subtitle, Integer part, @NonNull String isbn, String author, Drawable cover, boolean resolved) {
        this.title = title;
        this.subtitle = subtitle;
        this.part = part;
        this.isbn = isbn;
        this.author = author;
        this.cover = cover;
        this.resolved = resolved;
    }

    public Book(String title, String subtitle, Integer part, @NonNull String isbn, String author, boolean resolved, long seriesId) {
        this.title = title;
        this.subtitle = subtitle;
        this.part = part;
        this.isbn = isbn;
        this.author = author;
        cover = null;
        this.resolved = resolved;
        this.seriesId = seriesId;
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
        return new Book(title, subtitle, part, isbn, author, cover, resolved);
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
}