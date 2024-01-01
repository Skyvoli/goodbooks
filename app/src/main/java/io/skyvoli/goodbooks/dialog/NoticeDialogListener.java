package io.skyvoli.goodbooks.dialog;

import androidx.fragment.app.DialogFragment;

public interface NoticeDialogListener {
    void onDialogPositiveClick(DialogFragment dialog);
    void onDialogNegativeClick(DialogFragment dialog);
}
