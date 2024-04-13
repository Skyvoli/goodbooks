package io.skyvoli.goodbooks;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import io.skyvoli.goodbooks.dao.BookDao;
import io.skyvoli.goodbooks.model.Book;
import io.skyvoli.goodbooks.serializer.Converter;

@Database(entities = {Book.class}, version = 1)
@TypeConverters({Converter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract BookDao bookDao();
}