package com.toralabs.deviceinfo.menuItems;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public class ChangeLocale {
    Context context;
    Preferences preferences;

    public ChangeLocale(Context context) {
        this.context = context;
        preferences = new Preferences(context);
    }

    public void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.locale = locale;
        preferences.setLocalePref(langCode);
        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
    }
}
