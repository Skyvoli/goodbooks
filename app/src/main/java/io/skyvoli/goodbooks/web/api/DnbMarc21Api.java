package io.skyvoli.goodbooks.web.api;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import io.skyvoli.goodbooks.exception.BookNotFound;
import io.skyvoli.goodbooks.model.Book;

public class DnbMarc21Api implements BookApi {


    private static final String BASE_URL = "https://services.dnb.de/sru/dnb?version=1.1";
    private static final String OPERATION = "&operation=searchRetrieve";
    private static final String QUERY = "&query=";
    private static final String MAXIMUM_RECORDS = "&maximumRecords=";
    private static final String RECORD_SCHEMA = "&recordSchema=MARC21-xml";

    public String buildUrl(String isbn) {
        return BASE_URL + OPERATION + QUERY + isbn + MAXIMUM_RECORDS + 5 + RECORD_SCHEMA;
    }

    @Override
    public List<Book> serializeDocument(Document document, String isbn) throws BookNotFound {
        List<Book> books = new ArrayList<>();
        try {
            Element bookData = document.getElementsByAttributeValueContaining("type", "Bibliographic").get(0);
            //Titel: tag 245 code a + n
            Element titleData = bookData.getElementsByAttributeValueContaining("tag", "245").get(0);
            String title = titleData.getElementsByAttributeValueContaining("code", "a").get(0).text()
                    + " "
                    + titleData.getElementsByAttributeValueContaining("code", "n").get(0).text();
            //Autor: tag 100 code a
            Element authorData = bookData.getElementsByAttributeValueContaining("tag", "100").get(0);
            String author = authorData.getElementsByAttributeValueContaining("code", "a").get(0).text();

            books.add(new Book(title, isbn, author, true));
        } catch (IndexOutOfBoundsException e) {
            throw new BookNotFound();
        }
        return books;
    }

}