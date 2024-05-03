package io.skyvoli.goodbooks.ui.fragments.seriesbooks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.skyvoli.goodbooks.databinding.FragmentSeriesBooksBinding;
import io.skyvoli.goodbooks.global.GlobalController;
import io.skyvoli.goodbooks.helper.SwipeColorSchemeConfigurator;
import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.ui.recyclerviews.bookcard.BookAdapter;

public class SeriesBooks extends Fragment {

    private String title;
    private FragmentSeriesBooksBinding binding;


    public SeriesBooks() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) {
            throw new IllegalStateException("Missing argument title");
        }
        title = Optional.ofNullable(getArguments().getString("title"))
                .orElseThrow(() -> new IllegalStateException("Missing argument title"));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        GlobalController globalController = new GlobalController(requireActivity());
        binding = FragmentSeriesBooksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.books;
        binding.progressBar.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Book> books = globalController.getBooks()
                .stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(title))
                .collect(Collectors.toList());

        recyclerView.setAdapter(new BookAdapter(books));

        final SwipeRefreshLayout swipeRefreshLayout = binding.swipeRefreshLayout;

        SwipeColorSchemeConfigurator.setSwipeColorScheme(swipeRefreshLayout, requireContext());
        //swipeRefreshLayout.setOnRefreshListener(() -> onSwipe(swipeRefreshLayout, context));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}