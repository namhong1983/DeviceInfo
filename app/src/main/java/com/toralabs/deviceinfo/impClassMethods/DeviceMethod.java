package com.toralabs.deviceinfo.impClassMethods;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.impClasses.BuildInfo;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.ClickableModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DeviceMethod {
    ArrayList<ClickableModel> deviceList = new ArrayList<>();
    Context context;
    Preferences preferences;
    boolean darkMode, iconAvail = true;
    String usbSupport, androidID, model, manufacturer, brand, device_lav, board, hardware, netOperator, macAddress, fingerprint, phoneType, advertId;
    WifiManager wifiManager;
    TelephonyManager telephonyManager;
    SubscriptionManager manager;
    String timezone;
    int drawableId;
    List<SubscriptionInfo> info = new ArrayList<>();
    List<String> carrierName = new ArrayList<>();
    BuildInfo buildInfo;

    public DeviceMethod(Context context) {
        this.context = context;
        buildInfo = new BuildInfo(context);
        preferences = new Preferences(context);
        darkMode = preferences.getMode();
        device();
    }

    @SuppressLint({"PrivateApi", "HardwareIds"})
    private void device() {
        if (deviceList != null)
            deviceList.clear();
        usbSupport = context.getResources().getString(R.string.supported);
        if (context != null && !context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_USB_HOST)) {
            usbSupport = context.getResources().getString(R.string.not_supported);
        }
        if (context != null) {
            wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }
        model = Build.MODEL;
        manufacturer = Build.MANUFACTURER;
        brand = Build.BRAND;
        device_lav = Build.DEVICE;
        board = Build.BOARD;
        hardware = Build.HARDWARE;
        phoneType = buildInfo.getPhoneType();
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
            advertId = buildInfo.advertId();
        } else {
            advertId = "error";
        }
        SimpleDateFormat date = new SimpleDateFormat("z", Locale.getDefault());
        String time = date.format(System.currentTimeMillis());
        timezone = TimeZone.getDefault().getDisplayName() + " (" + time + ")";
        deviceList.add(new ClickableModel(context.getResources().getString(R.string.model), model, false));
        deviceList.add(new ClickableModel(context.getResources().getString(R.string.manu), manufacturer, false));
        deviceList.add(new ClickableModel(context.getResources().getString(R.string.brand), brand, false));
        deviceList.add(new ClickableModel(context.getResources().getString(R.string.device_lav), device_lav, false));
        deviceList.add(new ClickableModel(context.getResources().getString(R.string.board), board, false));
        deviceList.add(new ClickableModel(context.getResources().getString(R.string.hardware), hardware, false));
        deviceList.add(new ClickableModel(context.getResources().getString(R.string.device_id), androidID, false));
        deviceList.add(new ClickableModel(context.getResources().getString(R.string.devicetype), phoneType, false));
        if (Build.VERSION.SDK_INT > 22) {
            manager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                info = manager.getActiveSubscriptionInfoList();
                if (info != null) {
                    for (int i = 0; i < info.size(); i++) {
                        carrierName.add((String) info.get(i).getCarrierName());
                    }
                    for (int i = 0; i < info.size(); i++) {
                        deviceList.add(new ClickableModel(context.getResources().getString(R.string.netop), carrierName.get(i), false));
                    }
                } else {
                    deviceList.add(new ClickableModel(context.getResources().getString(R.string.netop), context.getResources().getString(R.string.nosim), false));
                }
            } else {
                netOperator = context.getResources().getString(R.string.requires_per);
                deviceList.add(new ClickableModel(context.getResources().getString(R.string.netop), netOperator, true));
            }
        } else {
            netOperator = telephonyManager.getNetworkOperatorName();
            deviceList.add(new ClickableModel(context.getResources().getString(R.string.netop), netOperator, false));
        }
        if (wifiManager.isWifiEnabled()) {
            macAddress = buildInfo.getMacAddress();
        } else {
            macAddress = context.getResources().getString(R.string.no_wifi);
        }
        deviceList.add(new ClickableModel(context.getResources().getString(R.string.mac_address), macAddress, false));
        fingerprint = Build.FINGERPRINT;
        deviceList.add(new ClickableModel(context.getResources().getString(R.string.build_print), fingerprint, false));
        deviceList.add(new ClickableModel(context.getResources().getString(R.string.usb_host), usbSupport, false));
        if (!advertId.equalsIgnoreCase("error"))
            deviceList.add(new ClickableModel(context.getResources().getString(R.string.advertid), advertId, false));
        deviceList.add(new ClickableModel(context.getResources().getString(R.string.timezone), timezone, false));
        deviceList.add(new ClickableModel(context.getResources().getString(R.string.devicefeatures), deviceFeatures() + " (" + context.getResources().getString(R.string.taphere) + ")", true));
    }

    public int deviceFeatures() {
        return context.getPackageManager().getSystemAvailableFeatures().length;
    }

    public void getIcon(String logo) {
        switch (logo.trim()) {
            case "acer":
                drawableId = R.drawable.ic_acer;
                break;
            case "alcatel":
                if (!darkMode) {
                    drawableId = R.drawable.ic_alcatel;
                } else {
                    drawableId = R.drawable.ic_alcatel_dark;
                }
                break;
            case "asus":
                drawableId = R.drawable.ic_asus;
                break;
            case "blackberry":
                drawableId = R.drawable.ic_blackberry;
                break;
            case "blackshark":
                drawableId = R.drawable.ic_blackshark;
                break;
            case "blu":
                drawableId = R.drawable.ic_blu;
                break;
            case "doogee":
                drawableId = R.drawable.ic_doogee;
                break;
            case "elementalx":
                drawableId = R.drawable.ic_elementalx;
                break;
            case "elephone":
                drawableId = R.drawable.ic_elephone;
                break;
            case "google":
                drawableId = R.drawable.ic_g_logo;
                break;
            case "gionee":
                drawableId = R.drawable.ic_gionee;
                break;
            case "hcl":
                drawableId = R.drawable.ic_hcl;
                break;
            case "honor":
                drawableId = R.drawable.ic_honor;
                break;
            case "htc":
                drawableId = R.drawable.ic_htc;
                break;
            case "huawei":
                if (!darkMode) {
                    drawableId = R.drawable.ic_huawei;
                } else {
                    drawableId = R.drawable.ic_huawei_dark;
                }
                break;
            case "iball":
                if (!darkMode) {
                    drawableId = R.drawable.ic_iball;
                } else {
                    drawableId = R.drawable.ic_iball_dark;
                }
                break;
            case "infinix":
                drawableId = R.drawable.ic_infinix;
                break;
            case "jio":
                drawableId = R.drawable.ic_jio;
                break;
            case "karbonn":
                drawableId = R.drawable.ic_karbonn;
                break;
            case "kyocera":
                drawableId = R.drawable.ic_kyocera;
                break;
            case "lava":
                drawableId = R.drawable.ic_lava;
                break;
            case "leeco":
                drawableId = R.drawable.ic_leeco;
                break;
            case "lenevo":
                drawableId = R.drawable.ic_lenovo;
                break;
            case "lg":
                drawableId = R.drawable.ic_lg;
                break;
            case "lyf":
                drawableId = R.drawable.ic_lyf;
                break;
            case "meizu":
                drawableId = R.drawable.ic_meizu;
                break;
            case "micromax":
                drawableId = R.drawable.ic_micromax;
                break;
            case "motorola":
                drawableId = R.drawable.ic_moto;
                break;
            case "nokia":
                drawableId = R.drawable.ic_nokia;
                break;
            case "nubia":
                drawableId = R.drawable.ic_nubia;
                break;
            case "oneplus":
                if (!darkMode) {
                    drawableId = R.drawable.ic_oneplus;
                } else {
                    drawableId = R.drawable.ic_oneplus_dark;
                }
                break;
            case "oppo":
                drawableId = R.drawable.ic_oppo;
                break;
            case "panasonic":
                drawableId = R.drawable.ic_panasonic;
                break;
            case "razer":
                drawableId = R.drawable.ic_razer;
                break;
            case "realme":
                drawableId = R.drawable.ic_realme;
                break;
            case "redmi":
            case "xiaomi":
                drawableId = R.drawable.ic_xiaomi;
                break;
            case "rockchip":
                drawableId = R.drawable.ic_rockchip;
                break;
            case "samsung":
                drawableId = R.drawable.ic_samsung;
                break;
            case "sharp":
                drawableId = R.drawable.ic_sharp;
                break;
            case "smartisan":
                drawableId = R.drawable.ic_smartisan;
                break;
            case "sony":
                drawableId = R.drawable.ic_sony;
                break;
            case "tcl":
                drawableId = R.drawable.ic_tcl;
                break;
            case "tecno":
                drawableId = R.drawable.ic_tecno;
                break;
            case "tegra":
                drawableId = R.drawable.ic_tegra;
                break;
            case "toshiba":
                drawableId = R.drawable.ic_toshiba;
                break;
            case "ulefone":
                drawableId = R.drawable.ic_ulefone;
                break;
            case "umidigi":
                drawableId = R.drawable.ic_umidigi;
                break;
            case "videocon":
                if (!darkMode) {
                    drawableId = R.drawable.ic_videocon;
                } else {
                    drawableId = R.drawable.ic_videocon_dark;
                }
                break;
            case "vivo":
                drawableId = R.drawable.ic_vivo;
                break;
            case "zte":
                drawableId = R.drawable.ic_zte;
                break;
            default:
                iconAvail = false;
                break;
        }
    }

    public boolean isIconAvail() {
        return iconAvail;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public ArrayList<ClickableModel> getDeviceList() {
        return deviceList;
    }
}
