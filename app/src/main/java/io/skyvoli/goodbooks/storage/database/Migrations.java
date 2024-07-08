package io.skyvoli.goodbooks.storage.database;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@SuppressWarnings({"unused", "squid:S1192"})
public class Migrations {

    private Migrations() {
        throw new IllegalArgumentException();
    }

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

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE series");
            database.execSQL("CREATE TABLE IF NOT EXISTS series (seriesId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "title TEXT, author TEXT)");
            database.execSQL("INSERT INTO series (title, author) SELECT title, author FROM books GROUP BY title");
            database.execSQL("ALTER TABLE books ADD COLUMN seriesId INTEGER");
            database.execSQL("CREATE TABLE IF NOT EXISTS newBooks " +
                    "(isbn TEXT NOT NULL, " +
                    "title TEXT, " +
                    "subtitle TEXT, " +
                    "part INTEGER, " +
                    "author TEXT, " +
                    "resolved INTEGER NOT NULL, " +
                    "seriesId INTEGER NOT NULL, " +
                    "PRIMARY KEY(isbn), " +
                    "FOREIGN KEY(seriesId) REFERENCES series(seriesId) ON UPDATE NO ACTION ON DELETE RESTRICT)");
            database.execSQL("INSERT INTO newBooks (isbn, title, subtitle, part, author, resolved, seriesId) SELECT isbn, title, subtitle, part, author, resolved, 1 as seriesId FROM books");
            database.execSQL("DROP TABLE books");
            database.execSQL("ALTER TABLE newBooks RENAME TO books");
            database.execSQL("UPDATE books SET seriesId = (SELECT s.seriesId FROM books, series s WHERE books.title LIKE s.title)");

        }
    };

    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS newSeries (seriesId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "title TEXT)");
            database.execSQL("INSERT INTO newSeries (seriesId, title) SELECT seriesId, title FROM series");
            database.execSQL("DROP TABLE series");
            database.execSQL("ALTER TABLE newSeries RENAME TO series");
        }
    };

    public static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS newSeries (seriesId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "title TEXT COLLATE NOCASE)");
            database.execSQL("INSERT INTO newSeries (seriesId, title) SELECT seriesId, title FROM series");
            database.execSQL("DROP TABLE series");
            database.execSQL("ALTER TABLE newSeries RENAME TO series");


            database.execSQL("CREATE TABLE IF NOT EXISTS newBooks " +
                    "(isbn TEXT NOT NULL, " +
                    "title TEXT COLLATE NOCASE, " +
                    "subtitle TEXT COLLATE NOCASE, " +
                    "part INTEGER, " +
                    "author TEXT COLLATE NOCASE, " +
                    "resolved INTEGER NOT NULL, " +
                    "seriesId INTEGER NOT NULL, " +
                    "PRIMARY KEY(isbn), " +
                    "FOREIGN KEY(seriesId) REFERENCES series(seriesId) ON UPDATE NO ACTION ON DELETE RESTRICT)");

            database.execSQL("INSERT INTO newBooks (isbn, title, subtitle, part, author, resolved, seriesId) SELECT isbn, title, subtitle, part, author, resolved, seriesId FROM books");
            database.execSQL("DROP TABLE books");
            database.execSQL("ALTER TABLE newBooks RENAME TO books");
        }
    };

    public static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS newBooks " +
                    "(isbn TEXT NOT NULL, " +
                    "subtitle TEXT COLLATE NOCASE, " +
                    "part INTEGER, " +
                    "author TEXT COLLATE NOCASE, " +
                    "resolved INTEGER NOT NULL, " +
                    "seriesId INTEGER NOT NULL, " +
                    "PRIMARY KEY(isbn), " +
                    "FOREIGN KEY(seriesId) REFERENCES series(seriesId) ON UPDATE NO ACTION ON DELETE RESTRICT)");

            database.execSQL("INSERT INTO newBooks (isbn, subtitle, part, author, resolved, seriesId) SELECT isbn, subtitle, part, author, resolved, seriesId FROM books");
            database.execSQL("DROP TABLE books");
            database.execSQL("ALTER TABLE newBooks RENAME TO books");
        }
    };
}