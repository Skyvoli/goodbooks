package io.skyvoli.goodbooks.web.api;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.skyvoli.goodbooks.model.Book;

public class DnbOaiDcApi implements BookApi {

    private static final String BASE_URL = "https://services.dnb.de/sru/dnb?version=1.1";
    private static final String OPERATION = "&operation=searchRetrieve";
    private static final String QUERY = "&query=";
    private static final String MAXIMUM_RECORDS = "&maximumRecords=";
    private static final String RECORD_SCHEMA = "&recordSchema=oai_dc";

    public String buildUrl(String isbn) {
        return BASE_URL + OPERATION + QUERY + isbn + MAXIMUM_RECORDS + 5 + RECORD_SCHEMA;
    }

    public List<Book> serializeDocument(Document document, String isbn) {
        List<Elements> booksData = document.getElementsByTag("dc")
                .stream()
                .map(Element::children)
                .collect(Collectors.toList());


        List<Book> books = new ArrayList<>();

        for (Elements bookData : booksData) {
            Map<String, String> mappedData = new HashMap<>();

            for (Element data : bookData) {
                if (mappedData.containsKey(data.tagName())) {
                    mappedData.put(data.tagName() + data.attributes(), mappedData.get(data.tagName()) + " + " + data.text());
                } else {
                    mappedData.put(data.tagName() + data.attributes(), data.text());
                }
            }

            books.add(new Book(mappedData.get("dc:title"), isbn, mappedData.get("dc:creator"), true));
        }
        return books;
    }
}