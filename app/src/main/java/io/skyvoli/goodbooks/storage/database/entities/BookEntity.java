package io.skyvoli.goodbooks.storage.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "books",
        foreignKeys = @ForeignKey(entity = SeriesEntity.class,
                parentColumns = "seriesId",
                childColumns = "seriesId",
                onDelete = ForeignKey.RESTRICT))
public class BookEntity {

    @NonNull
    @PrimaryKey
    private String isbn;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "subtitle")
    private String subtitle;
    @ColumnInfo(name = "part")
    private Integer part;
    @ColumnInfo(name = "author")
    private String author;
    @ColumnInfo(name = "resolved")
    private boolean resolved;
    @ColumnInfo(name = "seriesId")
    private long seriesId;


    public BookEntity(String title, String subtitle, Integer part, @NonNull String isbn, String author, boolean resolved, long seriesId) {
        this.title = title;
        this.subtitle = subtitle;
        this.part = part;
        this.isbn = isbn;
        this.author = author;
        this.resolved = resolved;
        this.seriesId = seriesId;
    }

    @NonNull
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(@NonNull String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Integer getPart() {
        return part;
    }

    public void setPart(Integer part) {
        this.part = part;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public long getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(long seriesId) {
        this.seriesId = seriesId;
    }
}