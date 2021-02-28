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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.impClasses.BuildInfo;
import com.toralabs.deviceinfo.models.SensorListModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by @mrudultora
 */

public class SensorListMethod {
    private final Context context;
    String name, vendor, power, type, type_specific;
    int imageId;
    List<Sensor> sensors = new ArrayList<>();
    ArrayList<SensorListModel> sensorList = new ArrayList<>();
    SensorManager sm;
    BuildInfo buildInfo;

    public SensorListMethod(Context context) {
        this.context = context;
        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorInfo();
    }

    private void sensorInfo() {
        buildInfo = new BuildInfo(context);
        sensors = sm.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s : sensors) {
            name = s.getName();
            vendor = s.getVendor();
            if (!s.getStringType().contains("android")) {
                type = "PRIVATE SENSOR";
            } else {
                type = s.getStringType().replace("android.sensor.", "").toUpperCase();
                if (type.contains("_")) {
                    type = type.replace("_", " ");
                }
            }
            power = s.getPower() + "mA";
            assert type != null;
            type_specific = type.toLowerCase().trim();
            if (type_specific.contains("accelerometer") || type_specific.contains("acceleration")) {
                imageId = R.drawable.sensor_accelerometer;
            } else if (type_specific.contains("magnetic")) {
                imageId = R.drawable.sensor_magnetic_field;
            } else if (type_specific.contains("gyroscope")) {
                imageId = R.drawable.sensor_gyroscope;
            } else if (type_specific.contains("proximity")) {
                imageId = R.drawable.sensor_proximity;
            } else if (type_specific.contains("light")) {
                imageId = R.drawable.sensor_light;
            } else if (type_specific.contains("motion")) {
                imageId = R.drawable.sensor_motion;
            } else if (type_specific.contains("rotation") || type_specific.contains("orientation")) {
                imageId = R.drawable.sensor_orientation;
            } else if (type_specific.contains("step")) {
                imageId = R.drawable.sensor_step;
            } else if (type_specific.contains("tilt")) {
                imageId = R.drawable.sensor_tilt;
            } else if (type_specific.contains("pick") || type_specific.contains("stationary")) {
                imageId = R.drawable.sensor_all;
            } else if (type_specific.contains("private")) {
                imageId = R.drawable.sensor_private;
            } else if (type_specific.contains("heart")) {
                imageId = R.drawable.sensor_heartrate;
            } else if (type_specific.contains("humidity")) {
                imageId = R.drawable.sensor_humidity;
            } else if (type_specific.contains("temperature")) {
                imageId = R.drawable.sensor_temperature;
            } else if (type_specific.contains("gravity")) {
                imageId = R.drawable.sensor_gravity;
            }
            sensorList.add(new SensorListModel(imageId, name, vendor, type, power));
        }
    }

    public ArrayList<SensorListModel> getSensorList() {
        return sensorList;
    }
}
