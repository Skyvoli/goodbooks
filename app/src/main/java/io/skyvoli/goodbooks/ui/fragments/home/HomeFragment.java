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

import io.skyvoli.goodbooks.StartFragmentListener;
import io.skyvoli.goodbooks.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment implements StartFragmentListener {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private boolean shouldConfigureUi = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void configureFragment() {
        if (shouldConfigureUi) {
            shouldConfigureUi = false;
            final TextView textView = binding.textHome;
            homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
            final Button magicButton = binding.button;
            final ProgressBar progressCircular = binding.progressCircular;
            magicButton.setOnClickListener(v -> {
                progressCircular.setVisibility(View.VISIBLE);
                new Thread(() -> {
                    //Fixme Used for debug etc.
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> progressCircular.setVisibility(View.GONE));
                    }
                }).start();
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        shouldConfigureUi = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}