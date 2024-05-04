package io.skyvoli.goodbooks.storage.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.skyvoli.goodbooks.storage.database.dto.Series;

@Dao
public interface SeriesDao {

    @Query("SELECT 0 as seriesId, title, author FROM books GROUP BY title")
    List<Series> getSeries();

    @Query("SELECT * FROM series ORDER BY title")
    List<Series> getSeriesDto();

    @Query("SELECT * FROM series WHERE title LIKE :title")
    List<Series> getSeriesDtoByTitle(String title);

    @Query("SELECT COUNT(*) as countedBooks FROM books WHERE title LIKE :title")
    int getCountOfSeries(String title);

    @Insert
    void insert(Series... series);

    @Update
    void update(Series series);

    @Delete
    void delete(Series series);
}