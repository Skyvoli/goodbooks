package io.skyvoli.goodbooks.web;

import android.graphics.drawable.Drawable;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RequestHandler {
    private final String logTag = this.getClass().getSimpleName();

    public Optional<Document> getDocument(String url) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Optional<Document>> future = executorService.submit(() -> fetchDocument(url));

        Optional<Document> document = Optional.empty();
        try {
            document = future.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ignored) {
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
            return Optional.empty();
        }
        return Optional.ofNullable(doc);
    }


    public Drawable getImage(String url) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Drawable> future = executorService.submit(() -> fetchImage(url));

        Drawable cover;
        try {
            cover = future.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ignored) {
            return null;
        }
        return cover;
    }

    private Drawable fetchImage(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            return Drawable.createFromStream(is, "cover");
        } catch (IOException e) {
            Log.e(logTag, "Couldn't fetch image");
            e.printStackTrace();
            return null;
        }
    }


}