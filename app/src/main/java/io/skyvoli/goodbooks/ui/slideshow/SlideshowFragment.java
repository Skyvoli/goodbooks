package io.skyvoli.goodbooks.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import io.skyvoli.goodbooks.databinding.FragmentSlideshowBinding;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //TODO keine statische Liste
        String[] eintrag = {
                "Eintrag 1",
                "Eintrag 2",
                "Eintrag 3",
                "Eintrag 4",
                "Eintrag 5",
                "Eintrag 6",
        };
        final ListView listView = binding.books;
        ListAdapter adapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_list_item_1, eintrag);
        listView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}