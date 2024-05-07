package io.skyvoli.goodbooks.storage.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "series")
public class SeriesEntity {

    @PrimaryKey(autoGenerate = true)
    private long seriesId;
    @ColumnInfo(name = "title")
    private String title;

    public SeriesEntity(long seriesId, String title) {
        this.seriesId = seriesId;
        this.title = title;
    }

    public long getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(long seriesId) {
        this.seriesId = seriesId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}