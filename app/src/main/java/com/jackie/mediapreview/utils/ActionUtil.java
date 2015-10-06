package com.jackie.mediapreview.utils;

import android.content.Intent;

/**
 * Created 10/6/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class ActionUtil {
    public static boolean isSelected(String action){
        boolean result = false;
        if (Intent.ACTION_PICK.equals(action)) {
            result = true;
        } else if (Intent.ACTION_MAIN.equals(action)) {
            result = false;
        }
        return result;
    }
}
