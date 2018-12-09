package com.bl.open.library_ui.util;

import android.view.View;

/**
 * Created by YySleep
 *
 * @author YySleep
 */

public class EcgUtil {

    public static int measureSize(int spec, int defaultSize) {
        int size = View.MeasureSpec.getSize(spec);
        if (View.MeasureSpec.getMode(spec) != View.MeasureSpec.EXACTLY) {
            if (size > defaultSize) {
                size = defaultSize;
            }
        }
        return size;
    }
}
