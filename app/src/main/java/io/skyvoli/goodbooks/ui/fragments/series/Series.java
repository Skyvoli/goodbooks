package io.skyvoli.goodbooks.ui.fragments.series;

import android.graphics.drawable.Drawable;

import androidx.room.Ignore;

import java.util.Objects;

public class Series {

    private final String title;
    @Ignore
    private Drawable cover;
    private final int countedBooks;

    @Ignore
    public Series(String title, Drawable cover, int countedBooks) {
        this.title = title;
        this.cover = cover;
        this.countedBooks = countedBooks;
    }

    public Series(String title, int countedBooks) {
        this(title, null, countedBooks);
    }


    public String getTitle() {
        return title;
    }

    public Drawable getCover() {
        return cover;
    }

    public void setCover(Drawable cover) {
        this.cover = cover;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Series)) return false;
        Series series = (Series) o;
        return Objects.equals(title, series.title) && Objects.equals(cover, series.cover);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, cover);
    }

    public int getCountedBooks() {
        return countedBooks;
    }
}