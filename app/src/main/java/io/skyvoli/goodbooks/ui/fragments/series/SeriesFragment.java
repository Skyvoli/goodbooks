package io.skyvoli.goodbooks.ui.fragments.series;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.databinding.FragmentSeriesBinding;
import io.skyvoli.goodbooks.global.GlobalController;
import io.skyvoli.goodbooks.helper.SwipeColorSchemeConfigurator;

public class SeriesFragment extends Fragment {

    FragmentSeriesBinding binding;
    GlobalController globalController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSeriesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        globalController = new GlobalController(requireActivity());

        RecyclerView recyclerView = binding.series;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        recyclerView.setAdapter(new SeriesAdapter(globalController.getSeries()));
        recyclerView.setHasFixedSize(true);

        SwipeRefreshLayout swipeRefreshLayout = binding.swipeRefreshLayout;
        SwipeColorSchemeConfigurator.setSwipeColorScheme(binding.swipeRefreshLayout, requireContext());
        swipeRefreshLayout.setOnRefreshListener(() -> onSwipe(swipeRefreshLayout, recyclerView, requireContext()));


        return root;
    }

    private void onSwipe(SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView, Context context) {
        swipeRefreshLayout.setRefreshing(true);
        new Thread(() -> {
            recyclerView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
            recyclerView.setVisibility(View.INVISIBLE);

            globalController.setListsWithDataFromDatabase(requireContext());

            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    recyclerView.setAdapter(new SeriesAdapter(globalController.getSeries()));
                    recyclerView.clearAnimation();
                    recyclerView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
                    recyclerView.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                });
            }
        }).start();
    }
}