package io.skyvoli.goodbooks.ui.fragments.series;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Optional;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.storage.database.dto.Book;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesViewHolder> {

    private final List<Book> series;

    private Context context;

    public SeriesAdapter(List<Book> items) {
        series = items;
    }

    @NonNull
    @Override
    public SeriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.series_card, parent, false);
        return new SeriesViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SeriesViewHolder holder, int position) {
        Book book = series.get(position);
        Optional<Drawable> cover = book.getCover();
        if (cover.isPresent()) {
            holder.setCover(cover.get());
        } else {
            //Default
            holder.setCover(ContextCompat.getDrawable(context, R.drawable.ruby));
        }
    }

    @Override
    public int getItemCount() {
        return series.size();
    }
}