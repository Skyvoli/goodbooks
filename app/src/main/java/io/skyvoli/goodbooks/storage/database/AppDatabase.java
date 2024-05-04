package io.skyvoli.goodbooks.storage.database;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import io.skyvoli.goodbooks.storage.database.dao.BookDao;
import io.skyvoli.goodbooks.storage.database.dao.SeriesDao;
import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.storage.database.dto.Series;

@Database(entities = {Book.class, Series.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BookDao bookDao();

    public abstract SeriesDao seriesDao();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE books ADD COLUMN subtitle TEXT");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS series (seriesId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "title TEXT, author TEXT)");
        }
    };
}