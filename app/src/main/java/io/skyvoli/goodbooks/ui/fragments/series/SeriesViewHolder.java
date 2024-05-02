package io.skyvoli.goodbooks.ui.fragments.series;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.skyvoli.goodbooks.R;

public class SeriesViewHolder extends RecyclerView.ViewHolder {

    private final ImageView cover;

    public SeriesViewHolder(@NonNull View itemView) {
        super(itemView);
        cover = itemView.findViewById(R.id.cover);
    }

    public ImageView getCover() {
        return cover;
    }
}