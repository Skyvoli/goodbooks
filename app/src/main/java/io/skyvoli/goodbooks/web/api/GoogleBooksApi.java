package io.skyvoli.goodbooks.web.api;

import android.graphics.drawable.Drawable;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

        String selfLink = requestNode.get().findValue("selfLink").textValue();
        Optional<JsonNode> finalNode = requestHandler.getJsonDocument(selfLink, timeout);

        if (!finalNode.isPresent()) {
            return Optional.empty();
        }

        JsonNode volume = finalNode.get().findValue("volumeInfo");
        List<String> keys = new ArrayList<>();
        volume.fieldNames().forEachRemaining(keys::add);

        Map<String, JsonNode> map = new HashMap<>();
        keys.forEach(key -> map.put(key, volume.findValue(key)));

        String title = Objects.requireNonNull(map.get("title")).asText();
        String number = title.substring(title.lastIndexOf(" ") + 1);
        //JsonNode imageLink = map.get("imageLinks");
        //String publisher = Objects.requireNonNull(map.get("publisher")).asText();
        //TODO ArrayNode
        //String authors = Objects.requireNonNull(map.get("authors")).textValue();

        Book book = new Book(title, "", Integer.parseInt(number), isbn, "", null, true, 0);


        return Optional.of(book);
    }

    @Override
    public Drawable loadImage(String isbn, int timeout) {
        return null;
    }
}