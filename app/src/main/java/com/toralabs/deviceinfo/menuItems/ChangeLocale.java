/*
Copyright 2020 Mrudul Tora (ToraLabs)
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.toralabs.deviceinfo.menuItems;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * Created by @mrudultora
 */

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
        configuration.setLayoutDirection(locale);
        preferences.setLocalePref(langCode);
        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
    }
}
