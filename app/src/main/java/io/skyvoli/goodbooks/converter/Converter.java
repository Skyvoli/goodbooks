package io.skyvoli.goodbooks.converter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;

public class Converter {

    private Converter() {
        throw new IllegalStateException("Utility class");
    }

    public static byte[] convertToByteArray(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        return output.toByteArray();
    }
}