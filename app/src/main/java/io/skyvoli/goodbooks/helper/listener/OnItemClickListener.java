package io.skyvoli.goodbooks.helper.listener;

import android.view.View;

public interface OnItemClickListener {

    View.OnClickListener onClick();

    View.OnLongClickListener onLongClick();
}