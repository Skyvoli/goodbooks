package io.skyvoli.goodbooks.helper;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.skyvoli.goodbooks.exception.BookNotFound;
import io.skyvoli.goodbooks.model.Book;
import io.skyvoli.goodbooks.web.RequestHandler;
import io.skyvoli.goodbooks.web.api.BookApi;
import io.skyvoli.goodbooks.web.api.DnbMarc21Api;

public class BookResolver {

    private final BookApi dnbApi = new DnbMarc21Api();

    public Book resolveBook(String isbn) {
        String defaultName = "Titel";
        String defaultAuthor = "Autor";
        Optional<Document> document = new RequestHandler(dnbApi.buildUrl(isbn)).invoke();

        if (!document.isPresent()) {
            return new Book(defaultName, isbn, defaultAuthor, null, false);
        }
        List<Book> books = new ArrayList<>();
        try {
            books.addAll(dnbApi.serializeDocument(document.get(), isbn));
        } catch (BookNotFound e) {
            books.add(new Book("Unbekannt", isbn, "Unbekannt", null, false));
        }

        //TODO better
        return books.get(0);
    }
}