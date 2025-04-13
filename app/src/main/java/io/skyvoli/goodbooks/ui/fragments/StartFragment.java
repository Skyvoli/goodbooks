package io.skyvoli.goodbooks.ui.fragments;

import androidx.fragment.app.Fragment;

public abstract class StartFragment extends Fragment {

    protected boolean shouldConfigureUi = true;

    public abstract void configureFragment();

    @Override
    public void onPause() {
        super.onPause();
        shouldConfigureUi = true;
    }
}