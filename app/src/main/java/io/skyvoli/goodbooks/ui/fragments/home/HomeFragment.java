package io.skyvoli.goodbooks.ui.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import io.skyvoli.goodbooks.databinding.FragmentHomeBinding;
import io.skyvoli.goodbooks.global.GlobalController;

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
            new Thread(() -> {
                //Fixme Used for debug etc.
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> progressCircular.setVisibility(View.GONE));
                }
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