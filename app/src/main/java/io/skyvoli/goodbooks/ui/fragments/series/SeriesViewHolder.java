package io.skyvoli.goodbooks.ui.fragments.series;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.skyvoli.goodbooks.R;

public class SeriesViewHolder extends RecyclerView.ViewHolder {

    private final ImageView cover;
    private final View line1;
    private final View line2;

    public SeriesViewHolder(@NonNull View itemView) {
        super(itemView);
        cover = itemView.findViewById(R.id.cover);
        line1 = itemView.findViewById(R.id.multiple_books);
        line2 = itemView.findViewById(R.id.multiple_books_2);
    }

    public ImageView getCover() {
        return cover;
    }

    public void setMultiple(boolean multipleBooks) {
        if (!multipleBooks) {
            line1.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
        }
    }
}