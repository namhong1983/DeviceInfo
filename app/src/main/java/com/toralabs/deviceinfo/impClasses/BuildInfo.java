package com.toralabs.deviceinfo.impClasses;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.BatteryManager;
import android.os.Build;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Display;

import androidx.core.app.ActivityCompat;
import androidx.core.content.pm.PackageInfoCompat;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.ThermalModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.telephony.TelephonyManager.PHONE_TYPE_CDMA;
import static android.telephony.TelephonyManager.PHONE_TYPE_GSM;
import static android.telephony.TelephonyManager.PHONE_TYPE_NONE;
import static android.telephony.TelephonyManager.PHONE_TYPE_SIP;


public class BuildInfo {
    private final Context context;
    boolean unit;
    Preferences preferences;
    CameraManager cameraManager;
    ArrayList<ThermalModel> thermalList = new ArrayList<>();

    public BuildInfo(Context context) {
        this.context = context;
        preferences = new Preferences(context);
        unit = preferences.getTemp();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        }
    }

    public String getNetworkName() {
        StringBuilder name = new StringBuilder();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT > 22) {
            SubscriptionManager manager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                List<SubscriptionInfo> info = manager.getActiveSubscriptionInfoList();
                if (info != null) {
                    for (SubscriptionInfo subscriptionInfo : info) {
                        name.append(context.getResources().getString(R.string.netop)).append(" : ").append(subscriptionInfo.getCarrierName()).append("\n");
                    }
                } else
                    name.append(context.getResources().getString(R.string.netop)).append(" : ").append(context.getResources().getString(R.string.nosim)).append("\n");
            }
        } else {
            if (telephonyManager.getNetworkOperatorName() != null)
                name.append(context.getResources().getString(R.string.netop)).append(" : ").append(telephonyManager.getNetworkOperatorName()).append("\n");
        }
        return name.toString();
    }

    public String getPhoneType() {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String devicetype = "";
        switch (manager.getPhoneType()) {
            case PHONE_TYPE_NONE:
                devicetype = "NONE";
                break;
            case PHONE_TYPE_CDMA:
                devicetype = "CDMA";
                break;
            case PHONE_TYPE_GSM:
                devicetype = "GSM";
                break;
            case PHONE_TYPE_SIP:
                devicetype = "SIP";
                break;
        }
        return devicetype;
    }

    public String getCpuGovernor() {
        BufferedReader reader;
        String line;
        try {
            File file = new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor");
            reader = new BufferedReader(new FileReader(file));
            line = reader.readLine();
            reader.close();
        } catch (IOException e) {
            line = context.getResources().getString(R.string.unknown);
            e.printStackTrace();
        }
        return line;
    }

    public String getCpuDriver() {
        BufferedReader reader;
        String line;
        try {
            File file = new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_driver");
            reader = new BufferedReader(new FileReader(file));
            line = reader.readLine();
            reader.close();
        } catch (IOException e) {
            line = context.getResources().getString(R.string.unknown);
            e.printStackTrace();
        }
        return line;
    }

    public String getRunningCpuString() {
        StringBuilder cpuBuilder = new StringBuilder();
        String totalRunning;
        for (int i = 0; i < getCoresCount(); i++) {
            cpuBuilder.append("   Core ").append(i).append("       ").append(getRunningCpu(i, true)).append("\n\n");
        }
        totalRunning = cpuBuilder.toString();
        return totalRunning.substring(0, totalRunning.length() - 2);
    }

    public String getUsage() {
        int c = 0;
        for (int i = 0; i < getCoresCount(); i++) {
            c += (int) (getCpuUsage(i) * 100);
        }
        return c / getCoresCount() + "%";
    }

    public float getCpuUsage(int i) {
        Process process1, process2;
        BufferedReader reader1, reader2;
        int freq = 0, maxFreq = 0;
        try {
            process1 = Runtime.getRuntime().exec("cat /sys/devices/system/cpu/cpu" + i + "/cpufreq/scaling_cur_freq");
            reader1 = new BufferedReader(new InputStreamReader(process1.getInputStream()));
            process2 = Runtime.getRuntime().exec("cat /sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_max_freq");
            reader2 = new BufferedReader(new InputStreamReader(process2.getInputStream()));
            String line1, line2;
            if ((line1 = reader1.readLine()) != null) {
                freq = Integer.parseInt(line1) / 1000;
            }
            if ((line2 = reader2.readLine()) != null) {
                maxFreq = Integer.parseInt(line2) / 1000;
            }
            process1.destroy();
            reader1.close();
            process2.destroy();
            reader2.close();
        } catch (IOException e) {
            e.getMessage();
        }
        if (maxFreq == 0)
            return 0.5f;
        return (float) freq / maxFreq;
    }

    public String getCpuFrequency() {
        int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
        int minFreq, maxFreq;
        Process process1, process2;
        BufferedReader reader1, reader2;
        for (int i = 0; i < getCoresCount(); i++) {
            try {
                process1 = Runtime.getRuntime().exec("cat /sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_min_freq");
                reader1 = new BufferedReader(new InputStreamReader(process1.getInputStream()));
                process2 = Runtime.getRuntime().exec("cat /sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_max_freq");
                reader2 = new BufferedReader(new InputStreamReader(process2.getInputStream()));
                String line1, line2;
                if ((line1 = reader1.readLine()) != null) {
                    minFreq = Integer.parseInt(line1) / 1000;
                    if (min >= minFreq)
                        min = minFreq;
                }
                if ((line2 = reader2.readLine()) != null) {
                    maxFreq = Integer.parseInt(line2) / 1000;
                    if (max <= maxFreq)
                        max = maxFreq;
                }
                process1.destroy();
                reader1.close();
                process2.destroy();
                reader2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (min != Integer.MAX_VALUE && max != Integer.MIN_VALUE)
            return min + " Mhz" + " - " + max + " Mhz";
        return context.getResources().getString(R.string.unknown);
    }

    @SuppressLint("DefaultLocale")
    public String getCpuArchitecture() {
        StringBuilder builder = new StringBuilder();
        int cores = getCoresCount();
        if (cores <= 4) {
            Process process;
            BufferedReader reader;
            try {
                process = Runtime.getRuntime().exec("cat /sys/devices/system/cpu/cpu" + 0 + "/cpufreq/cpuinfo_max_freq");
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                float f = 0f;
                if ((line = reader.readLine()) != null) {
                    f = Float.parseFloat(line) / 1000000f;
                }
                builder.append(cores).append(" x ").append(String.format("%.2f", f)).append(" GHz" + "\n");
            } catch (IOException e) {
                e.getMessage();
            }
        } else {
            Process process;
            BufferedReader reader;
            float[] arr = new float[cores];
            int i = 0;
            while (i < cores) {
                try {
                    process = Runtime.getRuntime().exec("cat /sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_max_freq");
                    reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    float f;
                    if ((line = reader.readLine()) != null) {
                        f = Float.parseFloat(line) / 1000000f;
                        arr[i] = f;
                    }
                } catch (IOException e) {
                    e.getMessage();
                }
                i++;
            }
            int count = 0;
            float freq = 0f;
            for (int j = 0; j < arr.length; j++) {
                if (j == 0) {
                    freq = arr[0];
                    count++;
                } else if (j >= 1) {
                    if (freq == arr[j])
                        count++;
                    else {
                        builder.append(count).append(" x ").append(String.format("%.2f", freq)).append(" GHz" + "\n");
                        freq = arr[j];
                        count = 1;
                    }
                }
                if (j == arr.length - 1)
                    builder.append(count).append(" x ").append(String.format("%.2f", freq)).append(" GHz" + "\n");
            }
        }
        if (builder.toString().length() > 0)
            return builder.toString().substring(0, builder.toString().length() - 1);
        return context.getResources().getString(R.string.unknown);
    }

    @SuppressLint("DefaultLocale")
    public String getRunningCpu(int i, boolean bool) {
        Process process;
        BufferedReader reader;
        float freq;
        String frequency = context.getResources().getString(R.string.unknown);
        int f;
        try {
            process = Runtime.getRuntime().exec("cat /sys/devices/system/cpu/cpu" + i + "/cpufreq/scaling_cur_freq");
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            if ((line = reader.readLine()) != null) {
                if (bool) {
                    freq = Float.parseFloat(line) / 1000f;
                    frequency = String.format("%.2f", freq) + " MHz";
                } else {
                    f = Integer.parseInt(line) / 1000;
                    frequency = String.format("%d", f) + " MHz";
                }
            }
            process.destroy();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return frequency;
    }

    public int getCoresCount() {
        int processorCount = 0;
        try {
            Process process = Runtime.getRuntime().exec("cat /sys/devices/system/cpu/present");
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            if ((line = reader.readLine()) != null) {
                processorCount = Integer.parseInt(line.substring(line.indexOf("-") + 1)) + 1;
            }
            process.destroy();
            reader.close();
        } catch (IOException | InterruptedException | NumberFormatException e) {
            processorCount = Runtime.getRuntime().availableProcessors();
            e.printStackTrace();
        }
        return processorCount;
    }

    public void thermal() {
        String temp, type;
        for (int i = 0; i < 29; i++) {
            temp = thermalTemp(i);
            if (temp != null && !temp.contains("0.0")) {
                type = thermalType(i);
                if (type != null) {
                    thermalList.add(new ThermalModel(temp, type));
                }
            }
        }
    }

    public String thermalStringBuilder() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n").append(context.getResources().getString(R.string.thermal).toUpperCase()).append("\n").append(context.getResources().getString(R.string.dots)).append("\n");
        String temp, type;
        for (int i = 0; i < 29; i++) {
            temp = thermalTemp(i);
            if (temp != null && !temp.contains("0.0")) {
                type = thermalType(i);
                if (type != null) {
                    stringBuilder.append(type).append(" : ").append(temp).append("\n");
                }
            }
        }
        return stringBuilder.toString();
    }


    @SuppressLint("DefaultLocale")
    public String thermalTemp(int i) {
        Process process;
        BufferedReader reader;
        String line;
        String t = null;
        float temp = 0;
        try {
            process = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone" + i + "/temp");
            process.waitFor();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            line = reader.readLine();
            if (line != null) {
                temp = Float.parseFloat(line);
            }
            reader.close();
            process.destroy();
            if (!((int) temp == 0)) {
                if ((int) temp > 10000) {
                    temp = temp / 1000;
                } else if ((int) temp > 1000) {
                    temp = temp / 100;
                } else if ((int) temp > 100) {
                    temp = temp / 10;
                }
                if (unit) {
                    float f = ((temp / 5) * 9) + 32;
                    t = String.format("%.1f", f) + " " + (char) (176) + "F";
                } else {
                    t = String.format("%.1f", temp) + " " + (char) (176) + "C";
                }
            } else
                t = "0.0";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    public String thermalType(int i) {
        Process process;
        BufferedReader reader;
        String line, type = null;
        try {
            process = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone" + i + "/type");
            process.waitFor();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            line = reader.readLine();
            if (line != null) {
                type = line;
            }
            reader.close();
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }

    // use this for total storage of system,internal and external memory
    public long getTotalStorageInfo(String path) {
        long t = 10;
        try {
            StatFs statFs = new StatFs(path);
            t = statFs.getTotalBytes();
        } catch (Exception e) {
            System.out.println(e.getMessage() + " " + e.getCause());
        }
        return t;
    }

    // use this for used storage of system,internal and external memory
    public long getUsedStorageInfo(String path) {
        long u = 10;
        try {
            StatFs statFs = new StatFs(path);
            u = statFs.getTotalBytes() - statFs.getAvailableBytes();
        } catch (Exception e) {
            System.out.println(e.getMessage() + " " + e.getCause());
        }
        return u;
    }

    public Map<String, String> getZRamInfo() {
        Map<String, String> map = new HashMap<>();
        String[] str;
        File file = new File("proc/meminfo");
        try {
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                str = line.split(":");
                map.put(str[0].trim().toLowerCase(), str[1].trim().replace("kB", "").toLowerCase());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public String getBatteryLevel() {
        int pct = 0;
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batterystat = context.registerReceiver(null, intentFilter);
        if (batterystat != null) {
            int level = batterystat.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batterystat.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            pct = level * 100 / scale;
        }
        return pct + "%";
    }

    public String getCurrentLevel() {
        int current = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            current = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
        }
        return -(current / 1000) + " mA";
    }

    public String getBatteryStatus() {
        String batterystatus = null;
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batterystat = context.registerReceiver(null, intentFilter);
        int status = 0;
        if (batterystat != null) {
            status = batterystat.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        }
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                batterystatus = context.getResources().getString(R.string.charging);
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                batterystatus = context.getResources().getString(R.string.discharge);
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                batterystatus = context.getResources().getString(R.string.notcharge);
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                batterystatus = context.getResources().getString(R.string.full);
                break;
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                batterystatus = context.getResources().getString(R.string.unknown);
                break;
        }
        return batterystatus;
    }

    public String getBatteryHealth() {
        String health = null;
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batterystat = context.registerReceiver(null, intentFilter);
        int status = 0;
        if (batterystat != null) {
            status = batterystat.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        }
        switch (status) {
            case BatteryManager.BATTERY_HEALTH_COLD:
                health = context.getResources().getString(R.string.cold);
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                health = context.getResources().getString(R.string.dead);
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                health = context.getResources().getString(R.string.good);
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                health = context.getResources().getString(R.string.overvoltage);
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                health = context.getResources().getString(R.string.overheat);
                break;
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                health = context.getResources().getString(R.string.unknown);
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                health = context.getResources().getString(R.string.unspec_fail);
                break;
        }
        return health;
    }

    public String getPowerSource() {
        String source;
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batterystat = context.registerReceiver(null, intentFilter);
        int status = 0;
        if (batterystat != null) {
            status = batterystat.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        }
        switch (status) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                source = context.getResources().getString(R.string.ac);
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                source = context.getResources().getString(R.string.usbport);
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                source = context.getResources().getString(R.string.wireless);
                break;
            default:
                source = context.getResources().getString(R.string.battery);
                break;
        }
        return source;
    }

    public String getBatteryTechnology() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batterystat = context.registerReceiver(null, intentFilter);
        String tech = null;
        if (batterystat != null) {
            tech = batterystat.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
        }
        return tech;
    }

    @SuppressLint("PrivateApi")
    public String getBatteryCapacity() {
        Object power;
        double capacity = 0;
        try {
            power = Class.forName("com.android.internal.os.PowerProfile").getConstructor(Context.class).newInstance(context);
            capacity = (double) Class.forName("com.android.internal.os.PowerProfile").getMethod("getBatteryCapacity").invoke(power);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | NullPointerException e) {
            e.printStackTrace();
        }
        return (int) capacity + " mAh";
    }

    @SuppressLint("DefaultLocale")
    public String getBatteryTemp() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batterystat = context.registerReceiver(null, intentFilter);
        float temp = 0;
        if (batterystat != null) {
            temp = (float) batterystat.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
        }
        if (unit) {
            float f = ((temp / 50) * 9) + 32;
            return String.format("%.1f", f) + " " + (char) (176) + "F";
        }
        return String.format("%.1f", temp / 10) + " " + (char) (176) + "C";
    }

    public String getVoltage() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batterystat = context.registerReceiver(null, intentFilter);
        int volt = 0;
        if (batterystat != null) {
            volt = batterystat.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
        }
        return volt + " mV";
    }

    @SuppressLint("DefaultLocale")
    public String getScreenSize() {
        Point point = new Point();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        try {
            Display.class.getMethod("getRealSize", Point.class).invoke(((Activity) context).getWindowManager().getDefaultDisplay(), point);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        ((Activity) context).getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        double x = Math.pow(point.x / displayMetrics.xdpi, 2);
        double y = Math.pow(point.y / displayMetrics.ydpi, 2);
        double inches = (float) Math.round(Math.sqrt(x + y) * 10) / 10;
        return String.format("%.2f", inches);
    }

    public String getBrightnessMode() {
        String mode = null;
        try {
            switch (Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE)) {
                case 0:
                    mode = "Manual";
                    break;
                case 1:
                    mode = "Automatic";
                    break;
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return mode;
    }

    public String getOrientation() {
        String orien = "";
        switch (context.getResources().getConfiguration().orientation) {
            case 0:
                orien = context.getResources().getString(R.string.undefined);
                break;
            case 1:
                orien = context.getResources().getString(R.string.portrait);
                break;
            case 2:
                orien = context.getResources().getString(R.string.landscape);
                break;
            case 3:
                orien = context.getResources().getString(R.string.square);
                break;
        }
        return orien;
    }

    public String getDensityDpi() {
        float density;
        String dpi;
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        density = metrics.density;
        if (density >= 0.75f && density < 1.0f) {
            dpi = " (LDPI)";
        } else if (density >= 1.0f && density < 1.5f) {
            dpi = " (MDPI)";
        } else if (density >= 1.5f && density <= 2.0f) {
            dpi = " (HDPI)";
        } else if (density > 2.0f && density <= 3.0f) {
            dpi = " (XHDPI)";
        } else if (density >= 3.0f && density < 4.0f) {
            dpi = " (XXHDPI)";
        } else {
            dpi = " (XXXHDPI)";
        }
        return dpi;
    }

    public int getSensorsCount() {
        List<Sensor> sensors;
        SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensors = sm.getSensorList(Sensor.TYPE_ALL);
        return sensors.size();
    }

    public String getRootInfo() {
        if (checkRootFiles() || checkTags()) {
            return "Yes";
        }
        return "No";
    }

    public boolean checkRootFiles() {
        boolean root = false;
        String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths) {
            root = new File(path).exists();
            if (root)
                break;
        }
        return root;
    }

    public boolean checkTags() {
        String tag = Build.TAGS;
        return tag != null && tag.trim().contains("test-keys");
    }

    @SuppressLint("PrivateApi")
    public String getTrebleInfo() {
        Class<?> s;
        String treble = null;
        try {
            s = Class.forName("android.os.SystemProperties");
            Method m = s.getMethod("get", String.class);
            treble = (String) m.invoke(null, "ro.treble.enabled");
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (treble != null) {
                if (treble.equalsIgnoreCase("true")) {
                    return context.getResources().getString(R.string.supported);
                } else {
                    return context.getResources().getString(R.string.not_supported);
                }
            } else
                return context.getResources().getString(R.string.not_supported);
        } else {
            return context.getResources().getString(R.string.not_supported);
        }
    }

    @SuppressLint("PrivateApi")
    public String getSeamlessUpdatesInfo() {
        Class<?> s;
        String updates = null;
        try {
            s = Class.forName("android.os.SystemProperties");
            Method m = s.getMethod("get", String.class);
            if (m.invoke(null, "ro.virtual_ab.enabled") == "true" && m.invoke(null, "ro.virtual_ab.retrofit") == "false") {
                updates = "true";
            }
            if (m.invoke(null, "ro.build.ab_update") == "true") {
                updates = "true";
            } else {
                updates = "false";
            }
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (updates != null) {
                if (updates.equalsIgnoreCase("true")) {
                    return context.getResources().getString(R.string.supported);
                } else {
                    return context.getResources().getString(R.string.not_supported);
                }
            } else
                return context.getResources().getString(R.string.not_supported);
        } else {
            return context.getResources().getString(R.string.not_supported);
        }
    }

    @SuppressLint("PrivateApi")
    public String getSeLinuxInfo() {
        Class<?> s;
        String seLinux = "";
        try {
            s = Class.forName("android.os.SystemProperties");
            Method m = s.getMethod("get", String.class);
            seLinux = (String) m.invoke(null, "ro.build.selinux");
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        if (seLinux != null && seLinux.isEmpty())
            seLinux = context.getResources().getString(R.string.unable);
        return seLinux;
    }

    @SuppressLint("DefaultLocale")
    public String getUpTime() {
        long hour, min, sec, time;
        String uptime;
        time = SystemClock.elapsedRealtime();
        sec = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
        min = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
        hour = TimeUnit.MILLISECONDS.toHours(time);
        uptime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, min, sec);
        return uptime;
    }

    public String getCodeName(int level) {
        String[] api_level = new String[]{"Android 4.1 Jelly Bean", "Android 4.2 Jelly Bean", "Android 4.3 Jelly Bean", "Android 4.4 KitKat", "Android 4.4W KitKat",
                "Android 5.0 Lollipop", "Android 5.1 Lollipop", "Android 6.0 Marshmallow", "Android 7.0 Nougat", "Android 7.1 Nougat", "Android 8.0 Oreo",
                "Android 8.1.0 Oreo", "Android 9 Pie", "Android 10", "Android 11"};
        return (String) Array.get(api_level, level - 16);
    }

    public String getReleaseDate(int level) {
        String[] date = new String[]{"July 9, 2012", "November 13, 2012", "July 24, 2013", "October 31, 2013", "June 25, 2014", "November 12, 2014", "March 9, 2015",
                "October 5, 2015", "August 22, 2016", "October 4, 2016", "August 21, 2017", "December 5, 2017", "August 6, 2018",
                "September 3, 2019", "September 8, 2020"};
        return (String) Array.get(date, level - 16);
    }

    public String advertId() {
        String adId = context.getResources().getString(R.string.unable);
        AdvertisingIdClient.Info info;
        try {
            info = AdvertisingIdClient.getAdvertisingIdInfo(context);
            adId = info.getId();
        } catch (IOException | GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }
        return adId;
    }

    public String getMacAddress() {
        List<NetworkInterface> networkInterfaces;
        try {
            networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni : networkInterfaces) {
                if (ni.getName().equalsIgnoreCase("wlan0")) {
                    byte[] b = ni.getHardwareAddress();
                    StringBuilder builder = new StringBuilder();
                    for (byte add : b) {
                        builder.append(String.format("%02X:", add));
                    }
                    return builder.deleteCharAt(builder.length() - 1).toString();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00";
    }

    public String getPlayServices() {
        String version;
        String playServices = "";
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
            try {
                version = String.valueOf(PackageInfoCompat.getLongVersionCode(context.getPackageManager().getPackageInfo(GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE, 0)));
                playServices = version.substring(0, 2).concat(".").concat(version.substring(2, 4)).concat(".").concat(version.substring(4, 6));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            playServices = "Play Services Not Available";
        }
        return playServices;
    }

    public String afCamera(int facing) {
        String[] af;
        StringBuilder afm = new StringBuilder();
        String afModes = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CameraCharacteristics characteristics = null;
            try {
                characteristics = cameraManager.getCameraCharacteristics(String.valueOf(facing));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (characteristics != null) {
                af = Arrays.toString(characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES)).replace("[", "").replace("]", "").split(",");
                for (String mode : af) {
                    if (mode.trim().contains("0"))
                        afm.append("Off, ");
                    if (mode.trim().contains("1"))
                        afm.append("Auto, ");
                    if (mode.trim().contains("2"))
                        afm.append("Macro, ");
                    if (mode.trim().contains("3"))
                        afm.append("Continuous Video, ");
                    if (mode.trim().contains("4"))
                        afm.append("Continuous Picture, ");
                    if (mode.trim().contains("5"))
                        afm.append("EDof, ");
                }
            }
            if (afm.toString().length() > 0)
                afModes = afm.toString().substring(0, afm.toString().length() - 2);
            else
                afModes = "Not Available";
        }
        return afModes;
    }

    public String abCamera(int facing) {
        String[] ab;
        StringBuilder abm = new StringBuilder();
        String abModes = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CameraCharacteristics characteristics = null;
            try {
                characteristics = cameraManager.getCameraCharacteristics(String.valueOf(facing));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (characteristics != null) {
                ab = Arrays.toString(characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_ANTIBANDING_MODES)).replace("[", "").replace("]", "").split(",");
                for (String mode : ab) {
                    if (mode.trim().contains("0"))
                        abm.append("Off, ");
                    if (mode.trim().contains("1"))
                        abm.append("50Hz, ");
                    if (mode.trim().contains("2"))
                        abm.append("60Hz, ");
                    if (mode.trim().contains("3"))
                        abm.append("Auto, ");
                }
            }
            if (abm.toString().length() > 0)
                abModes = abm.toString().substring(0, abm.toString().length() - 2);
            else
                abModes = "Not Available";
        }
        return abModes;
    }

    public String effCamera(int facing) {
        String[] eff;
        StringBuilder effm = new StringBuilder();
        String effects = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CameraCharacteristics characteristics = null;
            try {
                characteristics = cameraManager.getCameraCharacteristics(String.valueOf(facing));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (characteristics != null) {
                eff = Arrays.toString(characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS)).replace("[", "").replace("]", "").split(",");
                for (String mode : eff) {
                    if (mode.trim().contains("0"))
                        effm.append("Off, ");
                    if (mode.trim().contains("1"))
                        effm.append("Mono, ");
                    if (mode.trim().contains("2"))
                        effm.append("Negative, ");
                    if (mode.trim().contains("3"))
                        effm.append("Solarize, ");
                    if (mode.trim().contains("4"))
                        effm.append("Sepia, ");
                    if (mode.trim().contains("5"))
                        effm.append("Posterize, ");
                    if (mode.trim().contains("6"))
                        effm.append("Whiteboard, ");
                    if (mode.trim().contains("7"))
                        effm.append("Blackboard, ");
                    if (mode.trim().contains("8"))
                        effm.append("Aqua, ");
                }
            }
            if (effm.toString().length() > 0)
                effects = effm.toString().substring(0, effm.toString().length() - 2);
            else
                effects = "Not Available";
        }
        return effects;
    }

    public String sceneCamera(int facing) {
        String[] scenes;
        String sceneModes = null;
        StringBuilder scenem = new StringBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CameraCharacteristics characteristics = null;
            try {
                characteristics = cameraManager.getCameraCharacteristics(String.valueOf(facing));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (characteristics != null) {
                scenes = Arrays.toString(characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES)).replace("[", "").replace("]", "").split(",");
                for (String mode : scenes) {
                    if (mode.trim().contains("0"))
                        scenem.append("Disabled, ");
                    if (mode.trim().contains("1"))
                        scenem.append("Face Priority, ");
                    if (mode.trim().contains("2"))
                        scenem.append("Action, ");
                    if (mode.trim().contains("3"))
                        scenem.append("Portrait, ");
                    if (mode.trim().contains("4"))
                        scenem.append("Landscape, ");
                    if (mode.trim().contains("5"))
                        scenem.append("Night, ");
                    if (mode.trim().contains("6"))
                        scenem.append("Night Portrait, ");
                    if (mode.trim().contains("7"))
                        scenem.append("Theatre, ");
                    if (mode.trim().contains("8"))
                        scenem.append("Beach, ");
                    if (mode.trim().contains("9"))
                        scenem.append("Snow, ");
                    if (mode.trim().contains("10"))
                        scenem.append("Sunset, ");
                    if (mode.trim().contains("11"))
                        scenem.append("Steady Photo, ");
                    if (mode.trim().contains("12"))
                        scenem.append("FireWorks, ");
                    if (mode.trim().contains("13"))
                        scenem.append("Sports, ");
                    if (mode.trim().contains("14"))
                        scenem.append("Party, ");
                    if (mode.trim().contains("15"))
                        scenem.append("CandleLight, ");
                    if (mode.trim().contains("16"))
                        scenem.append("Barcode, ");
                }
            }
            if (scenem.toString().length() > 0)
                sceneModes = scenem.toString().substring(0, scenem.toString().length() - 2);
            else
                sceneModes = "Not Available";
        }
        return sceneModes;
    }

    public String awbCamera(int facing) {
        String[] awb;
        StringBuilder awbm = new StringBuilder();
        String awbModes = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CameraCharacteristics characteristics = null;
            try {
                characteristics = cameraManager.getCameraCharacteristics(String.valueOf(facing));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (characteristics != null) {
                awb = Arrays.toString(characteristics.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES)).replace("[", "").replace("]", "").split(",");
                for (String mode : awb) {
                    if (mode.trim().contains("0"))
                        awbm.append("Off, ");
                    if (mode.trim().contains("1"))
                        awbm.append("Auto, ");
                    if (mode.trim().contains("2"))
                        awbm.append("Incandescent, ");
                    if (mode.trim().contains("3"))
                        awbm.append("Fluorescent, ");
                    if (mode.trim().contains("4"))
                        awbm.append("Warm Fluorescent, ");
                    if (mode.trim().contains("5"))
                        awbm.append("Daylight, ");
                    if (mode.trim().contains("6"))
                        awbm.append("Cloudy Daylight, ");
                    if (mode.trim().contains("7"))
                        awbm.append("Twilight, ");
                    if (mode.trim().contains("8"))
                        awbm.append("Shade, ");
                }
            }
            if (awbm.toString().length() > 0)
                awbModes = awbm.toString().substring(0, awbm.toString().length() - 2);
            else
                awbModes = "Not Available";
        }
        return awbModes;
    }

    public String hotPixelCamera(int facing) {
        String[] hp;
        StringBuilder hpm = new StringBuilder();
        String hotPixelModes = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CameraCharacteristics characteristics = null;
            try {
                characteristics = cameraManager.getCameraCharacteristics(String.valueOf(facing));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (characteristics != null) {
                hp = Arrays.toString(characteristics.get(CameraCharacteristics.HOT_PIXEL_AVAILABLE_HOT_PIXEL_MODES)).replace("[", "").replace("]", "").split(",");
                for (String mode : hp) {
                    if (mode.trim().contains("0"))
                        hpm.append("Off, ");
                    if (mode.trim().contains("1"))
                        hpm.append("Fast, ");
                    if (mode.trim().contains("2"))
                        hpm.append("High Quality, ");
                }
            }
            if (hpm.toString().length() > 0)
                hotPixelModes = hpm.toString().substring(0, hpm.toString().length() - 2);
            else
                hotPixelModes = "Not Available";
        }
        return hotPixelModes;
    }

    public String edgeModesCamera(int facing) {
        String[] ed;
        StringBuilder edgem = new StringBuilder();
        String edgeModes = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CameraCharacteristics characteristics = null;
            try {
                characteristics = cameraManager.getCameraCharacteristics(String.valueOf(facing));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (characteristics != null) {
                ed = Arrays.toString(characteristics.get(CameraCharacteristics.EDGE_AVAILABLE_EDGE_MODES)).replace("[", "").replace("]", "").split(",");
                for (String mode : ed) {
                    if (mode.trim().contains("0"))
                        edgem.append("Off, ");
                    if (mode.trim().contains("1"))
                        edgem.append("Fast, ");
                    if (mode.trim().contains("2"))
                        edgem.append("High Quality, ");
                    if (mode.trim().contains("3"))
                        edgem.append("Zero Shutter Lag, ");
                }
            }
            if (edgem.toString().length() > 0)
                edgeModes = edgem.toString().substring(0, edgem.toString().length() - 2);
            else
                edgeModes = "Not Available";
        }
        return edgeModes;
    }

    public String videoModesCamera(int facing) {
        String[] vs;
        StringBuilder vsm = new StringBuilder();
        String videoModes = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CameraCharacteristics characteristics = null;
            try {
                characteristics = cameraManager.getCameraCharacteristics(String.valueOf(facing));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (characteristics != null) {
                vs = Arrays.toString(characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES)).replace("[", "").replace("]", "").split(",");
                for (String mode : vs) {
                    if (mode.trim().contains("0"))
                        vsm.append("Off, ");
                    if (mode.trim().contains("1"))
                        vsm.append("On, ");
                }
            }
            if (vsm.toString().length() > 0)
                videoModes = vsm.toString().substring(0, vsm.toString().length() - 2);
            else
                videoModes = "Not Available";
        }
        return videoModes;
    }

    public String camCapCamera(int facing) {
        String[] camcap;
        StringBuilder camcapm = new StringBuilder();
        String capabilities = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CameraCharacteristics characteristics = null;
            try {
                characteristics = cameraManager.getCameraCharacteristics(String.valueOf(facing));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (characteristics != null) {
                camcap = Arrays.toString(characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)).replace("[", "").replace("]", "").split(",");
                for (String mode : camcap) {
                    if (mode.trim().contains("0"))
                        camcapm.append("Backward Compatible, ");
                    if (mode.trim().contains("1"))
                        camcapm.append("Manual Sensor, ");
                    if (mode.trim().contains("2"))
                        camcapm.append("Manual Post Processing, ");
                    if (mode.trim().contains("3"))
                        camcapm.append("RAW, ");
                    if (mode.trim().contains("4"))
                        camcapm.append("Private Reprocessing, ");
                    if (mode.trim().contains("5"))
                        camcapm.append("Read Sensor Settings, ");
                    if (mode.trim().contains("6"))
                        camcapm.append("Burst Capture, ");
                    if (mode.trim().contains("7"))
                        camcapm.append("YUV Reprocessing, ");
                    if (mode.trim().contains("8"))
                        camcapm.append("Depth Output, ");
                    if (mode.trim().contains("9"))
                        camcapm.append("High Speed Video, ");
                    if (mode.trim().contains("10"))
                        camcapm.append("Motion Tracking, ");
                    if (mode.trim().contains("11"))
                        camcapm.append("Logical Multi Camera, ");
                    if (mode.trim().contains("12"))
                        camcapm.append("Monochrome, ");
                    if (mode.trim().contains("13"))
                        camcapm.append("Secure Image Data, ");
                }
            }
            if (camcapm.toString().length() > 0)
                capabilities = camcapm.toString().substring(0, camcapm.toString().length() - 2);
            else
                capabilities = "Not Available";
        }
        return capabilities;
    }

    public String testModesCamera(int facing) {
        String[] tp;
        StringBuilder tpm = new StringBuilder();
        String testPattern = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CameraCharacteristics characteristics = null;
            try {
                characteristics = cameraManager.getCameraCharacteristics(String.valueOf(facing));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (characteristics != null) {
                tp = Arrays.toString(characteristics.get(CameraCharacteristics.SENSOR_AVAILABLE_TEST_PATTERN_MODES)).replace("[", "").replace("]", "").split(",");
                for (String mode : tp) {
                    if (mode.trim().contains("0"))
                        tpm.append("Off, ");
                    if (mode.trim().contains("1"))
                        tpm.append("Solid Color, ");
                    if (mode.trim().contains("2"))
                        tpm.append("Color Bars, ");
                    if (mode.trim().contains("3"))
                        tpm.append("Color Bars Fade to Gray, ");
                    if (mode.trim().contains("4"))
                        tpm.append("PN9, ");
                    if (mode.trim().contains("256"))
                        tpm.append("Custom1, ");
                }
            }
            if (tpm.toString().length() > 0)
                testPattern = tpm.toString().substring(0, tpm.toString().length() - 2);
            else
                testPattern = "Not Available";
        }
        return testPattern;
    }

    public String aeCamera(int facing) {
        String[] ae;
        StringBuilder aem = new StringBuilder();
        String aeModes = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CameraCharacteristics characteristics = null;
            try {
                characteristics = cameraManager.getCameraCharacteristics(String.valueOf(facing));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (characteristics != null) {
                ae = Arrays.toString(characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES)).replace("[", "").replace("]", "").split(",");
                for (String mode : ae) {
                    if (mode.trim().contains("0"))
                        aem.append("Off, ");
                    if (mode.trim().contains("1"))
                        aem.append("On, ");
                    if (mode.trim().contains("2"))
                        aem.append("Auto Flash, ");
                    if (mode.trim().contains("3"))
                        aem.append("Always Flash, ");
                    if (mode.trim().contains("4"))
                        aem.append("Auto Flash Red-Eye, ");
                    if (mode.trim().contains("5"))
                        aem.append("External Flash, ");
                }
            }
            if (aem.toString().length() > 0)
                aeModes = aem.toString().substring(0, aem.toString().length() - 2);
            else
                aeModes = "Not Available";
        }
        return aeModes;
    }

    public String fdCamera(int facing) {
        String[] fd;
        StringBuilder fdm = new StringBuilder();
        String faceDetectModes = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CameraCharacteristics characteristics = null;
            try {
                characteristics = cameraManager.getCameraCharacteristics(String.valueOf(facing));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (characteristics != null) {
                fd = Arrays.toString(characteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES)).replace("[", "").replace("]", "").split(",");
                for (String mode : fd) {
                    if (mode.trim().contains("0"))
                        fdm.append("Off, ");
                    if (mode.trim().contains("1"))
                        fdm.append("Simple, ");
                    if (mode.trim().contains("2"))
                        fdm.append("Full, ");
                }
            }
            if (fdm.toString().length() > 0)
                faceDetectModes = fdm.toString().substring(0, fdm.toString().length() - 2);
            else
                faceDetectModes = "Not Available";
        }
        return faceDetectModes;
    }

    public String amCamera(int facing) {
        String[] aberrationModes;
        StringBuilder am = new StringBuilder();
        String aberrationMode = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CameraCharacteristics characteristics = null;
            try {
                characteristics = cameraManager.getCameraCharacteristics(String.valueOf(facing));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (characteristics != null) {
                aberrationModes = Arrays.toString(characteristics.get(CameraCharacteristics.COLOR_CORRECTION_AVAILABLE_ABERRATION_MODES)).replace("[", "").replace("]", "").split(",");
                for (String mode : aberrationModes) {
                    if (mode.trim().contains("0"))
                        am.append("Off, ");
                    if (mode.trim().contains("1"))
                        am.append("Fast, ");
                    if (mode.trim().contains("2"))
                        am.append("High Quality, ");
                }
            }
            if (am.toString().length() > 0)
                aberrationMode = am.toString().substring(0, am.toString().length() - 2);
            else
                aberrationMode = "Not Available";
        }
        return aberrationMode;
    }

    public String osCamera(int facing) {
        String[] os;
        StringBuilder osm = new StringBuilder();
        String opticalStable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CameraCharacteristics characteristics = null;
            try {
                characteristics = cameraManager.getCameraCharacteristics(String.valueOf(facing));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (characteristics != null) {
                os = Arrays.toString(characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION)).replace("[", "").replace("]", "").split(",");
                for (String mode : os) {
                    if (mode.trim().contains("0"))
                        osm.append("Off, ");
                    if (mode.trim().contains("1"))
                        osm.append("On, ");
                }
            }
            if (osm.toString().length() > 0)
                opticalStable = osm.toString().substring(0, osm.toString().length() - 2);
            else
                opticalStable = "Not Available";
        }
        return opticalStable;
    }

    @SuppressLint("DefaultLocale")
    public String resolutionsCamera(int facing) {
        StreamConfigurationMap streamConfigurationMap;
        Size[] sizes;
        StringBuilder srm = new StringBuilder();
        String resolutions = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CameraCharacteristics characteristics;
            try {
                characteristics = cameraManager.getCameraCharacteristics(String.valueOf(facing));
                streamConfigurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                sizes = streamConfigurationMap.getOutputSizes(ImageFormat.RAW_SENSOR);
                if (sizes != null)
                    for (Size size : sizes)
                        srm.append(String.format("%.2f", size.getWidth() * size.getHeight() / 1000000.0)).append(" MP - ").append(size.getWidth()).append(" x ").append(size.getHeight()).append("\n");
                sizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
                if (sizes != null)
                    for (Size size : sizes)
                        srm.append(String.format("%.2f", size.getWidth() * size.getHeight() / 1000000.0)).append(" MP - ").append(size.getWidth()).append(" x ").append(size.getHeight()).append("\n");
                resolutions = srm.toString().substring(0, srm.toString().length() - 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resolutions;
    }

    public String getIp4Address() {
        String a = context.getResources().getString(R.string.connect_to_net);
        try {
            List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intF : networkInterfaces) {
                List<InterfaceAddress> interfaceAddresses = intF.getInterfaceAddresses();
                for (InterfaceAddress interfaceAddress : interfaceAddresses) {
                    if (!interfaceAddress.getAddress().isLoopbackAddress() && interfaceAddress.getAddress() instanceof Inet4Address) {
                        if (interfaceAddress.getNetworkPrefixLength() % 8 == 0) {
                            a = interfaceAddress.getAddress().getHostAddress();
                        } else if (a.equalsIgnoreCase(context.getResources().getString(R.string.connect_to_net))) {
                            a = interfaceAddress.getAddress().getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return a;
    }

    public String getIp6Address() {
        String a = context.getResources().getString(R.string.connect_to_net);
        try {
            List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intF : networkInterfaces) {
                List<InterfaceAddress> interfaceAddresses = intF.getInterfaceAddresses();
                for (InterfaceAddress interfaceAddress : interfaceAddresses) {
                    if (!interfaceAddress.getAddress().isLoopbackAddress() && interfaceAddress.getAddress() instanceof Inet6Address)
                        a = interfaceAddress.getAddress().getHostAddress();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (a.contains("%")) {
            int index = a.indexOf("%");
            a = a.substring(0, index);
        }
        return a;
    }

    public String subnet() {
        int x = 0;
        try {
            List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intF : networkInterfaces) {
                List<InterfaceAddress> interfaceAddresses = intF.getInterfaceAddresses();
                for (InterfaceAddress interfaceAddress : interfaceAddresses) {
                    if (!interfaceAddress.getAddress().isLoopbackAddress() && interfaceAddress.getAddress() instanceof Inet4Address) {
                        if (interfaceAddress.getNetworkPrefixLength() % 8 == 0) {
                            x = interfaceAddress.getNetworkPrefixLength();
                            break;
                        } else {
                            x = interfaceAddress.getNetworkPrefixLength();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        int v, bitValue;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            bitValue = 0;
            if (x > 0) {
                if (x >= 8)
                    v = 0;
                else
                    v = x % 8;
                if (v == 0)
                    stringBuilder.append("255.");
                else {
                    int j = 7;
                    while (v > 0) {
                        bitValue += Math.pow(2, j);
                        j--;
                        v--;
                    }
                    stringBuilder.append(bitValue).append(".");
                }
                x -= 8;
            } else
                stringBuilder.append("0.");
        }
        return (stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1));
    }

    public String getNetworkG(int type) {
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G LTE";
            case TelephonyManager.NETWORK_TYPE_NR:
                return "5G";
            default:
                return "Unknown";
        }
    }

    public String getAxis(int num) {
        String axis = null;
        switch (num) {
            case 0:
                axis = "AXIS_X";
                break;
            case 1:
                axis = "AXIS_Y";
                break;
            case 2:
                axis = "AXIS_PRESSURE";
                break;
            case 3:
                axis = "AXIS_SIZE";
                break;
            case 4:
                axis = "AXIS_TOUCH_MAJOR";
                break;
            case 5:
                axis = "AXIS_TOUCH_MINOR";
                break;
            case 6:
                axis = "AXIS_TOOL_MAJOR";
                break;
            case 7:
                axis = "AXIS_TOOL_MINOR";
                break;
            case 8:
                axis = "AXIS_ORIENTATION";
                break;
            case 9:
                axis = "AXIS_VSCROLL";
                break;
            case 10:
                axis = "AXIS_HSCROLL";
                break;
            case 11:
                axis = "AXIS_Z";
                break;
            case 12:
                axis = "AXIS_RX";
                break;
            case 13:
                axis = "AXIS_RY";
                break;
            case 14:
                axis = "AXIS_RZ";
                break;
            case 15:
                axis = "AXIS_HAT_X";
                break;
            case 16:
                axis = "AXIS_HAT_Y";
                break;
            case 17:
                axis = "AXIS_LTRIGGER";
                break;
            case 18:
                axis = "AXIS_RTRIGGER";
                break;
            case 19:
                axis = "AXIS_THROTTLE";
                break;
            case 20:
                axis = "AXIS_RUDDER";
                break;
            case 21:
                axis = "AXIS_WHEEL";
                break;
            case 22:
                axis = "AXIS_GAS";
                break;
            case 23:
                axis = "AXIS_BRAKE";
                break;
            case 24:
                axis = "AXIS_DISTANCE";
                break;
            case 25:
                axis = "AXIS_TILT";
                break;
            case 26:
                axis = "AXIS_SCROLL";
                break;
            case 27:
                axis = "AXIS_RELATIVE_X";
                break;
            case 28:
                axis = "AXIS_RELATIVE_Y";
                break;
            case 32:
                axis = "AXIS_GENERIC_1";
                break;
            case 33:
                axis = "AXIS_GENERIC_2";
                break;
            case 34:
                axis = "AXIS_GENERIC_3";
                break;
            case 35:
                axis = "AXIS_GENERIC_4";
                break;
            case 36:
                axis = "AXIS_GENERIC_5";
                break;
            case 37:
                axis = "AXIS_GENERIC_6";
                break;
            case 38:
                axis = "AXIS_GENERIC_7";
                break;
            case 39:
                axis = "AXIS_GENERIC_8";
                break;
            case 40:
                axis = "AXIS_GENERIC_9";
                break;
            case 41:
                axis = "AXIS_GENERIC_10";
                break;
            case 42:
                axis = "AXIS_GENERIC_11";
                break;
            case 43:
                axis = "AXIS_GENERIC_12";
                break;
            case 44:
                axis = "AXIS_GENERIC_13";
                break;
            case 45:
                axis = "AXIS_GENERIC_14";
                break;
            case 46:
                axis = "AXIS_GENERIC_15";
                break;
            case 47:
                axis = "AXIS_GENERIC_16";
                break;
        }
        return axis;
    }

    public String getWifiChannel(int frequency) {
        int i;
        switch (frequency) {
            case 2412:
                i = 1;
                break;
            case 2417:
                i = 2;
                break;
            case 2422:
                i = 3;
                break;
            case 2427:
                i = 4;
                break;
            case 2432:
                i = 5;
                break;
            case 2437:
                i = 6;
                break;
            case 2442:
                i = 7;
                break;
            case 2447:
                i = 8;
                break;
            case 2452:
                i = 9;
                break;
            case 2457:
                i = 10;
                break;
            case 2462:
                i = 11;
                break;
            case 2467:
                i = 12;
                break;
            case 2472:
                i = 13;
                break;
            case 2484:
                i = 14;
                break;
            default:
                return context.getResources().getString(R.string.unable);
        }
        return String.valueOf(i);
    }

    public ArrayList<ThermalModel> getThermalList() {
        return thermalList;
    }

    @SuppressLint("PrivateApi")
    public String getSimState() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String state = "Unknown";
        switch (telephonyManager.getSimState()) {
            case 0:
                state = "Unknown";
                break;
            case 1:
                state = "Absent";
                break;
            case 2:
                state = "PIN Required";
                break;
            case 3:
                state = "PUK Required";
                break;
            case 4:
                state = "Locked";
                break;
            case 5:
                state = "Ready";
                break;
            case 6:
                state = "Not Ready";
                break;
            case 7:
                state = "Permanently Disabled";
                break;
            case 8:
                state = "Card IO Error";
                break;
            case 9:
                state = "Card Restricted";
                break;
        }
        return state;
    }
}
