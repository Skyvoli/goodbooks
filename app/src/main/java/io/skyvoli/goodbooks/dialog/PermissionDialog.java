package io.skyvoli.goodbooks.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class PermissionDialog extends AppCompatDialogFragment {

    private final String title;
    private final String message;
    private final boolean canceledOnTouchOutside;
    private final NoticeDialogListener listener;

    public PermissionDialog(String title, String message, boolean canceledOnTouchOutside, NoticeDialogListener listener) {
        this.title = title;
        this.message = message;
        this.canceledOnTouchOutside = canceledOnTouchOutside;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> listener.onDialogPositiveClick())
                .setNegativeButton("No", (dialog, which) -> listener.onDialogNegativeClick());
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        return dialog;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
    }
}