package com.academy.fundamentals.Movies.Movies.CommonData;

import android.view.View;

public interface ItemClickListener {
    void onClick (View view, int position);
    void onLongClick (View view, int position);
}
