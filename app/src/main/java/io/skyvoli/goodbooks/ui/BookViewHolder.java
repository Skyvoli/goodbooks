package io.skyvoli.goodbooks.ui;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

    public void setCover(Drawable cover) {
        this.cover.setImageDrawable(cover);
    }
}