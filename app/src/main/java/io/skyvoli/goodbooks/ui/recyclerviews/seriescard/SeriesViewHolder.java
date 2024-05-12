package io.skyvoli.goodbooks.ui.recyclerviews.seriescard;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import io.skyvoli.goodbooks.R;

public class SeriesViewHolder extends RecyclerView.ViewHolder {

    private final ImageView cover;
    private final View line1;
    private final View line2;
    private long seriesId;

    public SeriesViewHolder(@NonNull View itemView) {
        super(itemView);
        cover = itemView.findViewById(R.id.cover);
        line1 = itemView.findViewById(R.id.multiple_books);
        line2 = itemView.findViewById(R.id.multiple_books_2);


        itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong("seriesId", seriesId);
            Navigation.findNavController(v).navigate(R.id.to_series_books, bundle);
        });
    }

    public ImageView getCover() {
        return cover;
    }

    public void setMultiple(boolean multipleBooks) {
        if (!multipleBooks) {
            line1.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
        } else {
            line1.setVisibility(View.VISIBLE);
            line2.setVisibility(View.VISIBLE);
        }
    }

    public void setSeriesId(long seriesId) {
        this.seriesId = seriesId;
    }
}