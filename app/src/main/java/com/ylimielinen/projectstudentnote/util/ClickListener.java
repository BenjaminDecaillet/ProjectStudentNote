package com.ylimielinen.projectstudentnote.util;

import android.view.View;

/**
 * Created by kb on 11.11.2017.
 */

public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
