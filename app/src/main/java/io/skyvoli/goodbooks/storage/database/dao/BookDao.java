package io.skyvoli.goodbooks.storage.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.skyvoli.goodbooks.storage.database.dto.Book;

@Dao
public interface BookDao {
    @Query("SELECT * FROM books ORDER BY title")
    List<Book> getAll();

    @Insert
    void insert(Book... books);
}