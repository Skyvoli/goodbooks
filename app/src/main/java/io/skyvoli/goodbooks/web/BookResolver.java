package io.skyvoli.goodbooks.web;

import org.jsoup.nodes.Document;

import java.util.Optional;

import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.web.api.BookApi;
import io.skyvoli.goodbooks.web.api.DnbMarc21Api;

public class BookResolver {

    private final BookApi dnbApi = new DnbMarc21Api();

    public Book resolveBook(String isbn, int timeout) {
        Optional<Document> document = new RequestHandler().getDocument(dnbApi.buildUrl(isbn), timeout);
        if (!document.isPresent()) {
            return new Book(isbn);
        }

        Book book = dnbApi.serializeDocument(document.get(), isbn);
        book.setResolved(book.getCover().isPresent());
        return book;
    }
}