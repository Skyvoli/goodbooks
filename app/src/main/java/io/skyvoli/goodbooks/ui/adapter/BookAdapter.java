package io.skyvoli.goodbooks.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.model.Book;
import io.skyvoli.goodbooks.model.BookViewHolder;

public class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {

    private final List<Book> books;

    public BookAdapter(List<Book> books) {
        this.books = books;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.book_card, parent, false);
        return new BookViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {

        holder.setTitle(books.get(position).getName());

        holder.setIsbn(books.get(position).getIsbn());
        holder.setAuthor("Autor");
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}