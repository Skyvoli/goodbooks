package io.skyvoli.goodbooks.web.api;

import android.graphics.drawable.Drawable;

import java.util.Optional;

import io.skyvoli.goodbooks.storage.database.dto.Book;

public interface BookApi {

    Optional<Book> getBook(String isbn, int timeout);

    Drawable loadImage(String isbn, int timeout);
}