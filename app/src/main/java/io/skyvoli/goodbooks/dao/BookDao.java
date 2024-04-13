package io.skyvoli.goodbooks.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.skyvoli.goodbooks.model.Book;

@Dao
public interface BookDao {
    @Query("SELECT * FROM books")
    List<Book> getAll();

    @Insert
    void insert(Book... books);
}