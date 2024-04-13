package io.skyvoli.goodbooks.web;

import org.jsoup.nodes.Document;

import java.util.Optional;

import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.web.api.BookApi;
import io.skyvoli.goodbooks.web.api.DnbMarc21Api;

public class BookResolver {

    private final BookApi dnbApi = new DnbMarc21Api();

    public Book resolveBook(String isbn) {
        Optional<Document> document = new RequestHandler().getDocument(dnbApi.buildUrl(isbn));
        if (!document.isPresent()) {
            return new Book(isbn);
        }
        return dnbApi.serializeDocument(document.get(), isbn);
    }
}