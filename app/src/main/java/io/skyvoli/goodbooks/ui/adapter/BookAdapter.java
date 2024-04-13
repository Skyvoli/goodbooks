package io.skyvoli.goodbooks.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Optional;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.storage.database.dto.Book;
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

        holder.setListener(v -> Toast.makeText(context, "Clicked: " + book.getTitle() + " " + book.getPart(), Toast.LENGTH_SHORT).show());

        holder.setTitle(book.getTitle() + " " + book.getPart());
        holder.setIsbn(book.getIsbn());
        holder.setAuthor(book.getAuthor());
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
        return books.size();
    }
}