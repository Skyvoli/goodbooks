package io.skyvoli.goodbooks.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

import io.skyvoli.goodbooks.model.Book;

public class BookSerializer extends StdSerializer<Book> {

    public BookSerializer(Class<Book> t) {
        super(t);
    }

    @Override
    public void serialize(Book book, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("title", book.getTitle());
        gen.writeStringField("part", book.getPart());
        gen.writeStringField("isbn", book.getIsbn());
        gen.writeStringField("author", book.getAuthor());
        gen.writeBinaryField("cover", Converter.convertToByteArray(book.getCover()));
        gen.writeBooleanField("resolved", book.isResolved());
        gen.writeEndObject();
    }


}