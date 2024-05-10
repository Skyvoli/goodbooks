package io.skyvoli.goodbooks.storage.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.skyvoli.goodbooks.storage.database.entities.BookEntity;

@Dao
public interface BookDao {
    @Query("SELECT * FROM books ORDER BY title, part")
    List<BookEntity> getAll();

    @Query("SELECT * FROM books WHERE seriesId = :seriesId ORDER BY title, part")
    List<BookEntity> getBooksFromSeries(long seriesId);

    @Query("SELECT * FROM books WHERE isbn = :isbn")
    BookEntity getBookByIsbn(String isbn);

    @Insert
    void insert(BookEntity... books);

    @Update
    void update(BookEntity book);

    @Delete
    void delete(BookEntity book);
}