package com.toralabs.deviceinfo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.impClasses.BuildInfo;
import com.toralabs.deviceinfo.impClassMethods.CodecsMethod;
import com.toralabs.deviceinfo.impClassMethods.CpuMethod;
import com.toralabs.deviceinfo.impClassMethods.DeviceMethod;
import com.toralabs.deviceinfo.impClassMethods.DisplayMethod;
import com.toralabs.deviceinfo.impClassMethods.InputDeviceMethod;
import com.toralabs.deviceinfo.impClassMethods.SensorListMethod;
import com.toralabs.deviceinfo.impClassMethods.SystemMethod;
import com.toralabs.deviceinfo.impClassMethods.TestMethod;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.ClickableModel;
import com.toralabs.deviceinfo.models.CpuFreqModel;
import com.toralabs.deviceinfo.models.CpuModel;
import com.toralabs.deviceinfo.models.InputModel;
import com.toralabs.deviceinfo.models.SensorListModel;
import com.toralabs.deviceinfo.models.SimpleModel;
import com.toralabs.deviceinfo.models.TestModel;
import com.toralabs.deviceinfo.models.ThermalModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SplashActivity extends AppCompatActivity implements Runnable, Handler.Callback, GLSurfaceView.Renderer {

    RelativeLayout relativeLayout;
    Preferences preferences;
    String color;
    Handler handler, taskHandler;
    HandlerThread handlerThread = new HandlerThread("MultipleTaskThread");
    int logoId;
    String[] wide = new String[9];
    String[] clearkey = new String[2];
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
    String vendor, version, renderer, processorName, family, machine;
    String[] extensions;
    BuildInfo buildInfo;
    int coresCount, drawableId;
    boolean iconAvail;
    Message message;
    Bundle bundle;
    ProgressBar progressSplash;
    PackageManager packageManager;
    long totalRAM, usedRAM, freeRam, totalSys, usedSys, totalInternal, usedInternal, totalExternal, usedExternal;
    ActivityManager.MemoryInfo memoryInfo;
    ActivityManager manager;
    boolean isExtAvail;
    String voltage, temp, level, status, memory, bandwidth, channel, process, cpuFamily;
    int sensorCount, appCount;
    RelativeLayout rlroot;
    GLSurfaceView glSurfaceView;
    List<ApplicationInfo> packageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = new Preferences(SplashActivity.this);
        buildInfo = new BuildInfo(SplashActivity.this);
        if (preferences.getMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        color = preferences.getCircleColor();
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(color));
        }
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        bundle = new Bundle();
        message = new Message();
        handlerThread.start();
        taskHandler = new Handler(handlerThread.getLooper());
        relativeLayout = findViewById(R.id.rel_main);
        relativeLayout.setBackgroundColor(Color.parseColor(color));
        progressSplash = findViewById(R.id.progressSplash);
        rlroot = findViewById(R.id.relroot);
        taskHandler.post(this);
        handler = new Handler(this);
    }

    @Override
    public void run() {
        if (!preferences.getInitial()) {
            preferences.setInitialResult();
            preferences.setInitial(true);
        }
        coresCount = buildInfo.getCoresCount();
        for (int i = 0; i < coresCount; i++) {
            cpuCoresList.add(new CpuFreqModel("Core " + i, buildInfo.getRunningCpu(i, false)));
        }
        buildInfo.thermal();
        thermalList = buildInfo.getThermalList();
        bundle.putInt("progress", 20);
        message.setData(bundle);
        handler.sendMessage(message);
        message = null;
        message = new Message();
        dashboardInfo();
        DeviceMethod deviceMethod = new DeviceMethod(SplashActivity.this);
        deviceList = deviceMethod.getDeviceList();
        deviceMethod.getIcon(deviceList.get(1).getDesc().toLowerCase());
        drawableId = deviceMethod.getDrawableId();
        iconAvail = deviceMethod.isIconAvail();
        TestMethod testMethod = new TestMethod(SplashActivity.this);
        testList = testMethod.getTestList();
        bundle.putInt("progress", 40);
        message.setData(bundle);
        handler.sendMessage(message);
        message = null;
        message = new Message();
        SystemMethod systemMethod = new SystemMethod(SplashActivity.this);
        systemList = systemMethod.getSystemList();
        wide = systemMethod.getWide();
        clearkey = systemMethod.getClearkey();
        logoId = systemMethod.getLogoId();
        CpuMethod cpuMethod = new CpuMethod(SplashActivity.this);
        cpuList = cpuMethod.getCpuList();
        memory = cpuMethod.getMemory();
        bandwidth = cpuMethod.getBandwidth();
        channel = cpuMethod.getChannel();
        cpuFamily = cpuMethod.getCpuFamily();
        process = cpuMethod.getProcess();
        if (preferences.getGpu()) {
            cpuList.add(new CpuModel(getResources().getString(R.string.gpu_render), preferences.getGlRenderer(), "renderer"));
            cpuList.add(new CpuModel(getResources().getString(R.string.gpu_version), preferences.getGlVersion(), "version"));
            cpuList.add(new CpuModel(getResources().getString(R.string.gpu_vendor), preferences.getGlVendor(), "vendor"));
            cpuList.add(new CpuModel(getResources().getString(R.string.gpu_extension), preferences.getGlExtLength() + " (" + getResources().getString(R.string.taphere) + ")", "extensions"));
        } else {
            bundle.putInt("addview", 1000);
            message.setData(bundle);
            handler.sendMessage(message);
            message = null;
            message = new Message();
        }
        processorName = cpuMethod.getProcessor();
        machine = cpuMethod.getMachine();
        family = cpuMethod.getFamily();
        bundle.putInt("progress", 80);
        message.setData(bundle);
        handler.sendMessage(message);
        message = null;
        message = new Message();
        CodecsMethod codecsMethod = new CodecsMethod();
        DisplayMethod displayMethod = new DisplayMethod(SplashActivity.this);
        InputDeviceMethod inputDeviceMethod = new InputDeviceMethod(SplashActivity.this);
        SensorListMethod sensorListMethod = new SensorListMethod(SplashActivity.this);
        codecsList = codecsMethod.getCodecsList();
        displayList = displayMethod.getDisplayList();
        inputList = inputDeviceMethod.getInputList();
        sensorList = sensorListMethod.getSensorList();
        bundle.putInt("progress", 100);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        progressSplash.setProgress(msg.getData().getInt("progress"));
        if (msg.getData().getInt("addview") == 1000) {
            glSurfaceView = new GLSurfaceView(SplashActivity.this);
            glSurfaceView.setRenderer(this);
            rlroot.setVisibility(View.VISIBLE);
            rlroot.removeAllViews();
            rlroot.addView(glSurfaceView);
        }
        if (msg.getData().getInt("progress") == 100) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
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
            startActivity(intent);
            finish();
        }
        return true;
    }

    public void dashboardInfo() {
        manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        packageManager = getPackageManager();
        memoryInfo = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(memoryInfo);
        totalRAM = memoryInfo.totalMem / (1024 * 1024);
        freeRam = memoryInfo.availMem / (1024 * 1024);
        usedRAM = totalRAM - freeRam;
        totalSys = buildInfo.getTotalStorageInfo("/system");
        usedSys = buildInfo.getUsedStorageInfo("/system");
        totalInternal = buildInfo.getTotalStorageInfo("/data");
        usedInternal = buildInfo.getUsedStorageInfo("/data");
        File[] files = ContextCompat.getExternalFilesDirs(SplashActivity.this, null);
        if (files.length > 1 && files[0] != null && files[1] != null) {
            isExtAvail = true;
            totalExternal = buildInfo.getTotalStorageInfo(files[1].getPath());
            usedExternal = buildInfo.getUsedStorageInfo(files[1].getPath());
        }
        level = buildInfo.getBatteryLevel();
        voltage = buildInfo.getVoltage();
        temp = buildInfo.getBatteryTemp();
        status = buildInfo.getBatteryStatus();
        packageList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        appCount = packageList.size();
        sensorCount = buildInfo.getSensorsCount();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handlerThread != null)
            handlerThread.quit();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        renderer = gl.glGetString(GL10.GL_RENDERER);
        vendor = gl.glGetString(GL10.GL_VENDOR);
        version = gl.glGetString(GL10.GL_VERSION);
        extensions = gl.glGetString(GL10.GL_EXTENSIONS).split(" ");
        cpuList.add(new CpuModel(getResources().getString(R.string.gpu_render), renderer, "renderer"));
        cpuList.add(new CpuModel(getResources().getString(R.string.gpu_version), version, "version"));
        cpuList.add(new CpuModel(getResources().getString(R.string.gpu_vendor), vendor, "vendor"));
        if (extensions != null) {
            cpuList.add(new CpuModel(getResources().getString(R.string.gpu_extension), extensions.length + " (" + getResources().getString(R.string.taphere) + ")", "extensions"));
            preferences.setGlExtLength(String.valueOf(extensions.length));
            preferences.setGlExt(Arrays.toString(extensions));
        }
        preferences.setGlVersion(version);
        preferences.setGlVendor(vendor);
        preferences.setGlRenderer(renderer);
        preferences.setGpu(true);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
}