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

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;

import com.toralabs.deviceinfo.models.ThermalModel;

import java.util.ArrayList;
import java.util.Arrays;

public class CodecsMethod {
    int numCodecs;
    MediaCodecList codecList;
    ArrayList<ThermalModel> codecsList = new ArrayList<>();
    MediaCodecInfo[] mediaCodecInfo;

    public CodecsMethod() {
        codecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
        mediaCodecInfo = codecList.getCodecInfos();
        numCodecs = mediaCodecInfo.length;
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = mediaCodecInfo[i];
            codecsList.add(new ThermalModel(Arrays.toString(codecInfo.getSupportedTypes()).trim().replace("[", "").replace("]", ""), codecInfo.getName()));
        }
    }

    public ArrayList<ThermalModel> getCodecsList() {
        return codecsList;
    }
}
