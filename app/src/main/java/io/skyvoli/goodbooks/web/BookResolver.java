package io.skyvoli.goodbooks.web;

import android.graphics.drawable.Drawable;

import java.util.Optional;

import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.web.api.BookApi;
import io.skyvoli.goodbooks.web.api.DnbMarc21Api;
import io.skyvoli.goodbooks.web.api.GoogleBooksApi;

public class BookResolver {

    private final BookApi dnbApi = new DnbMarc21Api();
    private final BookApi dnbApi2 = new GoogleBooksApi();

    public Book resolveBook(String isbn, int timeout) {
        Optional<Book> result = dnbApi.getBook(isbn, timeout);
        Optional<Book> result2 = dnbApi2.getBook(isbn, timeout);
        return result.orElseGet(() -> result2.orElseGet(() -> new Book(isbn)));
    }

    public Optional<Drawable> loadImage(String isbn, int timeout) {
        return Optional.ofNullable(dnbApi.loadImage(isbn, timeout));
    }
}