package com.toralabs.deviceinfo.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.HandlerThread;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.AudienceNetworkAds;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.adapter.MainAdapter;
import com.toralabs.deviceinfo.impClasses.BuildInfo;
import com.toralabs.deviceinfo.impClasses.ExportThread;
import com.toralabs.deviceinfo.menuItems.CustomSnackBar;
import com.toralabs.deviceinfo.menuItems.FullScreenAds;
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

public class MainActivity extends AppCompatActivity implements Runnable, ViewPager.OnPageChangeListener {
    Preferences preferences;
    RemoveAds removeAds;
    boolean bool, showRate;
    int bottom = 0;
    int themeNo, showAds = 0, themeColor;
    FullScreenAds fullScreenAds;
    public int coresCount, drawableId;
    public boolean iconAvail;
    RelativeLayout ad_banner, relBottom, relMain;
    SmartTabLayout smartTabLayout;
    ViewPager viewPager;
    String color, darkIndicator;
    boolean flag;
    BuildInfo buildInfo;
    ThemeConstant themeConstant;
    HandlerThread intentThread = new HandlerThread("intentThread");
    Handler handler;
    public long totalRAM, usedRAM, freeRam, totalSys, usedSys, totalInternal, usedInternal, totalExternal, usedExternal;
    public boolean isExtAvail;
    public String voltage, temp, level, status, memory, bandwidth, channel, cpuFamily, process;
    public int sensorCount, appCount;
    public int logoId;
    public String[] wide = new String[9];
    public String[] clearkey = new String[2];
    public String processorName, family, machine;
    public ArrayList<CpuFreqModel> cpuCoresList = new ArrayList<>();
    public ArrayList<ThermalModel> thermalList = new ArrayList<>();
    public ArrayList<ClickableModel> deviceList = new ArrayList<>();
    public ArrayList<SimpleModel> systemList = new ArrayList<>();
    public ArrayList<CpuModel> cpuList = new ArrayList<>();
    public ArrayList<SimpleModel> displayList = new ArrayList<>();
    public ArrayList<ThermalModel> codecsList = new ArrayList<>();
    public ArrayList<InputModel> inputList = new ArrayList<>();
    public ArrayList<SensorListModel> sensorList = new ArrayList<>();
    public ArrayList<TestModel> testList = new ArrayList<>();
    ActivityOptions options;
    String URL_STORE = "https://play.google.com/store/apps/details?id=com.toralabs.deviceinfo";
    Button btnRight, btnLeft;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = new Preferences(MainActivity.this);
        fullScreenAds = new FullScreenAds(MainActivity.this);
        AudienceNetworkAds.initialize(this);
        themeColor = Color.parseColor(preferences.getCircleColor());
        showRate = preferences.getRate();
        buildInfo = new BuildInfo(this);
        intentThread.start();
        handler = new Handler(intentThread.getLooper());
        handler.post(this);
        if (preferences.getMode()) {
            color = "#101011";
            darkIndicator = preferences.getCircleColor();
            flag = true;
        } else {
            color = preferences.getCircleColor();
            flag = false;
        }
        themeNo = preferences.getThemeNo();
        themeConstant = new ThemeConstant(themeNo);
        if (themeNo == 0) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(themeConstant.themeChooser());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bool = preferences.getPurchasePref();
        viewPager = findViewById(R.id.view_pager);
        ad_banner = findViewById(R.id.ad_banner);
        relMain = findViewById(R.id.relMain);
        smartTabLayout = findViewById(R.id.smartTab);
        ViewCompat.setElevation(smartTabLayout, 8);
        if (preferences.getMode()) {
            smartTabLayout.setSelectedIndicatorColors(selectedTabColor(Color.parseColor(darkIndicator)));
            smartTabLayout.setBackgroundColor(getResources().getColor(R.color.bg));
        } else {
            smartTabLayout.setBackgroundColor(Color.parseColor(color));
            smartTabLayout.setSelectedIndicatorColors(selectedTabColor(Color.parseColor(color)));
        }
        prepareViewPager(viewPager);
    }

    public void prepareViewPager(final ViewPager pager) {
        MainAdapter mainAdapter = new MainAdapter(getSupportFragmentManager(), MainActivity.this);
        pager.setAdapter(mainAdapter);
        smartTabLayout.setViewPager(pager);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.more_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.rate)
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE)));
        else if (id == R.id.share) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.putExtra(Intent.EXTRA_TEXT, "Get your complete Device Information with this amazing app.\n" + URL_STORE);
            i.setType("text/plain");
            startActivity(Intent.createChooser(i, "Share Via"));
        } else if (id == R.id.settings)
            startIntent(SettingsActivity.class);
        else if (id == R.id.about) {
            options = ActivityOptions.makeCustomAnimation(MainActivity.this, android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(new Intent(MainActivity.this, AboutActivity.class), options.toBundle());
        } else if (id == R.id.removeads) {
            if (bool) {
                CustomSnackBar customSnackBar = new CustomSnackBar(MainActivity.this, relMain);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.premium_user), Toast.LENGTH_SHORT).show();
                customSnackBar.showSnackBar(getResources().getString(R.string.premium_user));
            } else {
                removeAds = new RemoveAds(MainActivity.this, viewPager);
                removeAds.setupbillingclient();
            }
        } else if (id == R.id.export)
            export();
        else
            return super.onOptionsItemSelected(item);
        return true;
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

    public int selectedTabColor(int col) {
        int alpha = Color.alpha(col);
        int r = Math.round(Color.red(col) * 0.9f);
        int g = Math.round(Color.green(col) * 0.9f);
        int b = Math.round(Color.blue(col) * 0.9f);
        return Color.argb(alpha, Math.min(255, r), Math.min(255, g), Math.min(255, b));
    }

    public void export() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            ExportThread exportThread = new ExportThread(MainActivity.this, relMain, Color.parseColor(preferences.getCircleColor()));
            exportThread.start();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ExportThread exportThread = new ExportThread(MainActivity.this, relMain, Color.parseColor(preferences.getCircleColor()));
                exportThread.start();
            }
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

    public void startIntent(Class c) {
        Intent intent = new Intent(MainActivity.this, c);
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
        options = ActivityOptions.makeCustomAnimation(MainActivity.this, android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(intent, options.toBundle());
        finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        showAds++;
        if (!bool && showAds % 14 == 0)
            fullScreenAds.showFullScreenAd();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onBackPressed() {
        if (!showRate)
            showRateDialog();
        else
            super.onBackPressed();
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    public void showRateDialog() {
        preferences.setRate(true);
        showRate = preferences.getRate();
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        View customView = getLayoutInflater().inflate(R.layout.bottomdialog_layout, null);
        bottomSheetDialog.setContentView(customView);
        relBottom = bottomSheetDialog.findViewById(R.id.relBottom);
        textView = bottomSheetDialog.findViewById(R.id.txtStat);
        btnLeft = bottomSheetDialog.findViewById(R.id.btnLeft);
        if (btnLeft != null) {
            btnLeft.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_border));
            btnLeft.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        }
        btnRight = bottomSheetDialog.findViewById(R.id.btnRight);
        if (btnRight != null) {
            btnRight.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_bg));
            btnRight.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            btnRight.setTextColor(themeColor);
        }
        relBottom.setBackgroundColor(themeColor);
        btnRight.setText(getResources().getString(R.string.yes) + " !");
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.show();
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottom == 0) {
                    textView.setText(getResources().getString(R.string.askratestat));
                    btnLeft.setText(getResources().getString(R.string.nothanks));
                    btnRight.setText(getResources().getString(R.string.oksure));
                    bottom++;
                } else {
                    bottomSheetDialog.dismiss();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE)));
                }
            }
        });
    }
}