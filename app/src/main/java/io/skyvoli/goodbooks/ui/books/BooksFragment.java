package io.skyvoli.goodbooks.ui.books;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import io.skyvoli.goodbooks.databinding.FragmentBooksBinding;
import io.skyvoli.goodbooks.helper.BookAdapter;
import io.skyvoli.goodbooks.ui.GlobalViewModel;

public class BooksFragment extends Fragment {

    private FragmentBooksBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GlobalViewModel globalViewModel = new ViewModelProvider(requireActivity()).get(GlobalViewModel.class);

        binding = FragmentBooksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        final RecyclerView recyclerView = binding.books;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView.Adapter<BookAdapter.BookViewHolder> adapter =
                new BookAdapter(new ArrayList<>(Objects.requireNonNull(globalViewModel.getBooks().getValue())));
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}