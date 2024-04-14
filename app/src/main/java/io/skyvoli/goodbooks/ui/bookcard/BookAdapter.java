package io.skyvoli.goodbooks.ui.bookcard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Optional;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.helper.listener.OnItemClickListener;
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

        holder.setListener(new OnItemClickListener() {
            @Override
            public View.OnClickListener onClick() {
                return v -> Toast.makeText(context, "Short", Toast.LENGTH_SHORT).show();
            }

            @Override
            public View.OnLongClickListener onLongClick() {
                return v -> {
                    NavController navController = Navigation.findNavController(v);
                    Bundle bundle = new Bundle();
                    bundle.putString("isbn", book.getIsbn());
                    bundle.putInt("index", holder.getAdapterPosition());
                    navController.navigate(R.id.to_detail, bundle);
                    return true;
                };
            }
        });

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

    public List<Book> getBooks() {
        return books;
    }
}