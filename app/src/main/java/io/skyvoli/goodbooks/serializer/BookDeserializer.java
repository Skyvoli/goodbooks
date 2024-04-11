package io.skyvoli.goodbooks.serializer;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

import io.skyvoli.goodbooks.model.Book;

public class BookDeserializer extends JsonDeserializer<Book> {
    @Override
    public Book deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = jp.getCodec().readTree(jp);
        String title = node.get("title").asText();
        String part = node.get("part").asText();
        String isbn = node.get("isbn").asText();
        String author = node.get("author").asText();
        byte[] bytes = node.get("cover").binaryValue();
        Drawable cover = new BitmapDrawable(null, BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        boolean resolved = node.get("resolved").asBoolean();
        return new Book(title, part, isbn, author, cover, resolved);
    }
}