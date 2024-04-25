package io.skyvoli.goodbooks.web.api;

import android.graphics.drawable.Drawable;
import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
    public Book serializeDocument(Document document, String isbn) {
        Element bookData;
        try {
            bookData = document.getElementsByAttributeValueContaining("type", "Bibliographic").get(0);
        } catch (IndexOutOfBoundsException e) {
            return new Book(isbn);
        }
        //Titel: tag 245 code a + n TODO code b as subtitle?
        Element titleData = getElement(bookData, "245");
        String title = getContent(titleData, "a", "Unbekannt");
        String subTitle = getContent(titleData, "b", "");
        Integer part = parseToInt(getContent(titleData, "n", ""));

        String author = resolveAuthor(bookData);
        Drawable cover = new RequestHandler().getImage(IMAGE_URL + isbn, 10);

        return new Book(title, part, isbn, author, cover, true);

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

    private String resolveAuthor(Element bookData) {
        //Autor: tag 100 code a or 245 code c
        Element authorData = getElement(bookData, "100");
        if (authorData != null) {
            return getContent(authorData, "a", "Unbekannt");
        }

        authorData = getElement(bookData, "245");
        return getContent(authorData, "c", "Unbekannt");
    }

    private Element getElement(Element parent, String tag) {
        try {
            return parent.getElementsByAttributeValueContaining("tag", tag).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private String getContent(Element element, String value, String defaultValue) {
        if (element == null) {
            return defaultValue;
        }
        try {
            return element.getElementsByAttributeValueContaining("code", value).get(0).text();
        } catch (IndexOutOfBoundsException e) {
            return defaultValue;
        }
    }


}