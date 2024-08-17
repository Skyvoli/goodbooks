package io.skyvoli.goodbooks.web.api;

import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.web.RequestHandler;

public class GoogleBooksApi implements BookApi {

    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=isbn:";


    @Override
    public Optional<Book> getBook(String isbn, int timeout) {
        String url = BASE_URL + isbn;
        RequestHandler requestHandler = new RequestHandler();
        Optional<JsonNode> requestNode = requestHandler.getJsonDocument(url, timeout);
        if (!requestNode.isPresent()) {
            return Optional.empty();
        }

        Optional<JsonNode> finalNode = requestHandler.getJsonDocument(
                requestNode.get().findValue("selfLink").textValue(), timeout);

        if (!finalNode.isPresent()) {
            return Optional.empty();
        }

        JsonNode volume = finalNode.get().findValue("volumeInfo");
        List<String> keys = new ArrayList<>();
        volume.fieldNames().forEachRemaining(keys::add);

        Map<String, JsonNode> map = new HashMap<>();
        keys.forEach(key -> map.put(key, volume.findValue(key)));

        String title = getString(map.get("title"), "Unbekannt");
        String part = title;
        String subtitle = getString(map.get("subtitle"), null);

        String[] partTest = title.split(",\\sVol\\.\\s");
        if (partTest.length >= 2) {
            title = partTest[0];
            part = partTest[1];
        }

        Drawable cover = loadImage(isbn, timeout);
        String authors = getString(map.get("authors"), "");

        Book book = new Book(title, subtitle, parseToInt(part), isbn, authors, cover, true, 0);

        return Optional.of(book);
    }

    @Override
    public Drawable loadImage(String isbn, int timeout) {
        return new RequestHandler().getFallbackImage(isbn, timeout);
    }

    private String getString(@Nullable JsonNode node, String defaultValue) {
        if (node == null) {
            return defaultValue;
        }

        if (node.isTextual()) {
            return node.textValue();
        }

        if (node.isArray()) {
            StringBuilder builder = new StringBuilder();
            node.forEach(jsonNode -> builder.append(jsonNode.textValue()));
            return builder.toString();
        }
        return defaultValue;

    }

    private Integer parseToInt(String part) {
        try {
            return Integer.parseInt(part);
        } catch (NumberFormatException e) {
            Log.e(getClass().getSimpleName(), "Not a integer");

            StringBuilder digits = new StringBuilder();

            for (char character : part.toCharArray()) {
                if (Character.isDigit(character)) {
                    digits.append(character);
                }
            }

            if (digits.length() == 0) {
                return null;
            }
            return Integer.valueOf(digits.toString());
        }
    }
}