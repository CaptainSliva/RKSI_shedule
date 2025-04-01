package com.example.rksishedule.utils;


import android.content.Context;
import android.util.AttributeSet;


public class MySpinner extends androidx.appcompat.widget.AppCompatSpinner {
    OnItemSelectedListener listener;

    public MySpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        if (listener != null) {
            listener.onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    public void setOnItemSelectedEvenIfUnchangedListener(
            OnItemSelectedListener listener) {
        this.listener = listener;
    }
}
