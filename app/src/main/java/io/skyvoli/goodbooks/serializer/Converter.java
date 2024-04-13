package io.skyvoli.goodbooks.serializer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;

public class Converter {

    private Converter() {
        throw new IllegalStateException("Utility class");
    }

    @TypeConverter
    public static Drawable convertToDrawable(byte[] bytes) {
        return new BitmapDrawable(null, BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
    }

    @TypeConverter
    public static byte[] convertToByteArray(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        return output.toByteArray();
    }
}