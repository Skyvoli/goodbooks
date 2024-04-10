package io.skyvoli.goodbooks.storage;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import io.skyvoli.goodbooks.constants.Constants;
import io.skyvoli.goodbooks.model.Book;

public class Storage {

    private final String logTag = this.getClass().getSimpleName();
    private final File directory;

    public Storage(File directory) {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("File is not a directory");
        }
        this.directory = directory;
    }

    public void saveObject(String filename, Object object) {
        try {
            File file = new File(directory.getAbsolutePath() + File.separator + filename);
            Files.write(file.toPath(), new ObjectMapper().writeValueAsBytes(object));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearStorage() {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                Log.e(logTag, "Couldn't delete file");
            }
        }
    }

    public Set<Book> getBooks() {
        File file = new File(directory.getAbsolutePath() + File.separator + Constants.FILENAME_BOOKS);
        ObjectMapper mapper = new ObjectMapper();
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(Set.class, Book.class);

        try {
            return mapper.readValue(file, collectionType);
        } catch (IOException e) {
            Log.e(logTag, "Couldn't read file");
            e.printStackTrace();
        }
        return new HashSet<>();
    }
}