package io.skyvoli.goodbooks.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import io.skyvoli.goodbooks.R;

public class ImageLoader {

    private ImageLoader() {
        throw new IllegalStateException("Utility class");
    }

    public static void load(Context context, Drawable cover, ImageView imageView) {
        Glide.with(context)
                .load(cover)
                .fallback(R.drawable.ruby)
                .into(imageView);
    }
}