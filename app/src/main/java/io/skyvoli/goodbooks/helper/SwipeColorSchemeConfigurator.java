package io.skyvoli.goodbooks.helper;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SwipeColorSchemeConfigurator {

    private SwipeColorSchemeConfigurator() {
        throw new IllegalStateException("Utility Class");
    }

    public static void setSwipeColorScheme(SwipeRefreshLayout swipeRefreshLayout, Context context) {
        //Change colors
        TypedValue primary = new TypedValue();
        TypedValue secondary = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, primary, true);
        theme.resolveAttribute(com.google.android.material.R.attr.colorSecondary, secondary, true);

        swipeRefreshLayout.setColorSchemeResources(primary.resourceId, secondary.resourceId);
    }
}