package io.skyvoli.goodbooks.web.fetch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class JsonFetcher implements ResourceFetcher<JsonNode> {
    @Override
    public Optional<JsonNode> fetch(URL url) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            node = mapper.readTree(url);
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.of(node);
    }
}