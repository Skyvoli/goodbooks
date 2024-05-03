package io.skyvoli.goodbooks.ui.seriescard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.helper.ImageLoader;
import io.skyvoli.goodbooks.ui.fragments.series.Series;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesViewHolder> {

    private final List<Series> series;

    private Context context;

    public SeriesAdapter(List<Series> items) {
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
        Series oneSeries = this.series.get(position);
        ImageLoader.load(context, oneSeries.getCover(), holder.getCover());

        holder.setMultiple(oneSeries.getCountedBooks() > 1);

    }

    @Override
    public int getItemCount() {
        return series.size();
    }
}