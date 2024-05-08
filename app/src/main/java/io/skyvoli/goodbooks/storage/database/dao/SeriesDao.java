package io.skyvoli.goodbooks.storage.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import io.skyvoli.goodbooks.storage.database.dto.SeriesWithBooksConverter;
import io.skyvoli.goodbooks.storage.database.entities.SeriesEntity;

@Dao
public interface SeriesDao {

    @Query("SELECT * FROM series ORDER BY title")
    List<SeriesEntity> getSeriesDto();

    @Transaction
    @Query("SELECT s.*, b.* FROM series s INNER JOIN books b ON s.seriesId = b.seriesId GROUP BY s.title ORDER BY b.title")
    List<SeriesWithBooksConverter> getSeriesWithBooks();

    @Query("SELECT * FROM series WHERE title LIKE :title")
    List<SeriesEntity> getSeriesDtoByTitle(String title);

    @Query("SELECT COUNT(*) as countedBooks FROM books WHERE title LIKE :title")
    int getCountOfSeries(String title);

    @Insert
    long insert(SeriesEntity series);

    @Update
    void update(SeriesEntity series);

    @Delete
    void delete(SeriesEntity series);
}