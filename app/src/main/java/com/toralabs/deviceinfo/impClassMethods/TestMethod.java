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
package com.toralabs.deviceinfo.impClassMethods;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.TestModel;

import java.util.ArrayList;
import java.util.Map;

public class TestMethod {
    private final Context context;
    Preferences preferences;
    FingerprintManager fingerprintManager;
    ArrayList<TestModel> testList = new ArrayList<>();
    SensorManager sensorManager;
    Sensor lightSensor, accelerometer, proximity;
    int id;
    Map<String, ?> map;

    public TestMethod(Context context) {
        this.context = context;
        preferences = new Preferences(context);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        }
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        map = preferences.getResults();
        makeTestList();
    }

    private void makeTestList() {
        switch ((Integer) map.get("display")) {
            case 0:
                id = R.drawable.ic_test_incomplete;
                break;
            case 1:
                id = R.drawable.ic_test_check;
                break;
            case 2:
                id = R.drawable.ic_test_cancel;
                break;
        }
        testList.add(new TestModel(R.drawable.ic_test_display, context.getString(R.string.display), id, "display"));
        testList.add(new TestModel(R.drawable.ic_test_multitouch, "Adding a Native Ad here", R.drawable.ic_test_incomplete, "NativeAd"));
        switch ((Integer) map.get("multitouch")) {
            case 0:
                id = R.drawable.ic_test_incomplete;
                break;
            case 1:
                id = R.drawable.ic_test_check;
                break;
            case 2:
                id = R.drawable.ic_test_cancel;
                break;
        }
        testList.add(new TestModel(R.drawable.ic_test_multitouch, context.getString(R.string.multitouch), id, "multitouch"));
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch ((Integer) map.get("flashlight")) {
                case 0:
                    id = R.drawable.ic_test_incomplete;
                    break;
                case 1:
                    id = R.drawable.ic_test_check;
                    break;
                case 2:
                    id = R.drawable.ic_test_cancel;
                    break;
            }
            testList.add(new TestModel(R.drawable.ic_test_flashlight, context.getString(R.string.flashlight), id, "flashlight"));
        }
        switch ((Integer) map.get("loudspeaker")) {
            case 0:
                id = R.drawable.ic_test_incomplete;
                break;
            case 1:
                id = R.drawable.ic_test_check;
                break;
            case 2:
                id = R.drawable.ic_test_cancel;
                break;
        }
        testList.add(new TestModel(R.drawable.ic_test_loudspeaker, context.getString(R.string.loudspeaker), id, "loudspeaker"));
        switch ((Integer) map.get("earspeaker")) {
            case 0:
                id = R.drawable.ic_test_incomplete;
                break;
            case 1:
                id = R.drawable.ic_test_check;
                break;
            case 2:
                id = R.drawable.ic_test_cancel;
                break;
        }
        testList.add(new TestModel(R.drawable.ic_test_earspeaker, context.getString(R.string.earspeaker), id, "earspeaker"));
        if (proximity != null) {
            switch ((Integer) map.get("earproximity")) {
                case 0:
                    id = R.drawable.ic_test_incomplete;
                    break;
                case 1:
                    id = R.drawable.ic_test_check;
                    break;
                case 2:
                    id = R.drawable.ic_test_cancel;
                    break;
            }
            testList.add(new TestModel(R.drawable.ic_test_earproximity, context.getString(R.string.earproximity), id, "earproximity"));
        }
        if (lightSensor != null) {
            switch ((Integer) map.get("lightsensor")) {
                case 0:
                    id = R.drawable.ic_test_incomplete;
                    break;
                case 1:
                    id = R.drawable.ic_test_check;
                    break;
                case 2:
                    id = R.drawable.ic_test_cancel;
                    break;
            }
            testList.add(new TestModel(R.drawable.ic_test_lightsensor, context.getString(R.string.lightsensor), id, "lightsensor"));
        }
        if (accelerometer != null) {
            switch ((Integer) map.get("accel")) {
                case 0:
                    id = R.drawable.ic_test_incomplete;
                    break;
                case 1:
                    id = R.drawable.ic_test_check;
                    break;
                case 2:
                    id = R.drawable.ic_test_cancel;
                    break;
            }
            testList.add(new TestModel(R.drawable.ic_test_acc, context.getString(R.string.accelerometer), id, "accel"));
        }
        switch ((Integer) map.get("vibration")) {
            case 0:
                id = R.drawable.ic_test_incomplete;
                break;
            case 1:
                id = R.drawable.ic_test_check;
                break;
            case 2:
                id = R.drawable.ic_test_cancel;
                break;
        }
        testList.add(new TestModel(R.drawable.ic_test_vibration, context.getString(R.string.vibration), id, "vibration"));
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null) {
                switch ((Integer) map.get("bluetooth")) {
                    case 0:
                        id = R.drawable.ic_test_incomplete;
                        break;
                    case 1:
                        id = R.drawable.ic_test_check;
                        break;
                    case 2:
                        id = R.drawable.ic_test_cancel;
                        break;
                }
                testList.add(new TestModel(R.drawable.ic_test_bluetooth, context.getString(R.string.bluetooth), id, "bluetooth"));
            }
        }
        switch ((Integer) map.get("volumeup")) {
            case 0:
                id = R.drawable.ic_test_incomplete;
                break;
            case 1:
                id = R.drawable.ic_test_check;
                break;
            case 2:
                id = R.drawable.ic_test_cancel;
                break;
        }
        testList.add(new TestModel(R.drawable.ic_test_volumeup, context.getString(R.string.volumeup), id, "volumeup"));
        switch ((Integer) map.get("volumedown")) {
            case 0:
                id = R.drawable.ic_test_incomplete;
                break;
            case 1:
                id = R.drawable.ic_test_check;
                break;
            case 2:
                id = R.drawable.ic_test_cancel;
                break;
        }
        testList.add(new TestModel(R.drawable.ic_test_volumedown, context.getString(R.string.volumedown), id, "volumedown"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            switch ((Integer) map.get("fingerprint")) {
                case 0:
                    id = R.drawable.ic_test_incomplete;
                    break;
                case 1:
                    id = R.drawable.ic_test_check;
                    break;
                case 2:
                    id = R.drawable.ic_test_cancel;
                    break;
            }
            if (fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints()) {
                testList.add(new TestModel(R.drawable.ic_test_fingerprint, context.getString(R.string.fingertest), id, "fingerprint"));
            } else if (fingerprintManager.isHardwareDetected() && !fingerprintManager.hasEnrolledFingerprints()) {
                testList.add(new TestModel(R.drawable.ic_test_fingerprint, context.getString(R.string.fingertest), R.drawable.ic_test_cancel, "fingerprint"));
            }
        }
    }

    public ArrayList<TestModel> getTestList() {
        return testList;
    }
}
