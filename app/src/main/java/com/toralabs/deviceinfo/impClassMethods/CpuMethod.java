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
import android.os.Build;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.impClasses.BuildInfo;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.CpuModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class CpuMethod {
    private final Context context;
    Preferences preferences;
    BuildInfo buildInfo;
    int coresCount;
    ArrayList<CpuModel> cpuList = new ArrayList<>();
    String cpuArch, processorName, family, machine, processor, cpuHardware, governor, cpuType, cpuDriver, runningCpu, vulkan, abi, usage, bogoMips, features, osArch, cpuFreq, memory = "", bandwidth = "", channel = "";
    String json = null, process = "", cpuFamily = "";

    public CpuMethod(Context context) {
        this.context = context;
        preferences = new Preferences(context);
        buildInfo = new BuildInfo(context);
        getCpuInfo();
    }

    private void getCpuInfo() {
        getHeader();
        abi = Arrays.toString(Build.SUPPORTED_ABIS).replace("[", "").replace("]", "");
        if (processorName != null)
            cpuList.add(new CpuModel(context.getResources().getString(R.string.processor), processorName, "processorname"));
        if (cpuHardware == null)
            cpuList.add(new CpuModel(context.getResources().getString(R.string.cpu_hardware), Build.HARDWARE, "hardware"));
        else
            cpuList.add(new CpuModel(context.getResources().getString(R.string.cpu_hardware), cpuHardware, "hardware"));
        cpuList.add(new CpuModel(context.getResources().getString(R.string.abi), abi, "abi"));
        coresCount = buildInfo.getCoresCount();
        cpuArch = buildInfo.getCpuArchitecture();
        governor = buildInfo.getCpuGovernor();
        cpuDriver = buildInfo.getCpuDriver();
        runningCpu = buildInfo.getRunningCpuString();
        cpuFreq = buildInfo.getCpuFrequency();
        usage = buildInfo.getUsage();
        cpuList.add(new CpuModel(context.getResources().getString(R.string.cpu_arch), cpuArch, "cpuArch"));
        cpuList.add(new CpuModel(context.getResources().getString(R.string.cores), String.valueOf(coresCount), "cores"));
        cpuList.add(new CpuModel(context.getResources().getString(R.string.governor), governor, "governor"));
        osArch = System.getProperty("os.arch");
        if (Arrays.toString(Build.SUPPORTED_ABIS).contains("64"))
            cpuType = "64 Bit";
        else
            cpuType = "32 Bit";
        cpuList.add(new CpuModel(context.getResources().getString(R.string.cpu_type), cpuType + " (" + osArch + ")", "type"));
        cpuList.add(new CpuModel(context.getResources().getString(R.string.cpu_driver), cpuDriver, "driver"));
        cpuList.add(new CpuModel(context.getResources().getString(R.string.cpu_freq), cpuFreq, "frequency"));
        cpuList.add(new CpuModel(context.getResources().getString(R.string.cpu_running), runningCpu, "running"));
        cpuList.add(new CpuModel(context.getResources().getString(R.string.cpu_usage), usage, "usage"));
        getDataFromJson();
        if (bogoMips != null)
            cpuList.add(new CpuModel(context.getResources().getString(R.string.bogomips), bogoMips, "bogomips"));
        if (features != null)
            cpuList.add(new CpuModel(context.getResources().getString(R.string.features), features, "features"));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            vulkan = context.getResources().getString(R.string.not_supported);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            vulkan = context.getResources().getString(R.string.supported) + " (1.0)";
        } else {
            vulkan = context.getResources().getString(R.string.supported) + " (1.1)";
        }
        cpuList.add(new CpuModel(context.getResources().getString(R.string.vulkan), vulkan, "vulkan"));
    }

    private void getHeader() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("sys/devices/soc0/vendor"));
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                processor = str;
            }
        } catch (IOException e) {
            processor = "error";
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

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("sys/devices/soc0/family"));
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                family = str;
            }
        } catch (IOException e) {
            family = "error";
            e.printStackTrace();
        }
        try {
            String a, b, key, value, str;
            BufferedReader reader = new BufferedReader(new FileReader("/proc/cpuinfo"));
            while ((str = reader.readLine()) != null) {
                String[] data = str.trim().split(":");
                a = Arrays.toString(data);
                b = a.replace("[", "").replace("]", "").trim();
                if (b!=null && b.contains(",")) {
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
    }

    public void getDataFromJson() {
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
        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONObject object = jsonObject.getJSONObject(machine);
                if (!object.getString("CPU").equals("")) {
                    cpuFamily = object.getString("CPU");
                    cpuList.add(new CpuModel(context.getResources().getString(R.string.cpufamily), object.getString("CPU"), "cpuFamily"));
                }
                if (!object.getString("FAB").equals("")) {
                    process = object.getString("FAB");
                    cpuList.add(new CpuModel(context.getResources().getString(R.string.cpuprocess), object.getString("FAB"), "cpuProcess"));
                }
                memory = object.getString("MEMORY");
                bandwidth = object.getString("BANDWIDTH");
                channel = object.getString("CHANNELS");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<CpuModel> getCpuList() {
        return cpuList;
    }

    public String getFamily() {
        return family;
    }

    public String getMachine() {
        return machine;
    }

    public String getProcessor() {
        return processor;
    }

    public String getMemory() {
        return memory;
    }

    public String getBandwidth() {
        return bandwidth;
    }

    public String getChannel() {
        return channel;
    }

    public String getProcess() {
        return process;
    }

    public String getCpuFamily() {
        return cpuFamily;
    }
}
