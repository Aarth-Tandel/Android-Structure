package com.wozart.route_3;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.amazonaws.mobile.AWSMobileClient;

/**
 * Created by wozart on 12/10/17.
 */

public class Application extends MultiDexApplication {
    private static final String LOG_TAG = Application.class.getSimpleName();

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "Application.onCreate - Initializing application...");
        super.onCreate();
        initializeApplication();
        Log.d(LOG_TAG, "Application.onCreate - Application initialized OK");
    }

    private void initializeApplication() {
        AWSMobileClient.initializeMobileClientIfNecessary(getApplicationContext());

        // ...Put any application-specific initialization logic here...
    }
}
