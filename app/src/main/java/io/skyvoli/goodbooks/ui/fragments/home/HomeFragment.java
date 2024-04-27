package io.skyvoli.goodbooks.ui.fragments.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import io.skyvoli.goodbooks.databinding.FragmentHomeBinding;
import io.skyvoli.goodbooks.global.GlobalController;
import io.skyvoli.goodbooks.storage.database.AppDatabase;
import io.skyvoli.goodbooks.web.BookResolver;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        GlobalController globalController = new GlobalController(requireActivity());

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        final Button magicButton = binding.button;
        final ProgressBar progressCircular = binding.progressCircular;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        magicButton.setOnClickListener(v -> {
            progressCircular.setVisibility(View.VISIBLE);
            Context context = requireContext();
            new Thread(() -> {

                AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "books")
                        .build();
                BookResolver resolver = new BookResolver();
                globalController.getBooks().forEach(book -> db.bookDao().update(resolver.resolveBook(book.getIsbn(), 20)));

                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> progressCircular.setVisibility(View.GONE));
                }
                Log.d("", "Resolved");
            }).start();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}