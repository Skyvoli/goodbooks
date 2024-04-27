package io.skyvoli.goodbooks.dialog;

public abstract class OnlyPositiveListener implements NoticeDialogListener {

    @Override
    public void onDialogNegativeClick() {
        //Do nothing
    }
}