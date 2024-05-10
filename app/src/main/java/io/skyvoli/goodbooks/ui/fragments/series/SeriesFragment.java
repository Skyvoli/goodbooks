package io.skyvoli.goodbooks.ui.fragments.series;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.databinding.FragmentSeriesBinding;
import io.skyvoli.goodbooks.global.GlobalController;
import io.skyvoli.goodbooks.helper.SwipeColorSchemeConfigurator;
import io.skyvoli.goodbooks.storage.database.dto.Series;
import io.skyvoli.goodbooks.ui.recyclerviews.seriescard.SeriesAdapter;

public class SeriesFragment extends Fragment {

    private GlobalController globalController;
    private FragmentSeriesBinding binding;
    private List<Series> series;

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

        ProgressBar progressBar = binding.progressBar;

        SwipeRefreshLayout swipeRefreshLayout = binding.swipeRefreshLayout;
        SwipeColorSchemeConfigurator.setSwipeColorScheme(binding.swipeRefreshLayout, requireContext());
        swipeRefreshLayout.setOnRefreshListener(() -> onSwipe(swipeRefreshLayout, recyclerView, requireContext()));

        //TODO pagination
        //sHOW LOADING
        if (series == null && globalController.getSeries().isEmpty()) {
            new Thread(() -> {
                series = globalController.loadSeriesFromDb(requireContext());
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> loadingCompleted(recyclerView, progressBar));
                }
            }).start();
        } else {
            //series = globalController.getSeries();
            loadingCompleted(recyclerView, progressBar);

        }

        return root;
    }

    private void loadingCompleted(RecyclerView recyclerView, ProgressBar progressBar) {
        recyclerView.setAdapter(new SeriesAdapter(globalController.getSeries()));
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void onSwipe(SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView, Context context) {
        swipeRefreshLayout.setRefreshing(true);
        new Thread(() -> {
            recyclerView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
            recyclerView.setVisibility(View.INVISIBLE);

            series = globalController.loadSeriesFromDb(requireContext());

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}