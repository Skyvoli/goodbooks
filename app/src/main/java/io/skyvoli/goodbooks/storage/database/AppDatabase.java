package io.skyvoli.goodbooks.storage.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import io.skyvoli.goodbooks.storage.database.dao.BookDao;
import io.skyvoli.goodbooks.storage.database.dao.SeriesDao;
import io.skyvoli.goodbooks.storage.database.entities.BookEntity;
import io.skyvoli.goodbooks.storage.database.entities.SeriesEntity;

@Database(entities = {BookEntity.class, SeriesEntity.class}, version = 7)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BookDao bookDao();

    public abstract SeriesDao seriesDao();
}