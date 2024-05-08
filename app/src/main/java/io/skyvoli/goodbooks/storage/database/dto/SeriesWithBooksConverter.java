package io.skyvoli.goodbooks.storage.database.dto;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;
import java.util.stream.Collectors;

import io.skyvoli.goodbooks.storage.FileStorage;
import io.skyvoli.goodbooks.storage.database.entities.BookEntity;
import io.skyvoli.goodbooks.storage.database.entities.SeriesEntity;

public class SeriesWithBooksConverter {
    @Embedded
    private SeriesEntity seriesEntity;
    @Relation(
            parentColumn = "seriesId",
            entityColumn = "seriesId"
    )
    private final List<BookEntity> bookEntities;

    public SeriesWithBooksConverter(SeriesEntity seriesEntity, List<BookEntity> bookEntities) {
        this.seriesEntity = seriesEntity;
        this.bookEntities = bookEntities;

    }

    public SeriesWithBooks convert(FileStorage fileStorage) {
        Series series = new Series(seriesEntity.getSeriesId(), seriesEntity.getTitle(),
                fileStorage.getImage(bookEntities.get(0).getIsbn()), bookEntities.size());
        List<Book> books = bookEntities.stream().map(bookEntity ->
                new Book(seriesEntity.getTitle(), bookEntity.getSubtitle(),
                        bookEntity.getPart(), bookEntity.getIsbn(),
                        bookEntity.getAuthor(),
                        fileStorage.getImage(bookEntity.getIsbn()),
                        bookEntity.isResolved())).collect(Collectors.toList());

        return new SeriesWithBooks(series, books);
    }
}