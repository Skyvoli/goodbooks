package io.skyvoli.goodbooks.serializer;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

import io.skyvoli.goodbooks.model.Book;

public class BookDeserializer extends JsonDeserializer<Book> {
    @Override
    public Book deserialize(JsonParser jp, DeserializationContext context) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        String title = node.get("title").asText();
        String part = node.get("part").asText();
        String isbn = node.get("isbn").asText();
        String author = node.get("author").asText();
        Drawable cover = convertToDrawable(node.get("cover").binaryValue());
        boolean resolved = node.get("resolved").asBoolean();
        return new Book(title, part, isbn, author, cover, resolved);
    }

    private Drawable convertToDrawable(byte[] bytes) {
        return new BitmapDrawable(null, BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
    }
}