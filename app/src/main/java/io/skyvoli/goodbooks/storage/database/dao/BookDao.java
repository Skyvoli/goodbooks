package io.skyvoli.goodbooks.storage.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.storage.database.entities.BookEntity;

@Dao
public interface BookDao {
    @Query("SELECT s.title, b.* FROM books b INNER JOIN series s ON b.seriesId = s.seriesId ORDER BY title, part")
    List<Book> getAll();

    @Query("SELECT s.title, b.* FROM books b INNER JOIN series s ON b.seriesId = s.seriesId WHERE b.seriesId = :seriesId ORDER BY title, part")
    List<Book> getBooksFromSeries(long seriesId);

    @Query("SELECT s.title, b.* FROM books b INNER JOIN series s ON b.seriesId = s.seriesId WHERE isbn = :isbn")
    Book getBookByIsbn(String isbn);

    @Insert
    void insert(BookEntity... books);

    @Update
    void update(BookEntity book);

    @Delete
    void delete(BookEntity book);
}