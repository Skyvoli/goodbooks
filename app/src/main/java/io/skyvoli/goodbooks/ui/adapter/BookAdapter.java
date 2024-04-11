package io.skyvoli.goodbooks.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.model.Book;
import io.skyvoli.goodbooks.ui.BookViewHolder;

public class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {

    private final List<Book> books;
    private Context context;

    public BookAdapter(List<Book> books) {
        this.books = books;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.book_card, parent, false);
        return new BookViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.setTitle(book.getName());
        holder.setIsbn(book.getIsbn());
        holder.setAuthor(book.getAuthor());
        Drawable cover = book.getCover();
        if (cover != null) {
            holder.setCover(cover);
        } else {
            //Default
            holder.setCover(ContextCompat.getDrawable(context, R.drawable.ruby));
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}