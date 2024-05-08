package io.skyvoli.goodbooks.storage.database.dto;

import java.util.List;

public class SeriesWithBooks {

    private Series series;
    private List<Book> books;

    public SeriesWithBooks(Series series, List<Book> books) {
        this.series = series;
        this.books = books;
    }

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}