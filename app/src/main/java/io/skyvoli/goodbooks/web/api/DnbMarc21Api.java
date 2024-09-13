package io.skyvoli.goodbooks.web.api;

import android.graphics.drawable.Drawable;
import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.web.RequestHandler;

public class DnbMarc21Api implements BookApi {

    private static final String BASE_URL = "https://services.dnb.de/sru/dnb?version=1.1";
    private static final String OPERATION = "&operation=searchRetrieve";
    private static final String QUERY = "&query=";
    private static final String MAXIMUM_RECORDS = "&maximumRecords=";
    private static final String RECORD_SCHEMA = "&recordSchema=MARC21-xml";
    private static final String IMAGE_URL = "https://portal.dnb.de/opac/mvb/cover?isbn=";
    private final RequestHandler requestHandler = new RequestHandler();


    @Override
    public Optional<Book> getBook(String isbn, int timeout) {
        String url = BASE_URL + OPERATION + QUERY + isbn + MAXIMUM_RECORDS + 1 + RECORD_SCHEMA;
        Optional<Document> doc = requestHandler.getDocument(url, timeout);

        if (!doc.isPresent()) {
            return Optional.empty();
        }

        Document document = doc.get();

        Element bookData;
        try {
            bookData = document.getElementsByAttributeValueContaining("type", "Bibliographic").get(0);
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }

        List<XmlField> titleFields = new ArrayList<>();
        List<XmlField> subtitleFields = new ArrayList<>();
        List<XmlField> partFields = new ArrayList<>();
        List<XmlField> authorFields = new ArrayList<>();

        //Title: tag 245 code a + n
        titleFields.add(new XmlField("830", "a"));
        titleFields.add(new XmlField("800", "t"));
        titleFields.add(new XmlField("245", "a"));
        titleFields.add(new XmlField("245", "n"));
        titleFields.add(new XmlField("490", "a"));

        Resolved<String> resolvedTitle = resolveString(bookData, titleFields, "Unbekannt");
        resolvedTitle.setValue(removeUnwantedSequences(resolvedTitle.getValue()));

        //if title 800 t  --> 245 a is probably subtitle else 245 code b as subtitle
        if (resolvedTitle.with.equals(new XmlField("830", "a")) || resolvedTitle.with.equals(new XmlField("800", "t"))) {
            subtitleFields.add(new XmlField("245", "a"));
        }
        subtitleFields.add(new XmlField("245", "b"));

        Resolved<String> resolvedSubtitle = resolveString(bookData, subtitleFields, null);
        resolvedSubtitle.setValue(removeUnwantedSequences(resolvedSubtitle.getValue()));

        partFields.add(new XmlField("245", "n"));
        //?
        partFields.add(new XmlField("800", "v"));
        partFields.add(new XmlField("490", "v"));

        Resolved<Integer> resolvedPart = resolveInteger(bookData, partFields);

        authorFields.add(new XmlField("245", "c"));
        authorFields.add(new XmlField("100", "a"));
        authorFields.add(new XmlField("800", "a"));

        Resolved<String> resolvedAuthor = resolveString(bookData, authorFields, "Unbekannt");
        resolvedAuthor.setValue(formatAuthors(resolvedAuthor.getValue()));

        return Optional.of(new Book(resolvedTitle.getValue(), resolvedSubtitle.getValue(), resolvedPart.getValue(), isbn, resolvedAuthor.getValue(), true, 0));
    }

    public Optional<Drawable> loadImage(String isbn, int timeout) {
        return requestHandler.getImage(IMAGE_URL + isbn, timeout);

    }

    private Resolved<String> resolveString(Element bookData, List<XmlField> titleFields, String defaultValue) {

        for (XmlField field : titleFields) {
            String result = getContent(getElement(bookData, field.tag), field.code);
            if (result != null) {
                return new Resolved<>(result, field);
            }
        }
        return new Resolved<>(defaultValue, null);
    }

    private Resolved<Integer> resolveInteger(Element bookData, List<XmlField> titleFields) {
        for (XmlField field : titleFields) {
            String resultAsString = getContent(getElement(bookData, field.tag), field.code);
            if (resultAsString != null) {
                Optional<Integer> result = parseToInt(resultAsString);
                if (result.isPresent()) {
                    return new Resolved<>(result.get(), field);
                }
            }
        }
        return new Resolved<>(null, null);
    }

    private Optional<Integer> parseToInt(String part) {
        try {
            return Optional.of(Integer.parseInt(part));
        } catch (NumberFormatException e) {
            Log.e(getClass().getSimpleName(), "Not a integer");

            StringBuilder digits = new StringBuilder();

            for (char character : part.toCharArray()) {
                if (Character.isDigit(character)) {
                    digits.append(character);
                }
            }

            if (digits.length() == 0) {
                return Optional.empty();
            }
            return Optional.of(Integer.valueOf(digits.toString()));
        }
    }

    private String removeUnwantedSequences(String value) {
        if (value == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("˜[a-zA-Z]{3}œ");
        Matcher matcher = pattern.matcher(value);

        if (matcher.find()) {
            int start = matcher.start();
            return new StringBuilder(value)
                    .deleteCharAt(start)
                    .deleteCharAt(start + 3)
                    .toString();
        }
        return value;
    }

    private String formatAuthors(String authors) {
        String[] substrings = authors.split(";");
        StringBuilder result = new StringBuilder();
        for (String substring : substrings) {
            result.append(substring.trim()).append("\n");
        }
        result.setLength(result.length() - 1);
        return result.toString();
    }

    private Element getElement(Element parent, String tag) {
        try {
            return parent.getElementsByAttributeValueContaining("tag", tag).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private String getContent(Element element, String value) {
        if (element == null) {
            return null;
        }
        try {
            return element.getElementsByAttributeValueContaining("code", value).get(0).text();
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }


}