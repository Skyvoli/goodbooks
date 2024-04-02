package io.skyvoli.goodbooks.ui.formatter;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BulletSpan;

public class BulletItemFormatter {

    private final int color;

    public BulletItemFormatter(int color) {
        this.color = color;
    }

    public SpannableString convertString(String item) {
        SpannableString spanString = new SpannableString(item);
        spanString.setSpan(new BulletSpan(10, this.color, 5), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }
}
