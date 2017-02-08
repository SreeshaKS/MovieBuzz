package com.sreesha.android.moviebuzz.Settings;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.firebase.client.Firebase;

/**
 * Created by Sreesha on 10-08-2016.
 */
public class MovieBuzzApplication extends Application {
    public static boolean languagePreferenceChanged = false;
    private static MovieBuzzApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyApplication", "MovieBuzzApplication");
        Firebase.setAndroidContext(this);
        sInstance = this;
    }

    public static MovieBuzzApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }
}
