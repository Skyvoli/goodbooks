package io.skyvoli.goodbooks.ui.fragments.series;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.skyvoli.goodbooks.databinding.FragmentSeriesBinding;
import io.skyvoli.goodbooks.global.GlobalController;
import io.skyvoli.goodbooks.storage.database.dto.Book;

public class SeriesFragment extends Fragment {

    FragmentSeriesBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSeriesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        GlobalController globalController = new GlobalController(requireActivity());
        List<Book> books = globalController.getBooks();

        Set<String> titles = books.stream().map(Book::getTitle).collect(Collectors.toSet());

        List<Series> series = titles.stream().map(s ->
                        new Series(s, books.stream()
                                .filter(book -> s.equalsIgnoreCase(book.getTitle()))
                                .findFirst().get().getNullableCover()))
                .collect(Collectors.toList());


        RecyclerView recyclerView = binding.series;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        recyclerView.setAdapter(new SeriesAdapter(series));
        recyclerView.setHasFixedSize(true);

        return root;
    }
}