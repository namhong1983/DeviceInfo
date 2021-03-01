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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.toralabs.deviceinfo.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by @mrudultora
 */

public class Preferences extends Activity {
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor spEditor;

    public Preferences(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("PREF", MODE_PRIVATE);
        this.sp = context.getSharedPreferences("MAP_PREF", MODE_PRIVATE);
    }

    public void setLanguageChanged(boolean bool) {
        editor = sharedPreferences.edit();
        editor.putBoolean("isLanguageChanged", bool).apply();
    }

    public boolean getLanguageChanged() {
        return sharedPreferences.getBoolean("isLanguageChanged", false);
    }

    public boolean getPurchasePref() {
        return sharedPreferences.getBoolean("purchase_state", false);
    }

    public void setPurchasePref(boolean bool) {
        editor = sharedPreferences.edit();
        editor.putBoolean("purchase_state", bool).apply();
    }

    public String getLocalePref() {
        return sharedPreferences.getString("lang_code", "en");
    }

    public void setLocalePref(String code){
        editor = sharedPreferences.edit();
        editor.putString("lang_code", code).apply();
    }

    public boolean getMode() {
        return sharedPreferences.getBoolean("mode", false);
    }

    public void setMode(boolean bool) {
        editor = sharedPreferences.edit();
        editor.putBoolean("mode", bool).apply();
    }

    public boolean getTemp() {
        return sharedPreferences.getBoolean("temp", false);
    }

    public void setTemp(boolean bool) {
        editor = sharedPreferences.edit();
        editor.putBoolean("temp", bool).apply();
    }

    public int getThemeNo() {
        return sharedPreferences.getInt("theme", 0);
    }

    public void setThemeNo(int i) {
        editor = sharedPreferences.edit();
        editor.putInt("theme", i).apply();
    }

    public String getCircleColor() {
        return sharedPreferences.getString("circlecolor", "#2F4FE3");
    }

    public void setCircleColor(String s) {
        editor = sharedPreferences.edit();
        editor.putString("circlecolor", s).apply();
    }

    public String getGlRenderer() {
        return sharedPreferences.getString("render", "");
    }

    public void setGlRenderer(String s) {
        editor = sharedPreferences.edit();
        editor.putString("render", s).apply();
    }

    public String getGlVendor() {
        return sharedPreferences.getString("vendor", "");
    }

    public void setGlVendor(String s) {
        editor = sharedPreferences.edit();
        editor.putString("vendor", s).apply();
    }

    public String getGlVersion() {
        return sharedPreferences.getString("version", "");
    }

    public void setGlVersion(String s) {
        editor = sharedPreferences.edit();
        editor.putString("version", s).apply();
    }

    public String getGlExt() {
        return sharedPreferences.getString("extension", "");
    }

    public void setGlExt(String s) {
        editor = sharedPreferences.edit();
        editor.putString("extension", s).apply();
    }

    public String getGlExtLength() {
        return sharedPreferences.getString("ext", "");
    }

    public void setGlExtLength(String s) {
        editor = sharedPreferences.edit();
        editor.putString("ext", s).apply();
    }

    public boolean getGpu() {
        return sharedPreferences.getBoolean("gpu", false);
    }

    public void setGpu(boolean bool) {
        editor = sharedPreferences.edit();
        editor.putBoolean("gpu", bool).apply();
    }

    public void setInitialResult() {
        System.out.println("SplashActivity: setInitialResult");
        Map<String, Integer> map = new HashMap<>();
        map.put("display", 0);
        map.put("multitouch", 0);
        map.put("flashlight", 0);
        map.put("loudspeaker", 0);
        map.put("earspeaker", 0);
        map.put("earproximity", 0);
        map.put("lightsensor", 0);
        map.put("accel", 0);
        map.put("vibration", 0);
        map.put("bluetooth", 0);
        map.put("volumeup", 0);
        map.put("volumedown", 0);
        map.put("fingerprint", 0);
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            spEditor = sp.edit();
            spEditor.putInt(entry.getKey(), entry.getValue()).apply();
        }
    }

    public Map<String, ?> getResults() {
        System.out.println("SplashActivity: getResults");
        return sp.getAll();
    }

    public void setValue(String key, int value) {
        spEditor = sp.edit();
        spEditor.putInt(key, value).apply();
    }

    public void setInitial(boolean value) {
        editor = sharedPreferences.edit();
        editor.putBoolean("initial", value).apply();
    }

    public boolean getInitial() {
        return sharedPreferences.getBoolean("initial", false);
    }

    public boolean getRate() {
        return sharedPreferences.getBoolean("rate", false);
    }

    public void setRate(boolean bool) {
        editor = sharedPreferences.edit();
        editor.putBoolean("rate", bool).apply();
    }
}