package io.skyvoli.goodbooks.web;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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

    public Optional<Document> getDocument(String url, int timeout) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Optional<Document>> future = executorService.submit(() -> fetchDocument(url));

        Optional<Document> document;
        try {
            document = future.get(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (ExecutionException | TimeoutException e) {
            return Optional.empty();
        } finally {
            shutdownPool(executorService);
        }
        return document;
    }

    public Optional<JsonNode> getJsonDocument(String stringUrl, int timeout) {
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            return Optional.empty();
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Optional<JsonNode>> future = executorService.submit(() -> fetchJson(url));

        Optional<JsonNode> node;
        try {
            node = future.get(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (ExecutionException | TimeoutException e) {
            return Optional.empty();
        } finally {
            shutdownPool(executorService);
        }
        return node;
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
        return Optional.of(doc);
    }

    private Optional<JsonNode> fetchJson(URL url) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            node = mapper.readTree(url);
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.of(node);
    }

    public Optional<Drawable> getImage(String url, int timeout) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Optional<Drawable>> future = executorService.submit(() -> fetchImage(url));

        Optional<Drawable> cover;
        try {
            cover = future.get(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (ExecutionException | TimeoutException e) {
            return Optional.empty();
        } finally {
            shutdownPool(executorService);
        }
        return cover;
    }

    public Optional<Drawable> getFallbackImage(String isbn, int timeout) {
        Optional<JsonNode> imageLink = getJsonDocument("https://bookcover.longitood.com/bookcover/" + isbn, timeout);

        if (!imageLink.isPresent()) {
            return Optional.empty();
        }

        return getImage(imageLink.get().findValue("url").asText(), timeout);
    }

    private Optional<Drawable> fetchImage(String url) {
        try (InputStream is = new URL(url).openConnection().getInputStream()) {
            return Optional.ofNullable(Drawable.createFromStream(is, "cover"));
        } catch (IOException e) {
            Log.e(logTag, "Couldn't fetch image");
            e.printStackTrace();
            return Optional.empty();
        }

    }

    private void shutdownPool(ExecutorService pool) {
        pool.shutdown();

        try {
            if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }


}