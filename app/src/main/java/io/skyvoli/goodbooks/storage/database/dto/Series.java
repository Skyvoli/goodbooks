package io.skyvoli.goodbooks.storage.database.dto;

import android.graphics.drawable.Drawable;

import androidx.room.Ignore;

import io.skyvoli.goodbooks.storage.database.entities.SeriesEntity;

public class Series {
    private long seriesId;
    private String title;
    @Ignore
    private Drawable cover;
    private int countedBooks;

    @Ignore
    public Series(long seriesId, String title, Drawable cover, int countedBooks) {
        this.seriesId = seriesId;
        this.title = title;
        this.cover = cover;
        this.countedBooks = countedBooks;
    }

    public Series(long seriesId, String title, int countedBooks) {
        this(seriesId, title, null, countedBooks);
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

    public void setSeriesId(long seriesId) {
        this.seriesId = seriesId;
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

    public SeriesEntity getEntity() {
        return new SeriesEntity(seriesId, title);
    }
}