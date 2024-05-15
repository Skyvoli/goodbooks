package io.skyvoli.goodbooks.storage.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.skyvoli.goodbooks.storage.database.dto.Series;
import io.skyvoli.goodbooks.storage.database.entities.SeriesEntity;

@Dao
public interface SeriesDao {

    @Query("SELECT s.*, COUNT(*) as countedBooks FROM series s INNER JOIN books b ON s.seriesId = b.seriesId GROUP BY s.title ORDER BY s.title")
    List<Series> getSeries();

    @Query("SELECT s.*, COUNT(*) as countedBooks FROM series s INNER JOIN books b ON s.seriesId = b.seriesId WHERE s.seriesId = :seriesId GROUP BY s.title ORDER BY s.title")
    Series getSeriesById(long seriesId);

    @Query("SELECT * FROM series WHERE seriesId = :seriesId")
    SeriesEntity getSeriesEntityById(long seriesId);

    @Query("SELECT * FROM series WHERE title = :title")
    List<SeriesEntity> getSeriesDtoByTitle(String title);

    @Query("SELECT COUNT(*) as countedBooks FROM books WHERE seriesId LIKE :seriesId")
    int getCountOfSeries(long seriesId);

    @Insert
    long insert(SeriesEntity series);

    @Update
    void update(SeriesEntity series);

    @Query("DELETE FROM series WHERE seriesId = :seriesId")
    void delete(long seriesId);
}