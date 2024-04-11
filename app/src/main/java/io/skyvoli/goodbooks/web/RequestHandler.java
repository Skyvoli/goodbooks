package io.skyvoli.goodbooks.web;

import android.graphics.drawable.Drawable;

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

    private final String url;

    public RequestHandler(String url) {
        this.url = url;
    }

    public Optional<Document> invoke() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Optional<Document>> future = executorService.submit(this::fetchDocument);

        Optional<Document> document = Optional.empty();
        try {
            document = future.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ignored) {
        }
        return document;
    }

    private Optional<Document> fetchDocument() {
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


    public Drawable getImage() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Drawable> future = executorService.submit(this::getCover);

        Drawable cover;
        try {
            cover = future.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ignored) {
            return null;
        }
        return cover;
    }

    private Drawable getCover() {
        //String test = "https://portal.dnb.de/opac/mvb/cover?isbn=" + isbn;
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            //return BitmapFactory.decodeStream((InputStream) new URL(test).getContent());
            return Drawable.createFromStream(is, "cover");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}