package com.jackie.mediapreview.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created 9/23/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class MessageUtil {
    public static void showMessage(Context context, String format) {
        Toast.makeText(context, format, Toast.LENGTH_LONG).show();
    }
}
