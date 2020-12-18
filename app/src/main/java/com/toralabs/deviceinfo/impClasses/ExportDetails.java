package com.toralabs.deviceinfo.impClasses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaDrm;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.InputDevice;

import androidx.core.content.ContextCompat;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.menuItems.Preferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import static java.lang.System.getProperty;

public class ExportDetails {
    Preferences preferences;
    private final Context context;
    private final StringBuilder builder = new StringBuilder();
    BuildInfo buildInfo;
    CameraCharacteristics characteristics;
    CameraManager cameraManager;
    String[] cameraIds;
    JsonData jsonData;

    public ExportDetails(Context context) {
        this.context = context;
        buildInfo = new BuildInfo(context);
        preferences = new Preferences(context);
        jsonData = new JsonData(context);
    }

    @SuppressLint("HardwareIds")
    public void device() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        dateFormat.format(date);
        builder.append("Device Info v" + com.toralabs.deviceinfo.BuildConfig.VERSION_NAME + "\n©ToraLabs\n" + "Created on ").append(date).append("\n\n").append(context.getResources().getString(R.string.device).toUpperCase()).append("\n").append(context.getResources().getString(R.string.dots)).append("\n");
        String model = context.getResources().getString(R.string.model) + " : ";
        String manufacturer = context.getResources().getString(R.string.manu) + " : ";
        String brand = context.getResources().getString(R.string.brand) + " : ";
        String device = context.getResources().getString(R.string.device_lav) + " : ";
        String board = context.getResources().getString(R.string.board) + " : ";
        String hardware = context.getResources().getString(R.string.hardware) + " : ";
        String device_id = context.getResources().getString(R.string.device_id) + " : ";
        String deviceType = context.getResources().getString(R.string.devicetype) + " : ";
        String buildprint = context.getResources().getString(R.string.build_print) + " : ";
        String usbhost = context.getResources().getString(R.string.usb_host) + " : ";
        String macadd = context.getResources().getString(R.string.mac_address) + " : ";
        String usbSupport = context.getResources().getString(R.string.supported);
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_USB_HOST)) {
            usbSupport = context.getResources().getString(R.string.not_supported);
        }
        String advertId = context.getResources().getString(R.string.advertid) + " : ";
        String timezone = context.getResources().getString(R.string.timezone) + " : ";
        SimpleDateFormat d = new SimpleDateFormat("z", Locale.getDefault());
        String time = d.format(System.currentTimeMillis());
        String t = TimeZone.getDefault().getDisplayName() + " (" + time + ")";
        builder.append(model).append(Build.MODEL).append("\n").append(manufacturer).append(Build.MANUFACTURER).append("\n").append(brand).append(Build.BRAND).append("\n").append(device).append(Build.DEVICE).append("\n").append(board).append(Build.BOARD).append("\n").append(hardware).append(Build.HARDWARE).append("\n").append(device_id).append(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)).append("\n").append(deviceType).append(buildInfo.getPhoneType()).append("\n").append(buildInfo.getNetworkName()).append(macadd).append(buildInfo.getMacAddress()).append("\n").append(buildprint).append(Build.FINGERPRINT).append("\n").append(usbhost).append(usbSupport).append("\n").append(advertId).append(buildInfo.advertId()).append("\n").append(timezone).append(t).append("\n");
    }

    public void thermal() {
        builder.append(buildInfo.thermalStringBuilder());
    }

    public void deviceFeatures() {
        builder.append("\n").append(context.getResources().getString(R.string.devicefeat).toUpperCase()).append("\n").append(context.getResources().getString(R.string.dots)).append("\n");
        FeatureInfo[] featureInfo = context.getPackageManager().getSystemAvailableFeatures();
        for (FeatureInfo feature : featureInfo) {
            if (feature.name != null)
                builder.append(feature.name).append("\n");
        }
    }

    public void glExtensions() {
        builder.append("\n").append(context.getResources().getString(R.string.gpuext).toUpperCase()).append("\n").append(context.getResources().getString(R.string.dots)).append("\n");
        builder.append(preferences.getGlExt().replace("[", "").replace("]", "").replace(", ", "\n").trim()).append("\n");
    }

    public void drmDetails() {
        MediaDrm mediaDrm;
        String[] wide = new String[9];
        String[] clearkey = new String[2];
        builder.append("\n").append(context.getResources().getString(R.string.drminfo).toUpperCase()).append("\n").append(context.getResources().getString(R.string.dots)).append("\n");
        try {
            mediaDrm = new MediaDrm(UUID.fromString("edef8ba9-79d6-4ace-a3c8-27dcd51d21ed"));
            wide[0] = mediaDrm.getPropertyString("vendor");
            wide[1] = mediaDrm.getPropertyString("version");
            wide[2] = mediaDrm.getPropertyString("algorithms");
            wide[3] = mediaDrm.getPropertyString("systemId");
            wide[4] = mediaDrm.getPropertyString("securityLevel");
            wide[5] = mediaDrm.getPropertyString("maxHdcpLevel");
            wide[6] = mediaDrm.getPropertyString("maxNumberOfSessions");
            wide[7] = mediaDrm.getPropertyString("usageReportingSupport");
            wide[8] = mediaDrm.getPropertyString("hdcpLevel");
            builder.append("• ").append(context.getResources().getString(R.string.widevine)).append("     \n");
            builder.append(context.getResources().getString(R.string.vendor)).append(" : ").append(wide[0]).append("\n").append(context.getResources().getString(R.string.version)).append(" : ").append(wide[1]).append("\n").append(context.getResources().getString(R.string.algo)).append(" : ").append(wide[2]).append("\n").append(context.getResources().getString(R.string.systemid)).append(" : ").append(wide[3]).append("\n").append(context.getResources().getString(R.string.security_level)).append(" : ").append(wide[4]).append("\n").append(context.getResources().getString(R.string.maxhdcp)).append(" : ").append(wide[5]).append("\n").append(context.getResources().getString(R.string.max_sessions)).append(" : ").append(wide[6]).append("\n").append(context.getResources().getString(R.string.usage_reporting)).append(" : ").append(wide[7]).append("\n").append(context.getResources().getString(R.string.hdcp)).append(" : ").append(wide[8]).append("\n\n");
            mediaDrm.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mediaDrm = new MediaDrm(UUID.fromString("e2719d58-a985-b3c9-781a-b030af78d30e"));
            clearkey[0] = mediaDrm.getPropertyString("vendor");
            clearkey[1] = mediaDrm.getPropertyString("version");
            builder.append("• ").append(context.getResources().getString(R.string.clearkey)).append("     \n");
            builder.append(context.getResources().getString(R.string.vendor)).append(" : ").append(clearkey[0]).append("\n").append(context.getResources().getString(R.string.version)).append(" : ").append(clearkey[1]).append("\n\n");
            mediaDrm.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void system() {
        builder.append("\n").append(context.getResources().getString(R.string.system).toUpperCase()).append("\n").append(context.getResources().getString(R.string.dots)).append("\n");
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = activityManager.getDeviceConfigurationInfo();
        int api = Build.VERSION.SDK_INT;
        String codename = context.getResources().getString(R.string.codename) + " : ";
        String sec_patch = context.getResources().getString(R.string.sec_patch) + " : ";
        String api_level = context.getResources().getString(R.string.api) + " : ";
        String bootloader = context.getResources().getString(R.string.bootloader) + " : ";
        String build_no = context.getResources().getString(R.string.build_no) + " : ";
        String baseband = context.getResources().getString(R.string.baseband) + " : ";
        String java_vm = context.getResources().getString(R.string.java_vm) + " : ";
        String kernel = context.getResources().getString(R.string.kernel) + " : ";
        String lang = context.getResources().getString(R.string.lang) + " : ";
        String opengl = context.getResources().getString(R.string.opengl) + " : ";
        String root = context.getResources().getString(R.string.root_access) + " : ";
        String selinux = context.getResources().getString(R.string.se_linux) + " : ";
        String playservices = context.getResources().getString(R.string.play_services) + " : ";
        String treble = context.getResources().getString(R.string.treble) + " : ";
        String updates = context.getResources().getString(R.string.updates) + " : ";
        String uptime = context.getResources().getString(R.string.uptime) + " : ";
        String released_date = context.getResources().getString(R.string.date) + " : ";
        builder.append("Android Version : ").append(buildInfo.getCodeName(api)).append("\n").append(released_date).append(buildInfo.getReleaseDate(api)).append("\n").append(codename).append(buildInfo.getCodeName(api)).append("\n");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            String patch = Build.VERSION.SECURITY_PATCH;
            builder.append(sec_patch).append(patch).append("\n");
        }
        builder.append(api_level).append(api).append("\n").append(bootloader).append(Build.BOOTLOADER).append("\n").append(build_no).append(Build.DISPLAY).append("\n").append(baseband).append(Build.getRadioVersion()).append("\n").append(java_vm).append(getProperty("java.vm.version")).append("\n").append(kernel).append(getProperty("os.version")).append("\n").append(lang).append(Locale.getDefault().getDisplayLanguage()).append("\n").append(opengl).append(info.getGlEsVersion()).append("\n").append(root).append(buildInfo.getRootInfo()).append("\n").append(treble).append(buildInfo.getTrebleInfo()).append("\n").append(updates).append(buildInfo.getSeamlessUpdatesInfo()).append("\n").append(selinux).append(buildInfo.getSeLinuxInfo()).append("\n").append(uptime).append(buildInfo.getUpTime()).append("\n").append(playservices).append(buildInfo.getPlayServices()).append("\n");
    }

    public void cpu() {
        int coresCount;
        String cpuArch, processorName = context.getResources().getString(R.string.unknown), cpuHardware = null, governor, cpuType, cpuDriver, runningCpu, vulkan, abi, usage, bogoMips = null, features = null, osArch, cpuFreq, gpuRenderer, gpuVersion, gpuVendor, totalExtensions;
        builder.append("\n").append(context.getResources().getString(R.string.cpu).toUpperCase()).append("\n").append(context.getResources().getString(R.string.dots)).append("\n");
        abi = Arrays.toString(Build.SUPPORTED_ABIS).replace("[", "").replace("]", "");
        try {
            String a, b, key, value, str;
            BufferedReader reader = new BufferedReader(new FileReader("/proc/cpuinfo"));
            while ((str = reader.readLine()) != null) {
                String[] data = str.trim().split(":");
                a = Arrays.toString(data);
                b = a.replace("[", "").replace("]", "").trim();
                if (b.contains(",")) {
                    key = b.substring(0, b.indexOf(","));
                    value = b.substring(b.indexOf(",") + 1);
                    if (key.trim().equals("Processor"))
                        processorName = value.trim();
                    if (key.trim().equalsIgnoreCase("model name"))
                        processorName = value.trim();
                    if (key.trim().equalsIgnoreCase("hardware"))
                        cpuHardware = value.trim();
                    if (key.trim().equalsIgnoreCase("bogomips"))
                        bogoMips = value.trim();
                    if (key.trim().equalsIgnoreCase("features"))
                        features = value.trim();
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        coresCount = buildInfo.getCoresCount();
        cpuArch = buildInfo.getCpuArchitecture();
        governor = buildInfo.getCpuGovernor();
        cpuDriver = buildInfo.getCpuDriver();
        runningCpu = buildInfo.getRunningCpuString();
        cpuFreq = buildInfo.getCpuFrequency();
        usage = buildInfo.getUsage();
        osArch = System.getProperty("os.arch");
        if (Arrays.toString(Build.SUPPORTED_ABIS).contains("64"))
            cpuType = "64 Bit";
        else
            cpuType = "32 Bit";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            vulkan = context.getResources().getString(R.string.not_supported);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            vulkan = context.getResources().getString(R.string.supported) + " (1.0)";
        } else {
            vulkan = context.getResources().getString(R.string.supported) + " (1.1)";
        }
        builder.append(context.getResources().getString(R.string.processorname)).append(" : ").append(processorName).append("\n").append(context.getResources().getString(R.string.cpu_hardware)).append(" : ").append(cpuHardware).append("\n").append(context.getResources().getString(R.string.abi)).append(" : ").append(abi).append("\n").append(context.getResources().getString(R.string.cpu_arch)).append(" : ").append(cpuArch).append("\n").append(context.getResources().getString(R.string.cores)).append(" : ").append(coresCount).append("\n").append(context.getResources().getString(R.string.governor)).append(" : ").append(governor).append("\n").append(context.getResources().getString(R.string.cpu_type)).append(" : ").append(cpuType).append(" (").append(osArch).append(")").append("\n").append(context.getResources().getString(R.string.cpu_driver)).append(" : ").append(cpuDriver).append("\n").append(context.getResources().getString(R.string.frequency)).append(" : ").append(cpuFreq).append("\n").append(context.getResources().getString(R.string.cpu_running)).append(" : ").append("\n").append(runningCpu).append("\n").append(context.getResources().getString(R.string.cpu_usage)).append(" : ").append(usage).append("\n");
        if (bogoMips != null)
            builder.append(context.getResources().getString(R.string.bogomips)).append(" : ").append(bogoMips).append("\n");
        if (features != null)
            builder.append(context.getResources().getString(R.string.features)).append(" : ").append(features).append("\n");
        builder.append(context.getResources().getString(R.string.vulkan)).append(" : ").append(vulkan).append("\n");

        String cpuFamily = jsonData.getCpuFamily();
        String process = jsonData.getProcess();
        if (!cpuFamily.equals(""))
            builder.append(context.getResources().getString(R.string.cpufamily)).append(" : \n").append(cpuFamily).append("\n");
        if (!process.equals(""))
            builder.append(context.getResources().getString(R.string.cpuprocess)).append(" : ").append(process).append("\n");
        if (preferences.getGpu()) {
            gpuRenderer = preferences.getGlRenderer();
            gpuVendor = preferences.getGlVendor();
            gpuVersion = preferences.getGlVersion();
            totalExtensions = preferences.getGlExtLength();
            builder.append(context.getResources().getString(R.string.gpu_render)).append(" : ").append(gpuRenderer).append("\n").append(context.getResources().getString(R.string.gpu_vendor)).append(" : ").append(gpuVendor).append("\n").append(context.getResources().getString(R.string.gpu_version)).append(" : ").append(gpuVersion).append("\n").append(context.getResources().getString(R.string.gpu_extension)).append(" : ").append(totalExtensions).append("\n");
        }
    }

    public void battery() {
        builder.append("\n").append(context.getResources().getString(R.string.battery).toUpperCase()).append("\n").append(context.getResources().getString(R.string.dots)).append("\n");
        String health = context.getResources().getString(R.string.health) + " : ";
        String status = context.getResources().getString(R.string.status) + " : ";
        String level = context.getResources().getString(R.string.level) + " : ";
        String voltage = context.getResources().getString(R.string.voltage) + " : ";
        String source = context.getResources().getString(R.string.power_source) + " : ";
        String tech = context.getResources().getString(R.string.tech) + " : ";
        String temp = context.getResources().getString(R.string.temp) + " : ";
        String capacity = context.getResources().getString(R.string.capacity) + " : ";
        builder.append(health).append(buildInfo.getBatteryHealth()).append("\n").append(status).append(buildInfo.getBatteryStatus()).append("\n").append(level).append(buildInfo.getBatteryLevel()).append("\n").append(voltage).append(buildInfo.getVoltage()).append("\n").append(source).append(buildInfo.getPowerSource()).append("\n").append(tech).append(buildInfo.getBatteryTechnology()).append("\n").append(temp).append(buildInfo.getBatteryTemp()).append("\n").append(capacity).append(buildInfo.getBatteryCapacity()).append("\n");
    }

    public void sensor() {
        builder.append("\n").append(context.getResources().getString(R.string.sensors).toUpperCase()).append("\n").append(context.getResources().getString(R.string.dots)).append("\n");
        builder.append(buildInfo.getSensorsCount()).append(" ").append(context.getResources().getString(R.string.sensor_avai)).append(".\n\n");

        String vendor = context.getResources().getString(R.string.vendor) + " : ";
        String sensor_type = context.getResources().getString(R.string.type) + " : ";
        String power = context.getResources().getString(R.string.power) + " : ";

        List<Sensor> sensors;
        SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensors = sm.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s : sensors) {
            String type;
            builder.append("• ").append(s.getName()).append("\n\n  ").append(vendor).append(s.getVendor()).append("\n  ").append(power).append(s.getPower()).append("mA").append("\n  ");
            if (!s.getStringType().contains("android")) {
                type = "PRIVATE SENSOR";
            } else {
                type = s.getStringType().replace("android.sensor.", "").toUpperCase();
                if (type.contains("_")) {
                    type = type.replace("_", " ");
                }
            }
            if (type != null) {
                builder.append(sensor_type).append(type).append("\n\n");
            }
        }
    }

    @SuppressLint("DefaultLocale")
    public void display() {
        String resolution = context.getResources().getString(R.string.resol) + " : ";
        String density = context.getResources().getString(R.string.density) + " : ";
        String fontscale = context.getResources().getString(R.string.fontscale) + " : ";
        String size = context.getResources().getString(R.string.size) + " : ";
        String refreshrate = context.getResources().getString(R.string.ref_rate) + " : ";
        String hdr = context.getResources().getString(R.string.hdr) + " : ";
        String hdr_cap = context.getResources().getString(R.string.hdr_cap) + " : ";
        String bright_level = context.getResources().getString(R.string.brightness) + " : ";
        String bright_mode = context.getResources().getString(R.string.bright_mode) + " : ";
        String screen_timeout = context.getResources().getString(R.string.timeout) + " : ";
        String orientation = context.getResources().getString(R.string.orientation) + " : ";
        String pixel = context.getResources().getString(R.string.pixel);

        builder.append("\n").append(context.getResources().getString(R.string.display).toUpperCase()).append("\n").append(context.getResources().getString(R.string.dots)).append("\n");
        int h, w, dpi;
        String refreshRate;
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        h = metrics.heightPixels;
        w = metrics.widthPixels;
        dpi = metrics.densityDpi;
        refreshRate = String.format("%.1f", ((Activity) context).getWindowManager().getDefaultDisplay().getRefreshRate()) + " Hz";
        int brightness = 0, time = 0;
        float font = 0.0f;
        String bright, timeout, fontstr;
        try {
            brightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            time = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
            font = context.getResources().getConfiguration().fontScale;

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        bright = ((brightness * 100) / 255) + " %";
        timeout = (time / 1000) + " " + context.getResources().getString(R.string.seconds);
        fontstr = String.valueOf(font);
        String hdr_support = context.getResources().getString(R.string.supported);
        String hdr_capable = context.getResources().getString(R.string.none);
        StringBuilder str = new StringBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Display.HdrCapabilities hdrCapabilities = ((Activity) context).getWindowManager().getDefaultDisplay().getHdrCapabilities();
            if (hdrCapabilities.getSupportedHdrTypes().length == 0) {
                hdr_support = context.getResources().getString(R.string.not_supported);
            } else {
                int[] hdrtypes = hdrCapabilities.getSupportedHdrTypes();
                for (int hdrtype : hdrtypes) {
                    switch (hdrtype) {
                        case 1:
                            str.append("Dolby Vision HDR\n");
                            break;
                        case 2:
                            str.append("HDR10\n");
                            break;
                        case 3:
                            str.append("Hybrid Log-Gamma HDR\n");
                            break;
                        case 4:
                            str.append("HDR10+\n");
                            break;
                    }
                }
            }
            if (!str.toString().equals(""))
                hdr_capable = str.toString().substring(0, str.toString().length() - 1);
        }
        builder.append(resolution).append(w).append(" x ").append(h).append(" ").append(pixel).append("\n").append(density).append(dpi).append(" dpi").append(buildInfo.getDensityDpi()).append("\n").append(fontscale).append(fontstr).append("\n").append(size).append(buildInfo.getScreenSize()).append("\n").append(refreshrate).append(refreshRate).append("\n").append(hdr).append(hdr_support).append("\n").append(hdr_cap).append(hdr_capable).append("\n").append(bright_level).append(bright).append("\n").append(bright_mode).append(buildInfo.getBrightnessMode()).append("\n").append(screen_timeout).append(timeout).append("\n").append(orientation).append(buildInfo.getOrientation()).append("\n");
    }

    public void memory() {
        String ram = context.getResources().getString(R.string.ram);
        String sys = context.getResources().getString(R.string.sys_store);
        String internal = context.getResources().getString(R.string.in_store);
        String external = context.getResources().getString(R.string.ext_store);
        String totalMem = context.getResources().getString(R.string.total_mem) + " : ";
        String usedMem = context.getResources().getString(R.string.used_mem) + " : ";
        String availMem = context.getResources().getString(R.string.avail_mem) + " : ";

        String ramType = jsonData.getMemory();
        String bandwidth = jsonData.getBandwidth();
        String channels = jsonData.getChannels();
        builder.append("\n").append(context.getResources().getString(R.string.memory).toUpperCase()).append("\n").append(context.getResources().getString(R.string.dots)).append("\n");
        String system = Environment.getRootDirectory().getPath();
        String data = Environment.getDataDirectory().getPath();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(memoryInfo);
        builder.append("• ").append(ram).append("\n\n   ").append(totalMem).append(formattedValue(memoryInfo.totalMem)).append("\n   ").append(usedMem).append(formattedValue(memoryInfo.totalMem - memoryInfo.availMem)).append("\n   ").append(availMem).append(formattedValue(memoryInfo.availMem)).append("\n\n");
        builder.append("\n• ").append(sys).append("\n\n   ").append(totalMem).append(formattedValue(buildInfo.getTotalStorageInfo(system))).append("\n   ").append(usedMem).append(formattedValue(buildInfo.getUsedStorageInfo(system))).append("\n   ").append(availMem).append(formattedValue(buildInfo.getTotalStorageInfo(system) - buildInfo.getUsedStorageInfo(system))).append("\n\n");
        builder.append("\n• ").append(internal).append("\n\n   ").append(totalMem).append(formattedValue(buildInfo.getTotalStorageInfo(data))).append("\n   ").append(usedMem).append(formattedValue(buildInfo.getUsedStorageInfo(data))).append("\n   ").append(availMem).append(formattedValue(buildInfo.getTotalStorageInfo(data) - buildInfo.getUsedStorageInfo(data))).append("\n\n");

        File[] files = ContextCompat.getExternalFilesDirs(context, null);
        if (files.length > 1 && files[0] != null && files[1] != null) {
            String path = files[1].getPath();
            builder.append("• ").append(external).append("\n\n   ").append(totalMem).append(formattedValue(buildInfo.getTotalStorageInfo(path))).append("\n   ").append(usedMem).append(formattedValue(buildInfo.getUsedStorageInfo(path))).append("\n   ").append(availMem).append(formattedValue(buildInfo.getTotalStorageInfo(path) - buildInfo.getUsedStorageInfo(path))).append("\n\n");
        }

        if (!ramType.equals(""))
            builder.append(context.getResources().getString(R.string.ramtype)).append(" : ").append(ramType).append("\n");
        if (!bandwidth.equals(""))
            builder.append(context.getResources().getString(R.string.bandwidth)).append(" : ").append(bandwidth).append("\n");
        if (!channels.equals(""))
            builder.append(context.getResources().getString(R.string.channels)).append(" : ").append(channels).append("\n");
    }

    public void codecs() {
        builder.append("\n").append(context.getResources().getString(R.string.codecs).toUpperCase()).append("\n").append(context.getResources().getString(R.string.dots)).append("\n");
        MediaCodecList codecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
        MediaCodecInfo[] mediaCodecInfo = codecList.getCodecInfos();
        for (MediaCodecInfo codecInfo : mediaCodecInfo) {
            builder.append("• ").append(codecInfo.getName()).append("\n").append("  ").append(Arrays.toString(codecInfo.getSupportedTypes()).trim().replace("[", "").replace("]", "")).append("\n");
        }
    }

    public void inputDevices() {
        builder.append("\n").append(context.getResources().getString(R.string.input).toUpperCase()).append("\n").append(context.getResources().getString(R.string.dots)).append("\n");
        List<InputDevice.MotionRange> motionRangeList;
        int[] id = InputDevice.getDeviceIds();
        for (int facing : id) {
            String name, desc, vendorId, proId, hasVibrator, keyboardType = null, deviceId, sources, s = null, source;
            StringBuilder stringBuilder = new StringBuilder();
            StringBuilder motionBuilder = new StringBuilder();
            InputDevice inputDevice = InputDevice.getDevice(facing);
            name = inputDevice.getName();
            vendorId = String.valueOf(inputDevice.getVendorId());
            proId = String.valueOf(inputDevice.getProductId());
            hasVibrator = String.valueOf(inputDevice.getVibrator().hasVibrator());
            switch (inputDevice.getKeyboardType()) {
                case 0:
                    keyboardType = "None";
                    break;
                case 1:
                    keyboardType = "Non-Alphabetic";
                    break;
                case 2:
                    keyboardType = "Alphabetic";
                    break;
            }
            deviceId = String.valueOf(inputDevice.getId());
            desc = inputDevice.getDescriptor();
            sources = "0x" + Integer.toHexString(inputDevice.getSources());
            if ((inputDevice.getSources() & InputDevice.SOURCE_KEYBOARD) == InputDevice.SOURCE_KEYBOARD) {
                stringBuilder.append("Keyboard, ");
            }
            if ((inputDevice.getSources() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK) {
                stringBuilder.append("JoyStick, ");
            }
            if ((inputDevice.getSources() & InputDevice.SOURCE_DPAD) == InputDevice.SOURCE_DPAD) {
                stringBuilder.append("Dpad, ");
            }
            if ((inputDevice.getSources() & InputDevice.SOURCE_MOUSE) == InputDevice.SOURCE_MOUSE) {
                stringBuilder.append("Mouse, ");
            }
            if ((inputDevice.getSources() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {
                stringBuilder.append("GamePad, ");
            }
            if ((inputDevice.getSources() & InputDevice.SOURCE_TOUCHPAD) == InputDevice.SOURCE_TOUCHPAD) {
                stringBuilder.append("TouchPad, ");
            }
            if ((inputDevice.getSources() & InputDevice.SOURCE_TRACKBALL) == InputDevice.SOURCE_TRACKBALL) {
                stringBuilder.append("TrackBall, ");
            }
            if ((inputDevice.getSources() & InputDevice.SOURCE_STYLUS) == InputDevice.SOURCE_STYLUS) {
                stringBuilder.append("Stylus, ");
            }
            if ((inputDevice.getSources() & InputDevice.SOURCE_TOUCHSCREEN) == InputDevice.SOURCE_TOUCHSCREEN) {
                stringBuilder.append("TouchScreen, ");
            }
            if (stringBuilder.toString().length() > 0)
                s = sources + " (" + stringBuilder.toString().substring(0, stringBuilder.toString().length() - 2) + ")";
            motionRangeList = inputDevice.getMotionRanges();
            if (motionRangeList.size() != 0) {
                for (int i = 0; i < motionRangeList.size(); i++) {
                    source = "0x" + Integer.toHexString(motionRangeList.get(i).getSource());
                    motionBuilder.append(context.getResources().getString(R.string.axis)).append(" : ").append(buildInfo.getAxis(motionRangeList.get(i).getAxis())).append("\n").append(context.getResources().getString(R.string.range)).append(" : ").append(motionRangeList.get(i).getRange()).append("\n").append(context.getResources().getString(R.string.resol)).append(" : ").append(motionRangeList.get(i).getResolution()).append("\n").append(context.getResources().getString(R.string.flat)).append(" : ").append(motionRangeList.get(i).getFlat()).append("\n").append(context.getResources().getString(R.string.fuzz)).append(" : ").append(motionRangeList.get(i).getFuzz()).append("\n").append(context.getResources().getString(R.string.source)).append(" : ").append(source).append("\n");
                }
            }
            if (Integer.parseInt(deviceId) >= 0) {
                if (s != null && motionBuilder.toString().length() == 0) {
                    builder.append("• ").append(name).append("\n\n").append(context.getResources().getString(R.string.inputdevice)).append(" : ").append(facing).append("\n").append(context.getResources().getString(R.string.vendorid)).append(" : ").append(vendorId).append("\n").append(context.getResources().getString(R.string.productid)).append(" : ").append(proId).append("\n").append(context.getResources().getString(R.string.keyboardtype)).append(" : ").append(keyboardType).append("\n").append(context.getResources().getString(R.string.descriptor)).append(" : ").append(desc).append("\n").append(context.getResources().getString(R.string.hasvibrator)).append(" : ").append(hasVibrator).append("\n");
                    builder.append(context.getResources().getString(R.string.sources)).append(" : ").append(s).append("\n\n");
                } else if (s != null && motionBuilder.toString().length() > 0) {
                    builder.append("• ").append(name).append("\n\n").append(context.getResources().getString(R.string.inputdevice)).append(" : ").append(facing).append("\n").append(context.getResources().getString(R.string.vendorid)).append(" : ").append(vendorId).append("\n").append(context.getResources().getString(R.string.productid)).append(" : ").append(proId).append("\n").append(context.getResources().getString(R.string.keyboardtype)).append(" : ").append(keyboardType).append("\n").append(context.getResources().getString(R.string.descriptor)).append(" : ").append(desc).append("\n").append(context.getResources().getString(R.string.hasvibrator)).append(" : ").append(hasVibrator).append("\n");
                    builder.append(context.getResources().getString(R.string.sources)).append(" : ").append(s).append("\n");
                } else if (s == null && motionBuilder.toString().length() == 0) {
                    builder.append("• ").append(name).append("\n\n").append(context.getResources().getString(R.string.inputdevice)).append(" : ").append(facing).append("\n").append(context.getResources().getString(R.string.vendorid)).append(" : ").append(vendorId).append("\n").append(context.getResources().getString(R.string.productid)).append(" : ").append(proId).append("\n").append(context.getResources().getString(R.string.keyboardtype)).append(" : ").append(keyboardType).append("\n").append(context.getResources().getString(R.string.descriptor)).append(" : ").append(desc).append("\n").append(context.getResources().getString(R.string.hasvibrator)).append(" : ").append(hasVibrator).append("\n\n");
                } else if (s == null && motionBuilder.toString().length() > 0) {
                    builder.append("• ").append(name).append("\n\n").append(context.getResources().getString(R.string.inputdevice)).append(" : ").append(facing).append("\n").append(context.getResources().getString(R.string.vendorid)).append(" : ").append(vendorId).append("\n").append(context.getResources().getString(R.string.productid)).append(" : ").append(proId).append("\n").append(context.getResources().getString(R.string.keyboardtype)).append(" : ").append(keyboardType).append("\n").append(context.getResources().getString(R.string.descriptor)).append(" : ").append(desc).append("\n").append(context.getResources().getString(R.string.hasvibrator)).append(" : ").append(hasVibrator).append("\n");
                }
                if (motionBuilder.toString().length() > 0) {
                    builder.append(motionBuilder.toString()).append("\n");
                }
            }
        }
    }

    public void cameraApi21() {
        builder.append("\n").append(context.getResources().getString(R.string.camera).toUpperCase()).append("\n").append(context.getResources().getString(R.string.dots)).append("\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                cameraIds = cameraManager.getCameraIdList();
                for (String cameraId : cameraIds) {
                    camera21Facing(Integer.parseInt(cameraId));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void camera21Facing(int facing) {
        String lensFacing = null, maxFaceCount = null, iso = null;
        String thumbnailSizes = null, aberrationMode = null, sceneModes = null, abModes = null, aeModes = null, afModes = null, awbModes = null, effects = null, edgeModes = null, videoModes = null, hotPixelModes = null, capabilities = null;
        String faceDetectModes = null, opticalStable = null, testPattern = null, resolutions = null;
        String pixelSize = null, colorFilter = null, timeStamp = null, sensorSize = null, orientation = null, croppingType = null, maxZoom = null, maxOutProc = null, maxOutProcStall = null, maxOutRaw = null, focalLengths = null, filterDensity = null;
        String apertures = null, maxRegionsAe = null, maxRegionsAf = null, maxRegionsAwb = null, compStep = null, partialCount = null, flashAvailable = "No", hardwareLevel = null, focusCaliber = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                characteristics = cameraManager.getCameraCharacteristics(String.valueOf(facing));
                switch (characteristics.get(CameraCharacteristics.LENS_FACING)) {
                    case 0:
                        lensFacing = "Front";
                        builder.append("\n• ").append(context.getResources().getString(R.string.frontcam)).append("     \n\n");
                        break;
                    case 1:
                        lensFacing = "Back";
                        builder.append("\n• ").append(context.getResources().getString(R.string.backcam)).append("     \n\n");
                        break;
                    case 2:
                        lensFacing = "External";
                        builder.append("\n• ").append(context.getResources().getString(R.string.extcam)).append("     \n\n");
                        break;
                }
                thumbnailSizes = Arrays.toString(characteristics.get(CameraCharacteristics.JPEG_AVAILABLE_THUMBNAIL_SIZES)).replace("[", "").replace("]", "").replace(", ", "\n").replace("x", " x ");
                if (characteristics.get(CameraCharacteristics.LENS_INFO_FOCUS_DISTANCE_CALIBRATION) != null)
                    switch (characteristics.get(CameraCharacteristics.LENS_INFO_FOCUS_DISTANCE_CALIBRATION)) {
                        case 0:
                            focusCaliber = "Uncalibrated";
                            break;
                        case 1:
                            focusCaliber = "Approximate";
                            break;
                        case 2:
                            focusCaliber = "Calibrated";
                            break;
                    }
                if (characteristics.get(CameraCharacteristics.SCALER_CROPPING_TYPE) != null)
                    switch (characteristics.get(CameraCharacteristics.SCALER_CROPPING_TYPE)) {
                        case 0:
                            croppingType = "Center Only";
                            break;
                        case 1:
                            croppingType = "FreeForm";
                            break;
                    }
                aeModes = buildInfo.aeCamera(facing);
                abModes = buildInfo.abCamera(facing);
                afModes = buildInfo.afCamera(facing);
                awbModes = buildInfo.awbCamera(facing);
                testPattern = buildInfo.testModesCamera(facing);
                videoModes = buildInfo.videoModesCamera(facing);
                edgeModes = buildInfo.edgeModesCamera(facing);
                hotPixelModes = buildInfo.hotPixelCamera(facing);
                effects = buildInfo.effCamera(facing);
                sceneModes = buildInfo.sceneCamera(facing);
                capabilities = buildInfo.camCapCamera(facing);
                faceDetectModes = buildInfo.fdCamera(facing);
                aberrationMode = buildInfo.amCamera(facing);
                opticalStable = buildInfo.osCamera(facing);
                resolutions = buildInfo.resolutionsCamera(facing);
                compStep = String.valueOf(characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP));
                maxRegionsAe = String.valueOf(characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE));
                maxRegionsAf = String.valueOf(characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF));
                maxRegionsAwb = String.valueOf(characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AWB));
                hardwareLevel = "Level " + characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                maxOutProc = String.valueOf(characteristics.get(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_PROC));
                maxOutProcStall = String.valueOf(characteristics.get(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_PROC_STALLING));
                maxOutRaw = String.valueOf(characteristics.get(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_RAW));
                maxZoom = String.valueOf(characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM));
                pixelSize = String.valueOf(characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)).replace("x", " x ");
                sensorSize = String.valueOf(characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)).replace("x", " x ");
                orientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) + " deg";
                if (characteristics.get(CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE) != null)
                    switch (characteristics.get(CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE)) {
                        case 0:
                            timeStamp = "Unknown";
                            break;
                        case 1:
                            timeStamp = "Realtime";
                            break;
                    }
                partialCount = String.valueOf(characteristics.get(CameraCharacteristics.REQUEST_PARTIAL_RESULT_COUNT));
                if (characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE))
                    flashAvailable = "Yes";
                filterDensity = Arrays.toString(characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FILTER_DENSITIES)).replace("[", "").replace("]", "");
                apertures = Arrays.toString(characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES)).replace("[", "").replace("]", "");
                focalLengths = Arrays.toString(characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)).replace("[", "").replace("]", " mm").replace(",", "mm");
                maxFaceCount = String.valueOf(characteristics.get(CameraCharacteristics.STATISTICS_INFO_MAX_FACE_COUNT));
                iso = String.valueOf(characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE)).replace("[", "").replace("]", "").replace(", ", " - ");
                if (characteristics.get(CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT) != null)
                    switch (characteristics.get(CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT)) {
                        case 0:
                            colorFilter = "RGGB";
                            break;
                        case 1:
                            colorFilter = "GRBG";
                            break;
                        case 2:
                            colorFilter = "GBRG";
                            break;
                        case 3:
                            colorFilter = "BGGR";
                            break;
                        case 4:
                            colorFilter = "RGB";
                            break;
                        case 5:
                            colorFilter = "MONO";
                            break;
                        case 6:
                            colorFilter = "NIR";
                            break;
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
            builder.append(context.getResources().getString(R.string.aberration)).append(" : ").append(aberrationMode).append("\n");
            builder.append(context.getResources().getString(R.string.antibanding)).append(" : ").append(abModes).append("\n");
            builder.append(context.getResources().getString(R.string.autoexposure)).append(" : ").append(aeModes).append("\n");
            if (compStep != null)
                builder.append(context.getResources().getString(R.string.compen_step)).append(" : ").append(compStep).append("\n");
            builder.append(context.getResources().getString(R.string.autofocus)).append(" : ").append(afModes).append("\n");
            builder.append(context.getResources().getString(R.string.effects)).append(" : ").append(effects).append("\n");
            builder.append(context.getResources().getString(R.string.scenemodes)).append(" : ").append(sceneModes).append("\n");
            builder.append(context.getResources().getString(R.string.videostable_modes)).append(" : ").append(videoModes).append("\n");
            builder.append(context.getResources().getString(R.string.autowhitebalance)).append(" : ").append(awbModes).append("\n");
            if (maxRegionsAe != null || !maxRegionsAe.equalsIgnoreCase("null"))
                builder.append(context.getResources().getString(R.string.max_auto_exposure)).append(" : ").append(maxRegionsAe).append("\n");
            if (maxRegionsAf != null || !maxRegionsAf.equalsIgnoreCase("null"))
                builder.append(context.getResources().getString(R.string.autofocusreg)).append(" : ").append(maxRegionsAf).append("\n");
            if (maxRegionsAwb != null || !maxRegionsAwb.equalsIgnoreCase("null"))
                builder.append(context.getResources().getString(R.string.whitebalreg)).append(" : ").append(maxRegionsAwb).append("\n");
            builder.append(context.getResources().getString(R.string.edgemodes)).append(" : ").append(edgeModes).append("\n");
            builder.append(context.getResources().getString(R.string.flashavail)).append(" : ").append(flashAvailable).append("\n");
            builder.append(context.getResources().getString(R.string.hotpixelmode)).append(" : ").append(hotPixelModes).append("\n");
            builder.append(context.getResources().getString(R.string.hardwarelevel)).append(" : ").append(hardwareLevel).append("\n");
            if (thumbnailSizes != null && thumbnailSizes.equalsIgnoreCase("null"))
                builder.append(context.getResources().getString(R.string.thumbnailsize)).append(" : ").append(thumbnailSizes).append("\n");
            builder.append(context.getResources().getString(R.string.lensplacement)).append(" : ").append(lensFacing).append("\n");
            if (apertures != null && !apertures.equalsIgnoreCase("null"))
                builder.append(context.getResources().getString(R.string.apertures)).append(" : ").append(apertures).append("\n");
            if (filterDensity != null && !filterDensity.equalsIgnoreCase("null"))
                builder.append(context.getResources().getString(R.string.filter_dens)).append(" : ").append(filterDensity).append("\n");
            if (focalLengths != null && !focalLengths.equalsIgnoreCase("null"))
                builder.append(context.getResources().getString(R.string.focal_length)).append(" : ").append(focalLengths).append("\n");
            builder.append(context.getResources().getString(R.string.optical_stable)).append(" : ").append(opticalStable).append("\n");
            if (focusCaliber != null)
                builder.append(context.getResources().getString(R.string.focus_dist)).append(" : ").append(focusCaliber).append("\n");
            builder.append(context.getResources().getString(R.string.camera_capablity)).append(" : ").append(capabilities).append("\n");
            if (maxOutProc != null && !maxOutProc.equalsIgnoreCase("null"))
                builder.append(context.getResources().getString(R.string.max_out_stream)).append(" : ").append(maxOutProc).append("\n");
            if (maxOutProcStall != null && !maxOutProcStall.equalsIgnoreCase("null"))
                builder.append(context.getResources().getString(R.string.max_out_stream_stalling)).append(" : ").append(maxOutProcStall).append("\n");
            if (maxOutRaw != null && !maxOutRaw.equalsIgnoreCase("null"))
                builder.append(context.getResources().getString(R.string.max_raw_out_stream)).append(" : ").append(maxOutRaw).append("\n");
            if (partialCount != null && !partialCount.equalsIgnoreCase("null"))
                builder.append(context.getResources().getString(R.string.partial_res)).append(" : ").append(partialCount).append("\n");
            if (maxZoom != null && !maxZoom.equalsIgnoreCase("null"))
                builder.append(context.getResources().getString(R.string.max_dig_zoom)).append(" : ").append(maxZoom).append("\n");
            if (croppingType != null)
                builder.append(context.getResources().getString(R.string.cropping_type)).append(" : ").append(croppingType).append("\n");
            builder.append(context.getResources().getString(R.string.supported_resol)).append(" : ").append(resolutions).append("\n");
            builder.append(context.getResources().getString(R.string.test_pat_mode)).append(" : ").append(testPattern).append("\n");
            if (colorFilter != null)
                builder.append(context.getResources().getString(R.string.color_filter_arrg)).append(" : ").append(colorFilter).append("\n");
            if (sensorSize != null)
                builder.append(context.getResources().getString(R.string.sensor_size)).append(" : ").append(sensorSize).append("\n");
            if (pixelSize != null)
                builder.append(context.getResources().getString(R.string.pixel_array_size)).append(" : ").append(pixelSize).append("\n");
            if (timeStamp != null)
                builder.append(context.getResources().getString(R.string.timestamp_source)).append(" : ").append(timeStamp).append("\n");
            if (orientation != null)
                builder.append(context.getResources().getString(R.string.orientation)).append(" : ").append(orientation).append("\n");
            builder.append(context.getResources().getString(R.string.face_detect_mode)).append(" : ").append(faceDetectModes).append("\n");
            if (maxFaceCount != null)
                builder.append(context.getResources().getString(R.string.max_face_count)).append(" : ").append(maxFaceCount).append("\n");
            if (iso != null)
                builder.append(context.getResources().getString(R.string.iso_range)).append(" : ").append(iso).append("\n");
        }
    }

    @SuppressLint("DefaultLocale")
    public String formattedValue(long l) {
        float f;
        String s;
        if (l > 1024 * 1024 * 1024 * 10.0) {
            f = (float) l / (1024 * 1024 * 1024);
            s = String.format("%.1f", f) + " GB";
        } else if (l > 1024 * 1024 * 10.0 && l <= 1024 * 1024 * 1024 * 10.0) {
            f = (float) l / (1024 * 1024);
            s = String.format("%.1f", f) + " MB";
        } else if (l > 1024 && l <= 1024 * 1024 * 10.0) {
            f = (float) l / (1024);
            s = String.format("%.1f", f) + " KB";
        } else {
            f = (float) l;
            s = String.format("%.2f", f) + " Bytes";
        }
        return s;
    }

    public String getBuilder() {
        return builder.toString();
    }
}
