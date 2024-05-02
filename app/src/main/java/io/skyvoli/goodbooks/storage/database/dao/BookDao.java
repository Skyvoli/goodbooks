package io.skyvoli.goodbooks.storage.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.ui.fragments.series.Series;

@Dao
public interface BookDao {
    @Query("SELECT * FROM books ORDER BY title, part")
    List<Book> getAll();

    @Query("SELECT title, COUNT(*) as countedBooks FROM books GROUP BY title")
    List<Series> getSeries();

    @Insert
    void insert(Book... books);

    @Update
    void update(Book book);

    @Delete
    void delete(Book book);
}