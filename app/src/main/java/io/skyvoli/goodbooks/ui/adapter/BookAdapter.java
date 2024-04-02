package io.skyvoli.goodbooks.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.model.Book;
import io.skyvoli.goodbooks.ui.formatter.BulletItemFormatter;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private final ArrayList<Book> books;

    public BookAdapter(ArrayList<Book> books) {
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

        holder.title.setText(books.get(position).getName());

        BulletItemFormatter converter = new BulletItemFormatter(Color.CYAN);
        holder.isbn.setText(converter.convertString(books.get(position).getIsbn()));
        holder.author.setText(converter.convertString("Autor"));
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {

        protected TextView title;
        protected TextView isbn;
        protected TextView author;

        public BookViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            isbn = itemView.findViewById(R.id.isbn);
            author = itemView.findViewById(R.id.author);
        }
    }
}
