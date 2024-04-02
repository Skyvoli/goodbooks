package io.skyvoli.goodbooks.web;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HttpRequest {

    public Optional<Document> invoke(String url) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Optional<Document>> future = executorService.submit(() -> this.fetchDocument(url));

        Optional<Document> document = Optional.empty();
        try {
            document = future.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            //TODO better
            e.printStackTrace();
        }
        return document;
    }

    private Optional<Document> fetchDocument(String url) {
        Document doc;
        try {
            doc = Jsoup.connect(url)
                    .maxBodySize(0)
                    .get();
        } catch (IOException e) {
            //logger.error("Couldn't fetch document");
            return Optional.empty();
        }
        return Optional.ofNullable(doc);
    }

}
