package io.skyvoli.goodbooks.web.fetch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class DocumentFetcher implements ResourceFetcher<Document> {
    @Override
    public Optional<Document> fetch(URL url) {
        Document doc;
        try {
            doc = Jsoup.connect(url.toExternalForm())
                    .maxBodySize(0)
                    .get();
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.of(doc);
    }
}