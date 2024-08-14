package io.skyvoli.goodbooks.web.api;

import android.graphics.drawable.Drawable;

import org.jsoup.nodes.Document;

import io.skyvoli.goodbooks.storage.database.dto.Book;

public class GoogleBooksApi implements BookApi {

    private static final String URL = "https://www.googleapis.com/books/v1/volumes?q=isbn:";


    @Override
    public String buildUrl(String isbn) {
        return URL + isbn;
    }

    @Override
    public Book serializeDocument(Document document, String isbn, int timeout) {
        return null;
    }

    @Override
    public Drawable loadImage(String isbn, int timeout) {
        return null;
    }
}