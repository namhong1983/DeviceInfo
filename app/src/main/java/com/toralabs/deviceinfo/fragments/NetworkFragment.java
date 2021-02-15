package com.toralabs.deviceinfo.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.telephony.CellSignalStrength;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.adapter.SimpleAdapter;
import com.toralabs.deviceinfo.impClasses.BuildInfo;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.ClickableModel;
import com.toralabs.deviceinfo.models.SimpleModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class NetworkFragment extends Fragment implements View.OnClickListener, Handler.Callback, Runnable {
    Preferences preferences;
    BuildInfo buildInfo;
    List<SimpleModel> wifiList = new ArrayList<>();
    List<SimpleModel> mobileList = new ArrayList<>();
    List<SimpleModel> sim1List = new ArrayList<>();
    List<SimpleModel> defaultsList = new ArrayList<>();
    HandlerThread handlerThread = new HandlerThread("NetworkThread");
    Handler workerHandler, uiHandler;
    ImageView imgNetwork;
    ProgressBar progressBar;
    Button btnAllow;
    TextView txtNet, txtProvider, txtDbm, txtWifi, txtMobile, txtSim1, txtSim2;
    RecyclerView recyclerWifi, recyclerMobile, recyclerSim1, recyclerSim2, recyclerDefault;
    CardView cardNetwork;
    RelativeLayout cardWifi, cardMobile, cardDefaults, cardSim1, cardSim2, relNetwork, cardSimPermission;
    NestedScrollView nestedScroll;
    TelephonyManager telephonyManager;
    ConnectivityManager connectivityManager;
    String type, dbm;
    WifiManager wifiManager;
    DhcpInfo dhcpInfo;
    WifiInfo info;
    Network activeNetwork;
    IntentFilter intentFilter;
    BroadcastReceiver broadcastReceiver;
    SimpleAdapter simpleAdapterWifi, simpleAdapterMobile, simpleAdapterSim1, simpleAdapterSim2, simpleAdapterDefault;
    String simInfo, wifiStatus, mobileStatus, dualSim, phoneType, bssid, dhcpServer, dhcpLease, gatewayWifi, subnetMaskWifi, dns1, dns2, ipAddress, macAddress, interFaceWifi = null, directSupport, bandSupport, linkSpeed, signalStrength, frequency, channel;
    String ipv4, ipv6, subnetMobile, networkG;
    int level, color, phoneCount, asu;
    int READ_PHONE_STATE = 1001;
    ConnectivityManager.NetworkCallback networkCallback;
    NetworkCapabilities networkCapabilities;
    NetworkRequest networkRequestCell, networkRequestWifi;
    boolean darkMode, airplaneMode, simAvailable;
    List<CellSignalStrength> signalStrengthList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null)
            preferences = new Preferences(getContext());
        darkMode = preferences.getMode();
        buildInfo = new BuildInfo(getContext());
        color = Color.parseColor(preferences.getCircleColor());
        handlerThread.start();
        uiHandler = new Handler(this);
        workerHandler = new Handler(handlerThread.getLooper());
        telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        workerHandler.post(this);
        intentFilter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        if (Settings.Global.getInt(getContext().getContentResolver(), android.provider.Settings.Global.AIRPLANE_MODE_ON, 0) != 0) {
            onAirplaneMode();
            airplaneMode = true;
        }
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Settings.Global.getInt(context.getContentResolver(), android.provider.Settings.Global.AIRPLANE_MODE_ON, 0) != 0) {
                    onAirplaneMode();
                    airplaneMode = true;
                } else airplaneMode = false;
            }
        };
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                if (network != null) {
                    networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                    if (networkCapabilities != null) {
                        if (wifiManager.isWifiEnabled()) {
                            onWifiOn();
                            onCellularOff();
                        }
                        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                            onCellularOn();
                            onWifiOff();
                        }
                    }
                }
            }

            @Override
            public void onLost(@NonNull Network network) {
                if (!wifiManager.isWifiEnabled() && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) {
                    onWifiOff();
                    onCellularOn();
                }
                if (!wifiManager.isWifiEnabled() && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED) {
                    onWifiOff();
                }
                if (network != null) {
                    networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                    if (networkCapabilities != null) {
                        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                            onCellularOff();
                        }
                        if (!wifiManager.isWifiEnabled() && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                            bothOff();
                        }
                    }
                }
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();
                txtNet.setText(getResources().getString(R.string.notconnected));
                txtProvider.setText("");
                txtDbm.setText("");
                if (darkMode)
                    imgNetwork.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_signal_light));
                else
                    imgNetwork.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_signal_dark));
            }
        };
        networkRequestCell = new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).build();
        networkRequestWifi = new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build();
        connectivityManager.registerNetworkCallback(networkRequestCell, networkCallback);
        connectivityManager.registerNetworkCallback(networkRequestWifi, networkCallback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_network, container, false);
        setHasOptionsMenu(true);
        imgNetwork = view.findViewById(R.id.imgNetwork);
        txtNet = view.findViewById(R.id.txtNet);
        progressBar = view.findViewById(R.id.progressBar);
        txtProvider = view.findViewById(R.id.txtProvider);
        txtDbm = view.findViewById(R.id.txtDbm);
        relNetwork = view.findViewById(R.id.relNetwork);
        nestedScroll = view.findViewById(R.id.nestedScroll);
        recyclerMobile = view.findViewById(R.id.recyclerMobile);
        recyclerWifi = view.findViewById(R.id.recyclerWifi);
        recyclerSim1 = view.findViewById(R.id.recyclerSim1);
        recyclerSim2 = view.findViewById(R.id.recyclerSim2);
        recyclerDefault = view.findViewById(R.id.recyclerDefault);
        cardNetwork = view.findViewById(R.id.cardNetwork);
        cardWifi = view.findViewById(R.id.cardWifi);
        cardMobile = view.findViewById(R.id.cardMobile);
        cardSim1 = view.findViewById(R.id.cardSim1);
        cardDefaults = view.findViewById(R.id.cardDefault);
        cardSim2 = view.findViewById(R.id.cardSim2);
        txtWifi = view.findViewById(R.id.txtWifi);
        txtMobile = view.findViewById(R.id.txtMobile);
        txtSim1 = view.findViewById(R.id.txtSim1);
        txtSim2 = view.findViewById(R.id.txtSim2);
        cardSimPermission = view.findViewById(R.id.cardSimPermission);
        btnAllow = view.findViewById(R.id.btnAllow);
        relNetwork.setOnClickListener(this);
        btnAllow.setOnClickListener(this);
        if (getContext() != null && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            cardSimPermission.setVisibility(View.GONE);
            btnAllow.setVisibility(View.GONE);
        } else {
            getSimInfo();
        }
        return view;
    }

    @SuppressLint("PrivateApi")
    public void wifiInfo() {
        try {
            Method m = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
            interFaceWifi = (String) m.invoke(null, "wifi.interface");
            if (interFaceWifi == null || interFaceWifi.isEmpty() || interFaceWifi.equals(""))
                interFaceWifi = "Unknown";
        } catch (InvocationTargetException | NoSuchMethodException | ClassNotFoundException | IllegalAccessException e) {
            interFaceWifi = "Unknown";
            e.printStackTrace();
        }
        // check it in api 21-22
        try {
            Method m = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
            simInfo = (String) m.invoke(null, "persist.radio.multisim.config");
        } catch (InvocationTargetException | NoSuchMethodException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
        dualSim = getResources().getString(R.string.not_supported);
        switch (simInfo) {
            case "dsds":
                dualSim = getResources().getString(R.string.supported) + " (" + "Dual SIM Dual Standby" + ")";
                break;
            case "dsda":
                dualSim = getResources().getString(R.string.supported) + " (" + "Dual SIM Dual Active" + ")";
                break;
            case "tsts":
                dualSim = getResources().getString(R.string.supported);
                break;
        }
        phoneType = buildInfo.getPhoneType();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem item = menu.findItem(R.id.search);
        item.setVisible(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handlerThread != null)
            handlerThread.quit();
    }

    public String intToIp(int i) {
        return ((i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.relNetwork) {
            try {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            } catch (Exception e) {
            }
        } else if (v.getId() == R.id.btnAllow) {
            checkPhonePermissions();
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (getContext() != null)
            switch (msg.getData().getInt("update")) {
                case 0:
                    progressBar.setVisibility(View.GONE);
                    nestedScroll.setVisibility(View.VISIBLE);
                    simpleAdapterWifi = new SimpleAdapter(wifiList, getContext(), color);
                    recyclerWifi.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerWifi.setNestedScrollingEnabled(false);
                    recyclerWifi.setAdapter(simpleAdapterWifi);
                    simpleAdapterMobile = new SimpleAdapter(mobileList, getContext(), color);
                    recyclerMobile.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerMobile.setNestedScrollingEnabled(false);
                    recyclerMobile.setAdapter(simpleAdapterMobile);
                    txtNet.setTextColor(color);
                    txtProvider.setTextColor(color);
                    txtDbm.setTextColor(color);
                case 1:
                    type = String.valueOf(telephonyManager.getNetworkType());
                    networkG = buildInfo.getNetworkG(Integer.parseInt(type));
                    if (simpleAdapterWifi != null)
                        simpleAdapterWifi.notifyDataSetChanged();
                    txtNet.setText(getResources().getString(R.string.wifi));
                    break;
                case 2:
                    type = String.valueOf(telephonyManager.getNetworkType());
                    networkG = buildInfo.getNetworkG(Integer.parseInt(type));
                    if (simpleAdapterWifi != null)
                        simpleAdapterWifi.notifyDataSetChanged();
                    break;
                case 3:
                    type = String.valueOf(telephonyManager.getNetworkType());
                    networkG = buildInfo.getNetworkG(Integer.parseInt(type));
                    if (simpleAdapterMobile != null)
                        simpleAdapterMobile.notifyDataSetChanged();
                    txtNet.setText(getResources().getString(R.string.mobiledata));
                    break;
                case 4:
                    type = String.valueOf(telephonyManager.getNetworkType());
                    networkG = buildInfo.getNetworkG(Integer.parseInt(type));
                    if (simpleAdapterMobile != null)
                        simpleAdapterMobile.notifyDataSetChanged();
                    break;
                case 5:
                    if (simpleAdapterWifi != null)
                        simpleAdapterWifi.notifyDataSetChanged();
                    if (simpleAdapterMobile != null)
                        simpleAdapterMobile.notifyDataSetChanged();
                    txtNet.setText(getResources().getString(R.string.notconnected));
                    txtProvider.setText(getResources().getString(R.string.airplanemode));
                    txtDbm.setText("");
                    if (darkMode)
                        imgNetwork.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_signal_light));
                    else
                        imgNetwork.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_signal_dark));
                    break;
                case 6:
                    type = String.valueOf(telephonyManager.getNetworkType());
                    networkG = buildInfo.getNetworkG(Integer.parseInt(type));
                    txtNet.setText(getResources().getString(R.string.notconnected));
                    txtProvider.setText("");
                    txtDbm.setText("");
                    if (darkMode)
                        imgNetwork.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_signal_light));
                    else
                        imgNetwork.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_signal_dark));
                    break;
            }
        if (msg.getData().getInt("level") == 0 && getContext() != null) {
            switch (level) {
                case 0:
                    if (darkMode)
                        imgNetwork.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_signal_light));
                    else
                        imgNetwork.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_signal_dark));
                    break;
                case 1:
                    if (darkMode)
                        imgNetwork.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_signal1_dark));
                    else
                        imgNetwork.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_signal1_light));
                    break;
                case 2:
                    if (darkMode)
                        imgNetwork.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_signal2_dark));
                    else
                        imgNetwork.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_signal2_light));
                    break;
                case 3:
                    if (darkMode)
                        imgNetwork.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_signal3_dark));
                    else
                        imgNetwork.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_signal3_light));
                    break;
                case 4:
                    if (darkMode)
                        imgNetwork.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_signal4_dark));
                    else
                        imgNetwork.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_signal4_light));
                    break;
                case 5:
                    if (darkMode)
                        imgNetwork.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_signal_dark));
                    else
                        imgNetwork.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_signal_light));
                    break;
            }
        }
        if (msg.getData().getString("networkG") != null) {
            txtProvider.setText(msg.getData().getString("networkG"));
        }
        return true;
    }

    @Override
    public void run() {
        if (telephonyManager.getSimState() != TelephonyManager.SIM_STATE_ABSENT)
            simAvailable = true;
        wifiInfo();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activeNetwork = connectivityManager.getActiveNetwork();
            switch (checkConnectionStatusAboveM(activeNetwork)) {
                case 0:
                    bothOff();
                    break;
                case 1:
                    wifiOn();
                    break;
                case 2:
                    mobileDataOn();
                    break;
            }
        } else {
            switch (checkConnectionStatusBelowM()) {
                case 0:
                    bothOff();
                    break;
                case 1:
                    wifiOn();
                    break;
                case 2:
                    mobileDataOn();
                    break;
            }
        }
        try {
            Method m;
            m = telephonyManager.getClass().getDeclaredMethod("getPhoneCount");
            m.setAccessible(true);
            phoneCount = (Integer) m.invoke(telephonyManager);
        } catch (Exception e) {
        }
        sim1List.add(new SimpleModel(getResources().getString(R.string.state), buildInfo.getSimState()));
        sim1List.add(new SimpleModel(getResources().getString(R.string.operator), telephonyManager.getSimOperatorName()));
        sim1List.add(new SimpleModel(getResources().getString(R.string.operator_code), telephonyManager.getSimOperator()));
        sim1List.add(new SimpleModel(getResources().getString(R.string.country), telephonyManager.getNetworkCountryIso().toUpperCase()));
        sim1List.add(new SimpleModel(getResources().getString(R.string.networkType), buildInfo.getNetworkG(telephonyManager.getNetworkType())));
        sim1List.add(new SimpleModel(getResources().getString(R.string.phonetype), buildInfo.getPhoneType()));
        if (telephonyManager.isNetworkRoaming())
            sim1List.add(new SimpleModel(getResources().getString(R.string.roaming), "Enabled"));
        else
            sim1List.add(new SimpleModel(getResources().getString(R.string.roaming), "Disabled"));
        buildInfo.getIp6Address();
        buildInfo.getIp4Address();
        telephonyManager.getSimState();
        defaultsInfo();
        type = String.valueOf(telephonyManager.getNetworkType());
        networkG = buildInfo.getNetworkG(Integer.parseInt(type));
        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    signalStrengthList = signalStrength.getCellSignalStrengths();
                    if (signalStrengthList != null) {
                        asu = signalStrengthList.get(0).getDbm();
                        System.out.println("NetworkFragment: " + asu + " " + " " + signalStrengthList.toString());
                    }
                }
                if (signalStrength.isGsm()) {
                    asu = signalStrength.getGsmSignalStrength() * 2 - 113;
                    System.out.println("NetworkFragment strength: " + asu);
                    try {
                        Method m;
                        m = signalStrength.getClass().getDeclaredMethod("getGsmBitErrorRate");
                        m.setAccessible(true);
                        System.out.println("NetworkFragment strength: " + (Integer) m.invoke(signalStrength));
                    } catch (Exception e) {
                        System.out.println("NetworkFragment strength exception: " + e.getMessage() + " " + e.getCause());
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        level = signalStrength.getLevel();
                    } else {
                        int strength = signalStrength.getGsmSignalStrength();
                        if (strength == 99) {
                            level = 0;
                        } else if (strength > 30 && strength != 99) {
                            level = 4;
                        } else if (strength > 20 && strength < 30) {
                            level = 3;
                        } else if (strength < 20 && strength > 3) {
                            level = 2;
                        } else if (strength < 3) {
                            level = 1;
                        }
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        level = signalStrength.getLevel();
                    } else {
                        int strength = signalStrength.getCdmaDbm();
                        if (strength >= -60) {
                            level = 5;
                        } else if (strength >= -75) {
                            level = 4;
                        } else if (strength >= -85) {
                            level = 3;
                        } else if (strength >= -95) {
                            level = 2;
                        } else if (strength >= -100) {
                            level = 1;
                        } else {
                            level = 0;
                        }
                    }
                }
                Bundle bundle = new Bundle();
                Message message = new Message();
                bundle.putInt("level", 0);
                message.setData(bundle);
                uiHandler.sendMessage(message);
            }
        }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        Bundle bundle = new Bundle();
        bundle.putInt("update", 0);
        bundle.putString("networkG", networkG);
        Message message = new Message();
        message.setData(bundle);
        uiHandler.sendMessage(message);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
        if (getActivity() == null)
            return;
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        type = String.valueOf(telephonyManager.getNetworkType());
        networkG = buildInfo.getNetworkG(Integer.parseInt(type));
        txtProvider.setText(networkG);
        if (connectivityManager != null) {
            connectivityManager.registerNetworkCallback(networkRequestCell, networkCallback);
            connectivityManager.registerNetworkCallback(networkRequestWifi, networkCallback);
        }
        if (getActivity() == null)
            return;
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    public void onWifiOn() {
        if (wifiList != null)
            wifiList.clear();
        if (getContext() != null) {
            directSupport = getResources().getString(R.string.no);
            bandSupport = getResources().getString(R.string.no);
            wifiStatus = getResources().getString(R.string.connected);
        }
        if (getContext().getPackageManager().hasSystemFeature("android.hardware.wifi.direct") && getContext() != null)
            directSupport = getResources().getString(R.string.yes);
        if (wifiManager.is5GHzBandSupported() && getContext() != null)
            bandSupport = getResources().getString(R.string.yes);
        info = wifiManager.getConnectionInfo();
        dhcpInfo = wifiManager.getDhcpInfo();
        signalStrength = info.getRssi() + " dbm";
        dhcpServer = intToIp(dhcpInfo.serverAddress);
        dhcpLease = (dhcpInfo.leaseDuration) / (60 * 60) + " " + getResources().getString(R.string.hours);
        bssid = info.getBSSID();
        linkSpeed = info.getLinkSpeed() + " " + getResources().getString(R.string.mbps);
        ipAddress = intToIp(info.getIpAddress());
        frequency = info.getFrequency() + " MHz";
        macAddress = buildInfo.getMacAddress();
        subnetMaskWifi = intToIp(dhcpInfo.netmask);
        dns1 = intToIp(dhcpInfo.dns1);
        dns2 = intToIp(dhcpInfo.dns2);
        gatewayWifi = intToIp(dhcpInfo.gateway);
        channel = buildInfo.getWifiChannel(info.getFrequency());
        if (getContext() != null) {
            wifiList.add(new SimpleModel(getResources().getString(R.string.status), wifiStatus));
            wifiList.add(new SimpleModel(getResources().getString(R.string.bssid), bssid));
            wifiList.add(new SimpleModel(getResources().getString(R.string.dhcpserver), dhcpServer));
            wifiList.add(new SimpleModel(getResources().getString(R.string.dhcplease), dhcpLease));
            wifiList.add(new SimpleModel(getResources().getString(R.string.gateway), gatewayWifi));
            wifiList.add(new SimpleModel(getResources().getString(R.string.subnetmask), subnetMaskWifi));
            wifiList.add(new SimpleModel(getResources().getString(R.string.dns1), dns1));
            wifiList.add(new SimpleModel(getResources().getString(R.string.dns2), dns2));
            wifiList.add(new SimpleModel(getResources().getString(R.string.ipaddress), ipAddress));
            wifiList.add(new SimpleModel(getResources().getString(R.string.mac_address), macAddress));
            wifiList.add(new SimpleModel(getResources().getString(R.string.intf), interFaceWifi));
            wifiList.add(new SimpleModel(getResources().getString(R.string.wifidirect), directSupport));
            wifiList.add(new SimpleModel(getResources().getString(R.string.bandsupport), bandSupport));
            wifiList.add(new SimpleModel(getResources().getString(R.string.linkspeed), linkSpeed));
            wifiList.add(new SimpleModel(getResources().getString(R.string.signalstrength), signalStrength));
            wifiList.add(new SimpleModel(getResources().getString(R.string.frequency), frequency));
            wifiList.add(new SimpleModel(getResources().getString(R.string.channel), channel));
        }
        Bundle bundle = new Bundle();
        bundle.putInt("update", 1);
        Message message = new Message();
        message.setData(bundle);
        uiHandler.sendMessage(message);
    }

    public void onWifiOff() {
        if (wifiList != null)
            wifiList.clear();
        if (getContext() != null) {
            wifiStatus = getResources().getString(R.string.disconnected);
            wifiList.add(new SimpleModel(getResources().getString(R.string.status), wifiStatus));
            wifiList.add(new SimpleModel(getResources().getString(R.string.mac_address), "NA"));
            wifiList.add(new SimpleModel(getResources().getString(R.string.intf), interFaceWifi));
        }
        Bundle bundle = new Bundle();
        bundle.putInt("update", 2);
        Message message = new Message();
        message.setData(bundle);
        uiHandler.sendMessage(message);
    }

    public void onCellularOn() {
        if (mobileList != null)
            mobileList.clear();
        ipv4 = buildInfo.getIp4Address();
        ipv6 = buildInfo.getIp6Address();
        subnetMobile = buildInfo.subnet();
        if (getContext() != null) {
            mobileStatus = getResources().getString(R.string.connected);
            mobileList.add(new SimpleModel(getResources().getString(R.string.status), mobileStatus));
            mobileList.add(new SimpleModel(getResources().getString(R.string.phonetype), phoneType));
            mobileList.add(new SimpleModel(getResources().getString(R.string.dualsim), dualSim));
            mobileList.add(new SimpleModel(getResources().getString(R.string.ipaddress), ipv4));
            mobileList.add(new SimpleModel(getResources().getString(R.string.ipv6), ipv6));
            mobileList.add(new SimpleModel(getResources().getString(R.string.subnetmask), subnetMobile));
        }
        Bundle bundle = new Bundle();
        bundle.putInt("update", 3);
        Message message = new Message();
        message.setData(bundle);
        uiHandler.sendMessage(message);
    }

    public void onCellularOff() {
        if (mobileList != null)
            mobileList.clear();
        if (getContext() != null) {
            mobileStatus = getResources().getString(R.string.disconnected);
            mobileList.add(new SimpleModel(getResources().getString(R.string.status), mobileStatus));
            mobileList.add(new SimpleModel(getResources().getString(R.string.phonetype), phoneType));
            mobileList.add(new SimpleModel(getResources().getString(R.string.dualsim), dualSim));
        }
        Bundle bundle = new Bundle();
        bundle.putInt("update", 4);
        Message message = new Message();
        message.setData(bundle);
        uiHandler.sendMessage(message);
    }

    public void onAirplaneMode() {
        if (mobileList != null)
            mobileList.clear();
        if (wifiList != null)
            wifiList.clear();
        if (getContext() != null) {
            mobileStatus = getResources().getString(R.string.disconnected);
            mobileList.add(new SimpleModel(getResources().getString(R.string.status), mobileStatus));
            mobileList.add(new SimpleModel(getResources().getString(R.string.phonetype), phoneType));
            mobileList.add(new SimpleModel(getResources().getString(R.string.dualsim), dualSim));
            wifiStatus = getResources().getString(R.string.disconnected);
            wifiList.add(new SimpleModel(getResources().getString(R.string.status), wifiStatus));
            wifiList.add(new SimpleModel(getResources().getString(R.string.mac_address), "NA"));
        }
        Bundle bundle = new Bundle();
        bundle.putInt("update", 5);
        Message message = new Message();
        message.setData(bundle);
        uiHandler.sendMessage(message);
    }

    // Write methods for All the Sim Details available in the device....

    public void defaultsInfo() {
        if (simAvailable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            try {
                Method m;
                m = SubscriptionManager.class.getDeclaredMethod("getDefaultDataSubscriptionId");
                m.setAccessible(true);
                switch ((Integer) m.invoke(SubscriptionManager.class)) {
                    case -1:
                        defaultsList.add(new SimpleModel(getResources().getString(R.string.data), "Not Set"));
                        break;
                    case 1:
                        defaultsList.add(new SimpleModel(getResources().getString(R.string.data), "SIM 1"));
                        break;
                    case 2:
                        defaultsList.add(new SimpleModel(getResources().getString(R.string.data), "SIM 2"));
                        break;
                    case 3:
                        defaultsList.add(new SimpleModel(getResources().getString(R.string.data), "SIM 3"));
                        break;
                }
                m = SubscriptionManager.class.getDeclaredMethod("getDefaultVoiceSubscriptionId");
                m.setAccessible(true);
                switch ((Integer) m.invoke(SubscriptionManager.class)) {
                    case -1:
                        defaultsList.add(new SimpleModel(getResources().getString(R.string.voice), "Not Set"));
                        break;
                    case 1:
                        defaultsList.add(new SimpleModel(getResources().getString(R.string.voice), "SIM 1"));
                        break;
                    case 2:
                        defaultsList.add(new SimpleModel(getResources().getString(R.string.voice), "SIM 2"));
                        break;
                    case 3:
                        defaultsList.add(new SimpleModel(getResources().getString(R.string.voice), "SIM 3"));
                        break;
                }
                m = SubscriptionManager.class.getDeclaredMethod("getDefaultSmsSubscriptionId");
                m.setAccessible(true);
                switch ((Integer) m.invoke(SubscriptionManager.class)) {
                    case -1:
                        defaultsList.add(new SimpleModel(getResources().getString(R.string.sms), "Not Set"));
                        break;
                    case 1:
                        defaultsList.add(new SimpleModel(getResources().getString(R.string.sms), "SIM 1"));
                        break;
                    case 2:
                        defaultsList.add(new SimpleModel(getResources().getString(R.string.sms), "SIM 2"));
                        break;
                    case 3:
                        defaultsList.add(new SimpleModel(getResources().getString(R.string.sms), "SIM 3"));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public int checkConnectionStatusAboveM(Network network) {
        int i = 0;
        if (network != null) {
            networkCapabilities = connectivityManager.getNetworkCapabilities(network);
            if (networkCapabilities != null) {
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    i = 1;
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    i = 2;
                }
            }
        }
        return i;
    }

    public int checkConnectionStatusBelowM() {
        int i = 0;
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) {
            i = 2;
        } else if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            i = 1;
        }
        return i;
    }

    public void wifiOn() {
        onWifiOn();
        onCellularOff();
    }

    public void mobileDataOn() {
        onCellularOn();
        onWifiOff();
    }

    public void bothOff() {
        onCellularOff();
        onWifiOff();
        if (!airplaneMode) {
            Bundle bundle = new Bundle();
            bundle.putInt("update", 6);
            Message message = new Message();
            message.setData(bundle);
            uiHandler.sendMessage(message);
        }
    }

    private void checkPhonePermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE);
        }
    }

    private void getSimInfo(){
        if (simAvailable && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1 && defaultsList.size() != 0) {
            cardDefaults.setVisibility(View.VISIBLE);
            simpleAdapterDefault = new SimpleAdapter(defaultsList, getContext(), color);
            recyclerDefault.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerDefault.setNestedScrollingEnabled(false);
            recyclerDefault.setAdapter(simpleAdapterDefault);
            cardSim1.setVisibility(View.VISIBLE);
            simpleAdapterSim1 = new SimpleAdapter(sim1List, getContext(), color);
            recyclerSim1.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerSim1.setNestedScrollingEnabled(false);
            recyclerSim1.setAdapter(simpleAdapterSim1);
        }
//        if (Build.VERSION.SDK_INT > 22) {
//            manager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
//                info = manager.getActiveSubscriptionInfoList();
//                if (info != null) {
//                    for (int i = 0; i < info.size(); i++) {
//                        carrierName.add((String) info.get(i).getCarrierName());
//                    }
//                    for (int i = 0; i < info.size(); i++) {
//                        deviceList.add(new ClickableModel(context.getResources().getString(R.string.netop), carrierName.get(i), false));
//                    }
//                } else {
//                    deviceList.add(new ClickableModel(context.getResources().getString(R.string.netop), context.getResources().getString(R.string.nosim), false));
//                }
//            } else {
//                netOperator = context.getResources().getString(R.string.requires_per);
//                deviceList.add(new ClickableModel(context.getResources().getString(R.string.netop), netOperator, true));
//            }
//        } else {
//            netOperator = telephonyManager.getNetworkOperatorName();
//            deviceList.add(new ClickableModel(context.getResources().getString(R.string.netop), netOperator, false));
//        }
    }
}
