package io.skyvoli.goodbooks.web.api;

import android.graphics.drawable.Drawable;

import org.jsoup.nodes.Document;

import io.skyvoli.goodbooks.storage.database.dto.Book;

public interface BookApi {

    String buildUrl(String isbn);

    Book serializeDocument(Document document, String isbn, int timeout);

    Drawable loadImage(String isbn, int timeout);
}