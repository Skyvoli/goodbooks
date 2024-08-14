package io.skyvoli.goodbooks.web;

import android.graphics.drawable.Drawable;

import org.jsoup.nodes.Document;

import java.util.Optional;

import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.web.api.BookApi;
import io.skyvoli.goodbooks.web.api.DnbMarc21Api;
import io.skyvoli.goodbooks.web.api.GoogleBooksApi;

public class BookResolver {

    private final BookApi dnbApi = new DnbMarc21Api();
    private final BookApi dnbApi2 = new GoogleBooksApi();

    public Book resolveBook(String isbn, int timeout) {
        Optional<Document> document = new RequestHandler().getDocument(dnbApi.buildUrl(isbn), timeout);
        Optional<Document> document2 = new RequestHandler().getDocument(dnbApi2.buildUrl(isbn), timeout);
        if (!document.isPresent()) {
            return new Book(isbn);
        }

        Book book = dnbApi.serializeDocument(document.get(), isbn, timeout);
        book.setCover(dnbApi.loadImage(isbn, timeout));
        return book;
    }

    public Optional<Drawable> loadImage(String isbn, int timeout) {
        return Optional.ofNullable(dnbApi.loadImage(isbn, timeout));
    }
}