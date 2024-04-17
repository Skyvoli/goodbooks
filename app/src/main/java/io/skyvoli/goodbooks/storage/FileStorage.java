package io.skyvoli.goodbooks.storage;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileStorage {

    private final String logTag = this.getClass().getSimpleName();
    private final File directory;

    private static final String IMAGE_PATH_END = "Image.jpeg";

    public FileStorage(File directory) {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("File is not a directory");
        }
        this.directory = directory;
    }

    public void saveImage(String isbn, Drawable cover) {
        try {
            File file = new File(directory.getAbsolutePath() + File.separator + isbn + IMAGE_PATH_END);
            Files.write(file.toPath(), convertToByteArray(cover));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Drawable getImage(String isbn) {
        String path = directory.getPath() + File.separator + isbn + IMAGE_PATH_END;
        //If not found returns null
        return Drawable.createFromPath(path);
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

    private byte[] convertToByteArray(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        return output.toByteArray();
    }
}