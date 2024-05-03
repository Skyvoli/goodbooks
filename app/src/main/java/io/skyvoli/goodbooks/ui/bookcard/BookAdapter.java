package io.skyvoli.goodbooks.ui.bookcard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.helper.ImageLoader;
import io.skyvoli.goodbooks.helper.TitleBuilder;
import io.skyvoli.goodbooks.storage.database.dto.Book;

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
        holder.setTitle(TitleBuilder.buildTitle(book.getTitle(), book.getSubtitle(), book.getPart()));
        holder.setIsbn(book.getIsbn());
        holder.setAuthor(book.getAuthor());
        ImageLoader.load(context, book.getNullableCover(), holder.getCover());
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public List<Book> getBooks() {
        return books;
    }

}