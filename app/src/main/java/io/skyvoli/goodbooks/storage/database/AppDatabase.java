package io.skyvoli.goodbooks.storage.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import io.skyvoli.goodbooks.storage.database.dao.BookDao;
import io.skyvoli.goodbooks.storage.database.dto.Book;

@Database(entities = {Book.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BookDao bookDao();
}