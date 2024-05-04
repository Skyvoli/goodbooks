package io.skyvoli.goodbooks.storage.database.dto;

import android.graphics.drawable.Drawable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "series")
public class Series {

    @PrimaryKey(autoGenerate = true)
    private int seriesId;
    @ColumnInfo(name = "title")
    private String title;
    @Ignore
    private Drawable cover;
    @ColumnInfo(name = "author")
    private String author;
    @Ignore
    private int countedBooks;

    @Ignore
    public Series(int seriesId, String title, Drawable cover, String author, int countedBooks) {
        this.seriesId = seriesId;
        this.title = title;
        this.cover = cover;
        this.author = author;
        this.countedBooks = countedBooks;
    }

    public Series(int seriesId, String title, String author) {
        this(seriesId, title, null, author, 1);
    }

    public int getSeriesId() {
        return seriesId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Drawable getCover() {
        return cover;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCover(Drawable cover) {
        this.cover = cover;
    }

    public void setCountedBooks(int countedBooks) {
        this.countedBooks = countedBooks;
    }

    public int getCountedBooks() {
        return countedBooks;
    }
}