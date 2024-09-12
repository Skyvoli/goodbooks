package io.skyvoli.goodbooks.web;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import java.util.Optional;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.web.api.BookApi;
import io.skyvoli.goodbooks.web.api.DnbMarc21Api;
import io.skyvoli.goodbooks.web.api.GoogleBooksApi;

public class BookResolver {

    private final BookApi dnbApi = new DnbMarc21Api();
    private final BookApi dnbApi2 = new GoogleBooksApi();
    private final Drawable fallbackImage;

    public BookResolver(Context context) {
        fallbackImage = ContextCompat.getDrawable(context, R.drawable.ruby);
    }

    public Book resolveBook(String isbn, int timeout) {
        Optional<Book> result = dnbApi.getBook(isbn, timeout);
        Optional<Drawable> cover = dnbApi.loadImage(isbn, timeout);

        Book book = result.orElseGet(() -> dnbApi2.getBook(isbn, timeout).orElseGet(() -> new Book(isbn)));
        book.setCover(cover.orElseGet(() -> dnbApi2.loadImage(isbn, timeout).orElse(fallbackImage)));
        return book;
    }

    public Optional<Drawable> loadImage(String isbn, int timeout) {
        return dnbApi.loadImage(isbn, timeout);
    }
}