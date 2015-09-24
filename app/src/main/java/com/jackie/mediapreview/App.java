package com.jackie.mediapreview;

import android.app.Application;

import com.jackie.mediapreview.utils.UniversalImageLoaderUtil;

/**
 * Created 9/24/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class App extends Application {
    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();
        UniversalImageLoaderUtil.initImageLoader(this);
    }
}
