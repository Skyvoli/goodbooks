package io.skyvoli.goodbooks.ui.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import io.skyvoli.goodbooks.databinding.FragmentHomeBinding;
import io.skyvoli.goodbooks.ui.fragments.StartFragmentListener;

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
            homeViewModel.getText().observe(getViewLifecycleOwner(), binding.textHome::setText);
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