package io.skyvoli.goodbooks.ui.fragments.seriesbooks;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import io.skyvoli.goodbooks.databinding.FragmentSeriesBooksBinding;
import io.skyvoli.goodbooks.global.GlobalController;
import io.skyvoli.goodbooks.helper.SwipeColorSchemeConfigurator;
import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.ui.recyclerviews.bookcard.BookAdapter;

public class SeriesBooks extends Fragment {

    private long seriesId;
    private FragmentSeriesBooksBinding binding;
    private List<Book> books;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) {
            Log.e(getTag(), "Missing arguments");
            seriesId = 0;
            return;
        }
        seriesId = getArguments().getLong("seriesId");

        if (seriesId == 0) {
            Log.e(getTag(), "Missing argument seriesId");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        GlobalController globalController = new GlobalController(requireActivity());
        binding = FragmentSeriesBooksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.books;
        ProgressBar progressBar = binding.progressBar;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        new Thread(() -> {
            books = globalController.getBooksFromSeries(requireContext(), seriesId);

            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    recyclerView.setAdapter(new BookAdapter(books));
                    progressBar.setVisibility(View.GONE);
                });
            }
        }).start();

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