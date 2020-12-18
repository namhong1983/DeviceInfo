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
