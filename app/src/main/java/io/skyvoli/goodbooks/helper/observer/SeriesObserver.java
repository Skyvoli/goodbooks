package io.skyvoli.goodbooks.helper.observer;

import android.util.Log;

import androidx.databinding.ObservableList;
import androidx.fragment.app.FragmentActivity;

import io.skyvoli.goodbooks.databinding.FragmentSeriesBinding;
import io.skyvoli.goodbooks.storage.database.dto.Series;
import io.skyvoli.goodbooks.ui.recyclerviews.seriescard.SeriesAdapter;

public class SeriesObserver extends ObservableList.OnListChangedCallback<ObservableList<Series>> {

    private static final String ADAPTER_ERROR = "Adapter is null";
    private final FragmentSeriesBinding binding;
    private final FragmentActivity activity;
    private final String logTag = this.getClass().getSimpleName();

    public SeriesObserver(FragmentSeriesBinding binding, FragmentActivity activity) {
        this.binding = binding;
        this.activity = activity;
    }

    @Override
    public void onChanged(ObservableList<Series> sender) {

    }

    @Override
    public void onItemRangeChanged(ObservableList<Series> sender, int positionStart, int itemCount) {

    }

    @Override
    public void onItemRangeInserted(ObservableList<Series> sender, int positionStart, int itemCount) {

    }

    @Override
    public void onItemRangeMoved(ObservableList<Series> sender, int fromPosition, int toPosition, int itemCount) {

    }

    @Override
    public void onItemRangeRemoved(ObservableList<Series> sender, int positionStart, int itemCount) {
        SeriesAdapter adapter = (SeriesAdapter) binding.series.getAdapter();
        if (adapter == null) {
            Log.e(logTag, ADAPTER_ERROR);
            return;
        }
        int index = positionStart;
        for (int count = itemCount; count > 0; count--) {
            adapter.getSeries().remove(index);

            int finalIndex = index;
            activity.runOnUiThread(() -> adapter.notifyItemRemoved(finalIndex));
            index++;
        }
    }
}