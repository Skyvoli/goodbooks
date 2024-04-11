package io.skyvoli.goodbooks.web.api;

import android.graphics.drawable.Drawable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import io.skyvoli.goodbooks.model.Book;
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
        //Titel: tag 245 code a + n
        Element titleData = bookData.getElementsByAttributeValueContaining("tag", "245").get(0);
        String title = getContent(titleData, "a", "Unbekannt");
        String part = getContent(titleData, "n", "");
        //Autor: tag 100 code a
        Element authorData = bookData.getElementsByAttributeValueContaining("tag", "100").get(0);
        String author = getContent(authorData, "a", "Unbekannt");
        Drawable cover = new RequestHandler().getImage(IMAGE_URL + isbn);

        return new Book(title, part, isbn, author, cover, true);

    }

    private String getContent(Element element, String value, String defaultValue) {
        try {
            return element.getElementsByAttributeValueContaining("code", value).get(0).text();
        } catch (IndexOutOfBoundsException e) {
            return defaultValue;
        }
    }


}