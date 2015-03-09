package com.codepath.android.lollipopexercise.app;

import android.app.Application;
import android.content.Context;

import com.codepath.android.lollipopexercise.models.Contact;

public class LollipopExerciseApp extends Application {
    private static Context _appContext;

    @Override
    public void onCreate()
    {
        super.onCreate();
        _appContext = this;
        Contact.addContacts(this);
    }

    public static Context getAppContext()
    {
        return _appContext;
    }
}
