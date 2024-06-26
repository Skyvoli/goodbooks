package io.skyvoli.goodbooks.helper.observer;

import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableList;
import androidx.fragment.app.FragmentActivity;

import io.skyvoli.goodbooks.databinding.FragmentBooksBinding;
import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.ui.bookcard.BookAdapter;

public class BookObserver extends ObservableList.OnListChangedCallback<ObservableList<Book>> {

    private static final String ADAPTER_ERROR = "Adapter is null";
    private final FragmentBooksBinding binding;
    private final FragmentActivity activity;
    private final String logTag = this.getClass().getSimpleName();

    public BookObserver(FragmentBooksBinding binding, FragmentActivity activity) {
        this.binding = binding;
        this.activity = activity;
    }

    @Override
    public void onChanged(ObservableList<Book> sender) {
        Log.d(logTag, "OnChanged");
    }

    @Override
    public void onItemRangeChanged(ObservableList<Book> sender, int positionStart, int itemCount) {
        BookAdapter adapter = (BookAdapter) binding.books.getAdapter();
        if (adapter == null) {
            Log.e(getClass().getSimpleName(), ADAPTER_ERROR);
            return;
        }

        int index = positionStart;
        for (int count = itemCount; count > 0; count--) {
            adapter.getBooks().set(index, sender.get(index));

            int finalIndexBooks = index;
            activity.runOnUiThread(() -> adapter.notifyItemChanged(finalIndexBooks));
            index++;
        }
    }

    @Override
    public void onItemRangeInserted(ObservableList<Book> sender, int positionStart, int itemCount) {
        BookAdapter adapter = (BookAdapter) binding.books.getAdapter();
        if (adapter == null) {
            Log.e(getClass().getSimpleName(), ADAPTER_ERROR);
            return;
        }
        int index = positionStart;
        for (int count = itemCount; count > 0; count--) {
            adapter.getBooks().add(index, sender.get(index));

            int finalIndex = index;
            activity.runOnUiThread(() -> {
                adapter.notifyItemInserted(finalIndex);
                binding.placeholder.setVisibility(View.GONE);
            });
            index++;
        }
    }

    @Override
    public void onItemRangeMoved(ObservableList<Book> sender, int fromPosition, int toPosition, int itemCount) {
        Log.d(logTag, "OnRangeMoved");
    }

    @Override
    public void onItemRangeRemoved(ObservableList<Book> sender, int positionStart, int itemCount) {
        BookAdapter adapter = (BookAdapter) binding.books.getAdapter();
        if (adapter == null) {
            Log.e(getClass().getSimpleName(), ADAPTER_ERROR);
            return;
        }
        int index = positionStart;
        for (int count = itemCount; count > 0; count--) {
            adapter.getBooks().remove(index);

            int finalIndex = index;
            activity.runOnUiThread(() -> adapter.notifyItemRemoved(finalIndex));
            index++;
        }
    }
}