package com.tobiasfried.rasa;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Time Zone
        AndroidThreeTen.init(this);
    }

}
