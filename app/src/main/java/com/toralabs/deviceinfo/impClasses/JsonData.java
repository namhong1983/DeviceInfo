package com.toralabs.deviceinfo.impClasses;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class JsonData {
    private String cpuFamily = "", process = "", memory = "", bandwidth = "", channels = "", json = null, machine;
    private final Context context;

    public JsonData(Context context) {
        this.context = context;
        getDataFromJson();
    }

    private void getDataFromJson() {
        try {
            InputStream inputStream = context.getAssets().open("socList.json");
            int size = inputStream.available();
            byte[] bytes = new byte[size];
            inputStream.read(bytes);
            inputStream.close();
            json = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("sys/devices/soc0/machine"));
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                machine = str;
            }
        } catch (IOException e) {
            machine = "error";
            e.printStackTrace();
        }
        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONObject object = jsonObject.getJSONObject(machine);
                if (!object.getString("CPU").equals(""))
                    cpuFamily = object.getString("CPU");
                if (!object.getString("FAB").equals(""))
                    process = object.getString("FAB");
                memory = object.getString("MEMORY");
                bandwidth = object.getString("BANDWIDTH");
                channels = object.getString("CHANNELS");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getCpuFamily() {
        return cpuFamily;
    }

    public String getProcess() {
        return process;
    }

    public String getMemory() {
        return memory;
    }

    public String getBandwidth() {
        return bandwidth;
    }

    public String getChannels() {
        return channels;
    }
}
