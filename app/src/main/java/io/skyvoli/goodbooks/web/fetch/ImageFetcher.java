package io.skyvoli.goodbooks.web.fetch;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;

public class ImageFetcher implements ResourceFetcher<Drawable> {

    private final String logTag = this.getClass().getSimpleName();

    @Override
    public Optional<Drawable> fetch(URL url) {
        try (InputStream is = url.openConnection().getInputStream()) {
            return Optional.ofNullable(Drawable.createFromStream(is, "cover"));
        } catch (IOException e) {
            Log.e(logTag, "Couldn't fetch image");
            e.printStackTrace();
            return Optional.empty();
        }
    }
}