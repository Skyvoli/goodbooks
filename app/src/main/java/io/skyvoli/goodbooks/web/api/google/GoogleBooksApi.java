package io.skyvoli.goodbooks.web.api.google;

import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.web.RequestHandler;
import io.skyvoli.goodbooks.web.api.BookApi;

public class GoogleBooksApi implements BookApi {

    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=isbn:";
    private final RequestHandler requestHandler = new RequestHandler();
    private final String logTag = getClass().getSimpleName();

    @Override
    public Optional<Book> getBook(String isbn, int timeout) {
        Optional<JsonNode> requestNode = requestHandler.getJsonDocument(BASE_URL + isbn, timeout);
        if (!requestNode.isPresent()) {
            return Optional.empty();
        }

        Optional<JsonNode> selfLink = Optional.ofNullable(requestNode.get().findValue("selfLink"));

        if (!selfLink.isPresent()) {
            Log.i(logTag, "ISBN not found: " + isbn);
            return Optional.empty();
        }

        Optional<JsonNode> finalNode = requestHandler.getJsonDocument(selfLink.get().textValue(), timeout);
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

        String[] partTest = title.split(",?\\sVol\\.\\s");
        if (partTest.length == 2) {
            title = partTest[0];
            part = partTest[1];
        } else {
            List<String> titleParts = Arrays.stream(title.split("\\s")).collect(Collectors.toList());
            ListIterator<String> iterator = titleParts.listIterator(titleParts.size());

            while (iterator.hasPrevious()) {
                try {
                    String nextString = iterator.previous();
                    Integer.parseInt(nextString);
                    part = nextString;
                    title = title.split("\\s" + part)[0];
                    break;
                } catch (NumberFormatException ignore) {
                    //ignored
                }
            }
        }
        title = removeUnwantedSequencesFromTitle(title);

        String authors = getFromStringList(map.get("authors"));

        return Optional.of(new Book(title, subtitle, parseToInt(part), isbn, authors, true, 0));
    }

    private String removeUnwantedSequencesFromTitle(String title) {
        return title.replaceAll("\\s-\\sBand", "");
    }

    @Override
    public Optional<Drawable> loadImage(String isbn, int timeout) {
        return requestHandler.getFallbackImage(isbn, timeout);
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

    private String getFromStringList(@Nullable JsonNode node) {
        if (node == null) {
            return "";
        }

        if (node.isArray()) {
            StringBuilder builder = new StringBuilder();

            Iterator<JsonNode> iterator = node.iterator();

            if (iterator.hasNext()) {
                builder.append(iterator.next().textValue());
            } else {
                return "";
            }

            while (iterator.hasNext()) {
                builder.append(System.lineSeparator()).append(iterator.next().textValue());
            }
            return builder.toString();
        }

        if (node.isTextual()) {
            return node.textValue();
        }

        return "";
    }

    @Nullable
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