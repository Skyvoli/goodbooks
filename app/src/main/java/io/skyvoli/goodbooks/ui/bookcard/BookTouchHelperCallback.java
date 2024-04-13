package io.skyvoli.goodbooks.ui.bookcard;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;

public class BookTouchHelperCallback extends ItemTouchHelper.Callback {

    private final BookAdapter adapter;

    public BookTouchHelperCallback(BookAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.END);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        int draggedItemIndex = viewHolder.getAdapterPosition();
        int targetIndex = target.getAdapterPosition();
        Collections.swap(adapter.getBooks(), draggedItemIndex, targetIndex);
        adapter.notifyItemMoved(draggedItemIndex, targetIndex);
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.END) {
            adapter.getBooks().remove(viewHolder.getAdapterPosition());
            adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
        }
    }
}