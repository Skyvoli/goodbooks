package io.skyvoli.goodbooks.web;

import android.graphics.drawable.Drawable;

import com.fasterxml.jackson.databind.JsonNode;

import org.jsoup.nodes.Document;

import java.util.Optional;

import io.skyvoli.goodbooks.web.fetch.DocumentFetcher;
import io.skyvoli.goodbooks.web.fetch.ImageFetcher;
import io.skyvoli.goodbooks.web.fetch.JsonFetcher;

public class RequestHandler {

    public Optional<Document> getDocument(String url, int timeout) {
        return new Requester<Document>().getResource(new DocumentFetcher(), url, timeout);
    }

    public Optional<JsonNode> getJsonDocument(String url, int timeout) {
        return new Requester<JsonNode>().getResource(new JsonFetcher(), url, timeout);
    }

    public Optional<Drawable> getImage(String url, int timeout) {
        return new Requester<Drawable>().getResource(new ImageFetcher(), url, timeout);
    }


    public Optional<Drawable> getFallbackImage(String isbn, int timeout) {
        Optional<JsonNode> imageLink = getJsonDocument("https://bookcover.longitood.com/bookcover/" + isbn, timeout);

        if (!imageLink.isPresent()) {
            return Optional.empty();
        }

        return getImage(imageLink.get().findValue("url").asText(), timeout);
    }

}