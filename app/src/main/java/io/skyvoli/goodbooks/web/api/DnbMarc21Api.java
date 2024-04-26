package io.skyvoli.goodbooks.web.api;

import android.graphics.drawable.Drawable;
import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.web.RequestHandler;

public class DnbMarc21Api implements BookApi {

    private static final String BASE_URL = "https://services.dnb.de/sru/dnb?version=1.1";
    private static final String OPERATION = "&operation=searchRetrieve";
    private static final String QUERY = "&query=";
    private static final String MAXIMUM_RECORDS = "&maximumRecords=";
    private static final String RECORD_SCHEMA = "&recordSchema=MARC21-xml";
    private static final String IMAGE_URL = "https://portal.dnb.de/opac/mvb/cover?isbn=";

    public String buildUrl(String isbn) {
        return BASE_URL + OPERATION + QUERY + isbn + MAXIMUM_RECORDS + 1 + RECORD_SCHEMA;
    }

    @Override
    public Book serializeDocument(Document document, String isbn, int timeout) {
        Element bookData;
        try {
            bookData = document.getElementsByAttributeValueContaining("type", "Bibliographic").get(0);
        } catch (IndexOutOfBoundsException e) {
            return new Book(isbn);
        }

        List<XmlField> titleFields = new ArrayList<>();
        List<XmlField> subtitleFields = new ArrayList<>();
        List<XmlField> partFields = new ArrayList<>();
        List<XmlField> authorFields = new ArrayList<>();

        //Titel: tag 245 code a + n
        titleFields.add(new XmlField("800", "t"));
        titleFields.add(new XmlField("245", "a"));
        titleFields.add(new XmlField("245", "n"));

        Resolved<String> resolvedTitle = resolveString(bookData, titleFields, "Unbekannt");

        //if title 800 t  --> 245 a is probably subtitle else 245 code b as subtitle
        if (resolvedTitle.with.equals(new XmlField("800", "t"))) {
            subtitleFields.add(new XmlField("245", "a"));
        }
        subtitleFields.add(new XmlField("245", "b"));

        Resolved<String> resolvedSubtitle = resolveString(bookData, subtitleFields, null);

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

        Drawable cover = new RequestHandler().getImage(IMAGE_URL + isbn, timeout);

        //FIXME Filter weird String ˜Dieœ -> Die, ˜Derœ -> Der?
        return new Book(resolvedTitle.getValue(), resolvedSubtitle.getValue(), resolvedPart.getValue(), isbn, resolvedAuthor.getValue(), cover, true);

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
                Integer result = parseToInt(resultAsString);
                if (result != null) {
                    return new Resolved<>(result, field);
                }
            }
        }
        return new Resolved<>(null, null);
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