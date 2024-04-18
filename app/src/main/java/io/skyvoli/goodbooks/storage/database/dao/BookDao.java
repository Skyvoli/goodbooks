package io.skyvoli.goodbooks.storage.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.skyvoli.goodbooks.storage.database.dto.Book;

@Dao
public interface BookDao {
    @Query("SELECT * FROM books ORDER BY title, part")
    List<Book> getAll();

    @Insert
    void insert(Book... books);

    @Update
    void update(Book book);
}