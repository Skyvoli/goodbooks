package io.skyvoli.goodbooks.web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.skyvoli.goodbooks.web.fetch.ResourceFetcher;

public class Requester<T> {

    public Optional<T> getResource(ResourceFetcher<T> resourceFetcher, String stringUrl, int timeout) {
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            return Optional.empty();
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Optional<T>> future = executorService.submit(() -> resourceFetcher.fetch(url));

        Optional<T> content;
        try {
            content = future.get(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (ExecutionException | TimeoutException e) {
            return Optional.empty();
        } finally {
            shutdownPool(executorService);
        }
        return content;
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