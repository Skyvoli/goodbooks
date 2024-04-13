package io.skyvoli.goodbooks.model;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

import io.skyvoli.goodbooks.serializer.BookDeserializer;
import io.skyvoli.goodbooks.serializer.BookSerializer;

@JsonSerialize(using = BookSerializer.class)
@JsonDeserialize(using = BookDeserializer.class)
@Entity(tableName = "books")
public class Book {
    @ColumnInfo(name = "title")
    private final String title;
    @ColumnInfo(name = "part")
    private final String part;

    @NonNull
    @PrimaryKey
    private final String isbn;
    @ColumnInfo(name = "author")
    private final String author;
    //@ColumnInfo(name = "cover") too big
    @Ignore
    private Drawable cover;
    @ColumnInfo(name = "resolved")
    private boolean resolved;

    @Ignore
    public Book(String isbn) {
        this.title = "Unbekannt";
        this.part = "";
        this.isbn = isbn;
        this.author = "Unbekannt";
        this.cover = null;
        this.resolved = false;
    }


    @Ignore
    @JsonCreator
    public Book(String title, String part, String isbn, String author, Drawable cover, boolean resolved) {
        this.title = title;
        this.part = part;
        this.isbn = isbn;
        this.author = author;
        this.cover = cover;
        this.resolved = resolved;
    }

    public Book(String title, String part, String isbn, String author, boolean resolved) {
        this.title = title;
        this.part = part;
        this.isbn = isbn;
        this.author = author;
        //get From files this.cover = cover;
        cover = null;
        this.resolved = resolved;
    }

    public String getTitle() {
        return title;
    }

    public String getPart() {
        return part;
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

    public void setCover(Drawable cover) {
        this.cover = cover;
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