package io.skyvoli.goodbooks.ui.fragments.series;

import android.graphics.drawable.Drawable;

import java.util.Objects;

public class Series {

    private String title;
    private Drawable cover;

    public Series(String title, Drawable cover) {
        this.title = title;
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public Drawable getCover() {
        return cover;
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
}