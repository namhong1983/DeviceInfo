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
package com.toralabs.deviceinfo.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.impClasses.ExportThread;
import com.toralabs.deviceinfo.menuItems.ChangeLocale;
import com.toralabs.deviceinfo.menuItems.CustomSnackBar;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.menuItems.RemoveAds;
import com.toralabs.deviceinfo.menuItems.ThemeConstant;
import com.toralabs.deviceinfo.models.ClickableModel;
import com.toralabs.deviceinfo.models.CpuFreqModel;
import com.toralabs.deviceinfo.models.CpuModel;
import com.toralabs.deviceinfo.models.InputModel;
import com.toralabs.deviceinfo.models.SensorListModel;
import com.toralabs.deviceinfo.models.SimpleModel;
import com.toralabs.deviceinfo.models.TestModel;
import com.toralabs.deviceinfo.models.ThermalModel;

import java.util.ArrayList;

import petrov.kristiyan.colorpicker.ColorPicker;

/**
 * Created by @mrudultora
 */

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, Runnable {
    HandlerThread intentThread = new HandlerThread("intentThread");
    Handler intentHandler;
    RelativeLayout rel_theme, rel_color, rel_temp, rel_export, rel_rate, rel_removeads, rel_feedback, rel_app_version, rel_language;
    ScrollView rel_settings;
    Preferences preferences;
    RemoveAds removeAds;
    TextView text_themename, text_version, text_temp, text_lang;
    ThemeConstant themeConstant;
    boolean bool;
    int themeNo, coresCount, drawableId;
    boolean flag = false, iconAvail;
    long totalRAM, usedRAM, freeRam, totalSys, usedSys, totalInternal, usedInternal, totalExternal, usedExternal;
    boolean isExtAvail;
    String voltage, temp, level, status, memory, bandwidth, channel, cpuFamily, process;
    int sensorCount, appCount;
    int logoId;
    ChangeLocale changeLocale;
    String[] wide = new String[9];
    String[] clearkey = new String[2];
    String processorName, family, machine;
    int color, pos;
    ArrayList<String> colors = new ArrayList<>();
    String URL_STORE = "https://play.google.com/store/apps/details?id=com.toralabs.deviceinfo";
    ArrayList<CpuFreqModel> cpuCoresList = new ArrayList<>();
    ArrayList<ThermalModel> thermalList = new ArrayList<>();
    ArrayList<ClickableModel> deviceList = new ArrayList<>();
    ArrayList<SimpleModel> systemList = new ArrayList<>();
    ArrayList<CpuModel> cpuList = new ArrayList<>();
    ArrayList<SimpleModel> displayList = new ArrayList<>();
    ArrayList<ThermalModel> codecsList = new ArrayList<>();
    ArrayList<InputModel> inputList = new ArrayList<>();
    ArrayList<SensorListModel> sensorList = new ArrayList<>();
    ArrayList<TestModel> testList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new Preferences(SettingsActivity.this);
        changeLocale = new ChangeLocale(SettingsActivity.this);
        color = Color.parseColor(preferences.getCircleColor());
        themeNo = preferences.getThemeNo();
        themeConstant = new ThemeConstant(themeNo);
        if (themeNo == 0) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(themeConstant.themeChooser());
        }
        changeLocale.setLocale(preferences.getLocalePref());
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.circle);
        Drawable wrappedDrawable = null;
        if (unwrappedDrawable != null) {
            wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        }
        if (themeNo != 0) {
            if (wrappedDrawable != null) {
                DrawableCompat.setTint(wrappedDrawable, color);
            }
        } else {
            if (wrappedDrawable != null) {
                DrawableCompat.setTint(wrappedDrawable, Color.parseColor("#2F4FE3"));
            }
        }
        setContentView(R.layout.activity_settings);
        intentThread.start();
        intentHandler = new Handler(intentThread.getLooper());
        intentHandler.post(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.setting));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        colors.add("#f44236");
        colors.add("#ea1e63");
        colors.add("#9a28b1");
        colors.add("#683ab7");
        colors.add("#2F4FE3");
        colors.add("#2295f0");
        colors.add("#04a8f5");
        colors.add("#00bed2");
        colors.add("#009788");
        colors.add("#4cb050");
        colors.add("#ff9700");
        colors.add("#FFC000");
        colors.add("#D2E41D");
        colors.add("#fe5722");
        colors.add("#795547");

        rel_color = findViewById(R.id.rel_color);
        rel_export = findViewById(R.id.rel_export);
        rel_rate = findViewById(R.id.rel_rate);
        rel_removeads = findViewById(R.id.rel_removeads);
        rel_temp = findViewById(R.id.rel_temp);
        rel_theme = findViewById(R.id.rel_theme);
        rel_feedback = findViewById(R.id.rel_feedback);
        rel_app_version = findViewById(R.id.rel_app_version);
        rel_language = findViewById(R.id.rel_language);
        text_version = findViewById(R.id.text_version);
        text_themename = findViewById(R.id.text_themename);
        text_temp = findViewById(R.id.text_temp);
        text_lang = findViewById(R.id.text_lang);
        rel_settings = findViewById(R.id.rel_settings);

        rel_color.setOnClickListener(this);
        rel_export.setOnClickListener(this);
        rel_rate.setOnClickListener(this);
        rel_removeads.setOnClickListener(this);
        rel_temp.setOnClickListener(this);
        rel_theme.setOnClickListener(this);
        rel_feedback.setOnClickListener(this);
        rel_app_version.setOnClickListener(this);
        rel_language.setOnClickListener(this);

        bool = preferences.getPurchasePref();
        if (preferences.getMode()) {
            flag = true;
            text_themename.setText(getResources().getString(R.string.dark));
        }
        if (preferences.getTemp()) {
            text_temp.setText(getResources().getString(R.string.fahren));
        }
        text_version.setText(com.toralabs.deviceinfo.BuildConfig.VERSION_NAME);
        String[] langArray = getResources().getStringArray(R.array.languageCodeList);
        for (int i = 0; i < langArray.length; i++) {
            if (langArray[i].equals(preferences.getLocalePref())) {
                pos = i;
                break;
            }
        }
        text_lang.setText(getResources().getStringArray(R.array.languageList)[pos]);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @SuppressLint("IntentReset")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rel_color) {
            showColorDialog();
        } else if (id == R.id.rel_export) {
            export();
        } else if (id == R.id.rel_theme) {
            showThemeDialog();
        } else if (id == R.id.rel_temp) {
            showTempDialogBox();
        } else if (id == R.id.rel_language) {
            chooseLanguageDialog();
        } else if (id == R.id.rel_removeads) {
            if (bool) {
                CustomSnackBar customSnackBar = new CustomSnackBar(SettingsActivity.this, rel_settings);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.premium_user), Toast.LENGTH_SHORT).show();
                customSnackBar.showSnackBar(getResources().getString(R.string.premium_user));
            } else {
                removeAds = new RemoveAds(SettingsActivity.this, rel_settings);
                removeAds.setupbillingclient();
            }
        } else if (id == R.id.rel_rate) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE)));
        } else if (id == R.id.rel_feedback) {
            Intent contact_intent = new Intent(Intent.ACTION_SENDTO);
            contact_intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"toralabs24@gmail.com"});
            contact_intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for DeviceInfo App");
            contact_intent.setType("message/rfc822");
            contact_intent.setData(Uri.parse("mailto:"));
            startActivity(contact_intent);
        } else if (id == R.id.rel_app_version) {
            Toast.makeText(getApplicationContext(), "App Version is " + com.toralabs.deviceinfo.BuildConfig.VERSION_NAME, Toast.LENGTH_LONG).show();
        }
    }

    private void export() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            ExportThread exportThread = new ExportThread(SettingsActivity.this, rel_settings, color);
            exportThread.start();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
    }

    private void showColorDialog() {
        final ColorPicker colorPicker = new ColorPicker(SettingsActivity.this);
        colorPicker.setColors(colors).setColumns(5).setDefaultColorButton(Color.parseColor(preferences.getCircleColor())).setRoundColorButton(true).setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position, int color) {
                preferences.setThemeNo(position + 1);
                preferences.setCircleColor(colors.get(position));
                recreate();
            }

            @Override
            public void onCancel() {

            }
        }).show();
    }

    private void showThemeDialog() {
        int currentThemePos = 0;
        final String[] themeList = new String[]{getResources().getString(R.string.light), getResources().getString(R.string.dark)};
        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle(getString(R.string.theme));
        if (preferences.getMode())
            currentThemePos = 1;
        builder.setSingleChoiceItems(themeList, currentThemePos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    preferences.setMode(false);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    preferences.setMode(true);
                }
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color);
    }

    private void showTempDialogBox() {
        int currentTempPos = 0;
        final String[] themeList = new String[]{getResources().getString(R.string.cels), getResources().getString(R.string.fahren)};
        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle(getString(R.string.theme));
        if (preferences.getTemp())
            currentTempPos = 1;
        builder.setSingleChoiceItems(themeList, currentTempPos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    preferences.setTemp(false);
                    text_temp.setText(getResources().getString(R.string.cels));
                } else {
                    preferences.setTemp(true);
                    text_temp.setText(getResources().getString(R.string.fahren));
                }
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color);
    }


    private void chooseLanguageDialog() {
        final String[] languageList = getResources().getStringArray(R.array.languageList);
        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle(getString(R.string.choose_language));
        builder.setSingleChoiceItems(languageList, pos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!getResources().getStringArray(R.array.languageCodeList)[which].equals(preferences.getLocalePref())) {
                    changeLocale.setLocale(getResources().getStringArray(R.array.languageCodeList)[which]);
                    preferences.setLanguageChanged(true);
                    recreate();
                }
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ExportThread exportThread = new ExportThread(SettingsActivity.this, rel_settings, color);
                exportThread.start();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (flag) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (preferences.getLanguageChanged()) {
            startActivity(new Intent(SettingsActivity.this, SplashActivity.class));
            finishAffinity();
        } else {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.putExtra("coresCount", coresCount);
            intent.putExtra("drawableId", drawableId);
            intent.putExtra("iconAvail", iconAvail);
            intent.putExtra("totalRAM", totalRAM);
            intent.putExtra("freeRam", freeRam);
            intent.putExtra("usedRAM", usedRAM);
            intent.putExtra("totalSys", totalSys);
            intent.putExtra("usedSys", usedSys);
            intent.putExtra("totalInternal", totalInternal);
            intent.putExtra("usedInternal", usedInternal);
            intent.putExtra("isExtAvail", isExtAvail);
            if (isExtAvail) {
                intent.putExtra("totalExternal", totalExternal);
                intent.putExtra("usedExternal", usedExternal);
            }
            intent.putExtra("level", level);
            intent.putExtra("voltage", voltage);
            intent.putExtra("temp", temp);
            intent.putExtra("status", status);
            intent.putExtra("appCount", appCount);
            intent.putExtra("sensorCount", sensorCount);
            intent.putExtra("logoId", logoId);
            intent.putExtra("wide", wide);
            intent.putExtra("clearkey", clearkey);
            intent.putExtra("processorName", processorName);
            intent.putExtra("machine", machine);
            intent.putExtra("family", family);
            intent.putExtra("memory", memory);
            intent.putExtra("bandwidth", bandwidth);
            intent.putExtra("channel", channel);
            intent.putExtra("cpuFamily", cpuFamily);
            intent.putExtra("process", process);
            intent.putParcelableArrayListExtra("cpuCoresList", cpuCoresList);
            intent.putParcelableArrayListExtra("thermalList", thermalList);
            intent.putParcelableArrayListExtra("deviceList", deviceList);
            intent.putParcelableArrayListExtra("systemList", systemList);
            intent.putParcelableArrayListExtra("cpuList", cpuList);
            intent.putParcelableArrayListExtra("codecsList", codecsList);
            intent.putParcelableArrayListExtra("displayList", displayList);
            intent.putParcelableArrayListExtra("inputList", inputList);
            intent.putParcelableArrayListExtra("sensorList", sensorList);
            intent.putParcelableArrayListExtra("testList", testList);
            ActivityOptions options =
                    ActivityOptions.makeCustomAnimation(SettingsActivity.this, android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent, options.toBundle());
            finish();
        }
    }

    @Override
    public void run() {
        if (getIntent() != null) {
            coresCount = getIntent().getIntExtra("coresCount", 2);
            drawableId = getIntent().getIntExtra("drawableId", 0);
            iconAvail = getIntent().getBooleanExtra("iconAvail", false);
            totalRAM = getIntent().getLongExtra("totalRAM", 0);
            freeRam = getIntent().getLongExtra("freeRam", 0);
            usedRAM = getIntent().getLongExtra("usedRAM", 0);
            totalSys = getIntent().getLongExtra("totalSys", 0);
            usedSys = getIntent().getLongExtra("usedSys", 0);
            totalInternal = getIntent().getLongExtra("totalInternal", 0);
            usedInternal = getIntent().getLongExtra("usedInternal", 0);
            isExtAvail = getIntent().getBooleanExtra("isExtAvail", false);
            if (isExtAvail) {
                totalExternal = getIntent().getLongExtra("totalExternal", 0);
                usedExternal = getIntent().getLongExtra("usedExternal", 0);
            }
            level = getIntent().getStringExtra("level");
            voltage = getIntent().getStringExtra("voltage");
            temp = getIntent().getStringExtra("temp");
            status = getIntent().getStringExtra("status");
            appCount = getIntent().getIntExtra("appCount", 0);
            sensorCount = getIntent().getIntExtra("sensorCount", 0);
            logoId = getIntent().getIntExtra("logoId", 0);
            wide = getIntent().getStringArrayExtra("wide");
            clearkey = getIntent().getStringArrayExtra("clearkey");
            processorName = getIntent().getStringExtra("processorName");
            family = getIntent().getStringExtra("family");
            machine = getIntent().getStringExtra("machine");
            memory = getIntent().getStringExtra("memory");
            bandwidth = getIntent().getStringExtra("bandwidth");
            channel = getIntent().getStringExtra("channel");
            cpuFamily = getIntent().getStringExtra("cpuFamily");
            process = getIntent().getStringExtra("process");
            cpuCoresList = getIntent().getParcelableArrayListExtra("cpuCoresList");
            thermalList = getIntent().getParcelableArrayListExtra("thermalList");
            deviceList = getIntent().getParcelableArrayListExtra("deviceList");
            systemList = getIntent().getParcelableArrayListExtra("systemList");
            cpuList = getIntent().getParcelableArrayListExtra("cpuList");
            codecsList = getIntent().getParcelableArrayListExtra("codecsList");
            inputList = getIntent().getParcelableArrayListExtra("inputList");
            displayList = getIntent().getParcelableArrayListExtra("displayList");
            sensorList = getIntent().getParcelableArrayListExtra("sensorList");
            testList = getIntent().getParcelableArrayListExtra("testList");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (intentThread != null)
            intentThread.quit();
    }
}