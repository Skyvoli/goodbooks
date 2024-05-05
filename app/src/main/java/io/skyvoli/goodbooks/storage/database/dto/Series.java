package io.skyvoli.goodbooks.storage.database.dto;

import android.graphics.drawable.Drawable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "series")
public class Series {

    @PrimaryKey(autoGenerate = true)
    private long seriesId = 0;
    @ColumnInfo(name = "title")
    private String title;
    @Ignore
    private Drawable cover;
    @Ignore
    private int countedBooks;

    @Ignore
    public Series(String title, Drawable cover, int countedBooks) {
        this.title = title;
        this.cover = cover;
        this.countedBooks = countedBooks;
    }

    public Series(long seriesId, String title) {
        this(title, null, 1);
        this.seriesId = seriesId;
    }

    public long getSeriesId() {
        return seriesId;
    }

    public String getTitle() {
        return title;
    }

    public Drawable getCover() {
        return cover;
    }

    public void setTitle(String title) {
        this.title = title;
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