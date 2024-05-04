package io.skyvoli.goodbooks.storage.database.dto;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "series")
public class SeriesDto {

    @PrimaryKey(autoGenerate = true)
    private final int seriesId;

    @ColumnInfo(name = "title")
    private final String title;
    @ColumnInfo(name = "author")
    private final String author;

    public SeriesDto(int seriesId, String title, String author) {
        this.seriesId = seriesId;
        this.title = title;
        this.author = author;
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

}