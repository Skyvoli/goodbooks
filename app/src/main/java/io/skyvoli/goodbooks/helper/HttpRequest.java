package io.skyvoli.goodbooks.helper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HttpRequest {

    public Document invoke(String url) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Document> future = executorService.submit(() -> this.fetchDocument(url));

        Document document = null;
        try {
            document = future.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            //TODO better
            e.printStackTrace();
        }
        return document;
    }

    private Document fetchDocument(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url)
                    .maxBodySize(0)
                    .get();
        } catch (IOException e) {
            //logger.error("Couldn't fetch document");
            e.printStackTrace();
        }
        return doc;
    }

}
