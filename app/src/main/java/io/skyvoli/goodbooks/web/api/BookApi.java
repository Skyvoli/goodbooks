package io.skyvoli.goodbooks.web.api;

import org.jsoup.nodes.Document;

import io.skyvoli.goodbooks.exception.BookNotFound;
import io.skyvoli.goodbooks.model.Book;

public interface BookApi {

    String buildUrl(String isbn);

    Book serializeDocument(Document document, String isbn) throws BookNotFound;
}