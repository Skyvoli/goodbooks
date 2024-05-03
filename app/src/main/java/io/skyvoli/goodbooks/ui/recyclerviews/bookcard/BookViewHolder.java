package io.skyvoli.goodbooks.ui.recyclerviews.bookcard;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import io.skyvoli.goodbooks.R;

public class BookViewHolder extends RecyclerView.ViewHolder {

    private final TextView title;
    private final TextView isbn;
    private final TextView author;
    private final ImageView cover;

    public BookViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.bookTitle);
        isbn = itemView.findViewById(R.id.isbn);
        author = itemView.findViewById(R.id.author);
        cover = itemView.findViewById(R.id.cover);

        itemView.findViewById(R.id.bookCard).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("isbn", isbn.getText().toString());
            Navigation.findNavController(v).navigate(R.id.to_detail, bundle);
        });
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setIsbn(String isbn) {
        this.isbn.setText(isbn);
    }

    public void setAuthor(String author) {
        this.author.setText(author);
    }

    public ImageView getCover() {
        return cover;
    }
}