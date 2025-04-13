package io.skyvoli.goodbooks.web.fetch;

import java.net.URL;
import java.util.Optional;

public interface ResourceFetcher<T> {

    Optional<T> fetch(URL url);
}