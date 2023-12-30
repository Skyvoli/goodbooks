package io.skyvoli.goodbooks.ui.camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import io.skyvoli.goodbooks.databinding.FragmentCameraBinding;
import io.skyvoli.goodbooks.listener.ScanListener;
import io.skyvoli.goodbooks.ui.GlobalViewModel;

public class CameraFragment extends Fragment {

    private FragmentCameraBinding binding;
    CameraViewModel cameraViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        cameraViewModel = new ViewModelProvider(this).get(CameraViewModel.class);

        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textContent;
        cameraViewModel.getText1().observe(getViewLifecycleOwner(), textView::setText);

        final TextView textView2 = binding.textFormat;
        cameraViewModel.getText2().observe(getViewLifecycleOwner(), textView2::setText);

        final Button button = binding.scanBtn;
        button.setOnClickListener(new ScanListener(this));

        return root;
    }

    //TODO Update
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                // if the intentResult is not null we'll set
                // the content and format of scan message
                cameraViewModel.setText1(result.getContents());
                cameraViewModel.setText2(result.getFormatName());
                GlobalViewModel globalViewModel = new ViewModelProvider(requireActivity()).get(GlobalViewModel.class);
                globalViewModel.addBook(result.getContents());

            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}