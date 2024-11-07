package io.skyvoli.goodbooks.helper;


import android.content.Context;

import java.util.Iterator;
import java.util.List;

import io.skyvoli.goodbooks.R;

public class DataFormatter {

    private static final int BORDER = 6;

    private DataFormatter() {
        throw new IllegalStateException("Utility class");
    }

    public static String getMissingBooksString(Context context, List<Integer> missing) {
        if (missing.isEmpty()) {
            return context.getResources().getString(R.string.no_missing_books);
        } else if (missing.size() == 1) {
            return "Buch " + missing.get(0) + " fehlt";
        }

        StringBuilder builder = new StringBuilder("Bücher ");
        Iterator<Integer> iterator = missing.iterator();

        if (missing.size() > BORDER) {
            builder.append(missing.get(0));
            for (int index = 1; index < BORDER; index++) {
                builder.append(", ").append(missing.get(index));
            }
            builder.setLength(builder.length() - 1);
            builder.append("& ").append(missing.size() - (BORDER - 1)).append(" weitere Bücher");
        } else {
            builder.append(iterator.next());
            while (iterator.hasNext()) {
                builder.append(", ").append(iterator.next());
            }
        }

        return builder.append(" fehlen").toString();
    }
}