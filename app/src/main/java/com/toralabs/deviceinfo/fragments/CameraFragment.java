package com.toralabs.deviceinfo.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PorterDuff;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.adapter.CameraAdapter;
import com.toralabs.deviceinfo.adapter.SimpleAdapter;
import com.toralabs.deviceinfo.impClasses.BuildInfo;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.CameraModel;
import com.toralabs.deviceinfo.models.SimpleModel;

import java.util.ArrayList;
import java.util.Arrays;

public class CameraFragment extends Fragment implements CameraAdapter.CameraClickListener, Handler.Callback {
    CameraManager cameraManager;
    Preferences preferences;
    ArrayList<SimpleModel> cameraList = new ArrayList<>();
    ArrayList<CameraModel> cameraModelList = new ArrayList<>();
    String[] cameraIds;
    Size[] sizes;
    TextView txt_noCamera;
    int FRONT = 1, BACK = 0, EXTERNAL = 2;
    CameraCharacteristics characteristics;
    BuildInfo buildInfo;
    StreamConfigurationMap streamConfigurationMap;
    String readThis;
    int color;
    String id;
    RecyclerView recycler_camera, recycler_horizon;
    SimpleAdapter simpleAdapter;
    CameraAdapter cameraAdapter;
    int lensPlacement;
    String pixel;
    boolean bool = true;
    String[] focus;
    String lensSide;
    ProgressBar progressBar;
    Handler mainHandler;
    int pos;
    HandlerThread handlerThread = new HandlerThread("CameraThread");
    HandlerThread horizonThread = new HandlerThread("HorizonThread");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null)
            preferences = new Preferences(getContext());
        color = Color.parseColor(preferences.getCircleColor());
        buildInfo = new BuildInfo(getContext());
        mainHandler = new Handler(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getContext() != null) {
            cameraManager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
            try {
                cameraIds = cameraManager.getCameraIdList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (cameraIds.length == 0) {
            bool = false;
        } else {
            if (savedInstanceState != null) {
                cameraList = savedInstanceState.getParcelableArrayList("cameraList");
                cameraModelList = savedInstanceState.getParcelableArrayList("horizonList");
                pos = savedInstanceState.getInt("position");
            } else {
                handlerThread.start();
                horizonThread.start();
                horizon();
                camera(BACK);
                pos = 0;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_camera, container, false);
        setHasOptionsMenu(true);
        txt_noCamera = view.findViewById(R.id.txt_nocamera);
        recycler_camera = view.findViewById(R.id.recycler_camera);
        recycler_horizon = view.findViewById(R.id.recycler_horizon);
        progressBar = view.findViewById(R.id.progressBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            progressBar.getIndeterminateDrawable().setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
        else
            progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        if (!bool) {
            txt_noCamera.setTextColor(color);
            recycler_horizon.setVisibility(View.GONE);
            recycler_camera.setVisibility(View.GONE);
            txt_noCamera.setVisibility(View.VISIBLE);
        }
        if (savedInstanceState != null) {
            cameraModelList.get(pos).setSelected(true);
            cameraAdapter = new CameraAdapter(getContext(), color, cameraModelList, this);
            recycler_horizon.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
            recycler_horizon.setAdapter(cameraAdapter);
            recycler_horizon.setVisibility(View.VISIBLE);
            updateUI();
        }
        return view;
    }

    public void camera(final int lensFace) {
        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                camera2Details(lensFace);
                Bundle bundle = new Bundle();
                bundle.putInt("update", 1);
                Message message = new Message();
                message.setData(bundle);
                mainHandler.sendMessage(message);
            }
        });
    }

    @SuppressLint("DefaultLocale")
    public void horizon() {
        Handler handler = new Handler(horizonThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    try {
                        cameraIds = cameraManager.getCameraIdList();
                        for (String cameraId : cameraIds) {
                            id = cameraId;
                            characteristics = cameraManager.getCameraCharacteristics(cameraId);
                            lensPlacement = characteristics.get(CameraCharacteristics.LENS_FACING);
                            switch (lensPlacement) {
                                case 0:
                                    lensSide = "Front";
                                    break;
                                case 1:
                                    lensSide = "Back";
                                    break;
                                default:
                                    lensSide = "External";
                                    break;
                            }
                            focus = Arrays.toString(characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)).replace("[", "").replace("]", "").split(", ");
                            streamConfigurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                            sizes = streamConfigurationMap.getOutputSizes(ImageFormat.RAW_SENSOR);
                            if (sizes != null)
                                pixel = sizes[0].getWidth() + " x " + sizes[0].getHeight();
                            else {
                                sizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
                                if (sizes != null)
                                    pixel = sizes[0].getWidth() + " x " + sizes[0].getHeight();
                            }
                            if (sizes != null)
                                cameraModelList.add(new CameraModel(String.format("%.1f", sizes[0].getWidth() * sizes[0].getHeight() / 1000000.0) + " MP - " + lensSide, pixel, focus[0] + " mm", false));
                        }
                        Bundle bundle = new Bundle();
                        bundle.putInt("update", 2);
                        Message message = new Message();
                        message.setData(bundle);
                        mainHandler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void camera2Details(int facing) {
        String lensFacing = null, maxFaceCount = null, iso = null;
        String thumbnailSizes = null, aberrationMode = null, sceneModes = null, abModes = null, aeModes = null, afModes = null, awbModes = null, effects = null, edgeModes = null, videoModes = null, hotPixelModes = null, capabilities = null;
        String faceDetectModes = null, opticalStable = null, testPattern = null, resolutions = null;
        String pixelSize = null, colorFilter = null, timeStamp = null, sensorSize = null, orientation = null, croppingType = null, maxZoom = null, maxOutProc = null, maxOutProcStall = null, maxOutRaw = null, focalLengths = null, filterDensity = null;
        String apertures = null, maxRegionsAe = null, maxRegionsAf = null, maxRegionsAwb = null, compStep = null, partialCount = null, flashAvailable = "No", hardwareLevel = null, focusCaliber = null;

        if (cameraList != null)
            cameraList.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                characteristics = cameraManager.getCameraCharacteristics(String.valueOf(facing));
                switch (characteristics.get(CameraCharacteristics.LENS_FACING)) {
                    case 0:
                        lensFacing = "Front";
                        break;
                    case 1:
                        lensFacing = "Back";
                        break;
                    case 2:
                        lensFacing = "External";
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
        }
        readThis = getResources().getString(R.string.read_this);
        cameraList.add(new SimpleModel(getResources().getString(R.string.readthis), readThis));
        cameraList.add(new SimpleModel(getResources().getString(R.string.aberration), aberrationMode));
        cameraList.add(new SimpleModel(getResources().getString(R.string.antibanding), abModes));
        cameraList.add(new SimpleModel(getResources().getString(R.string.autoexposure), aeModes));
        if (compStep != null)
            cameraList.add(new SimpleModel(getResources().getString(R.string.compen_step), compStep));
        cameraList.add(new SimpleModel(getResources().getString(R.string.autofocus), afModes));
        cameraList.add(new SimpleModel(getResources().getString(R.string.effects), effects));
        cameraList.add(new SimpleModel(getResources().getString(R.string.scenemodes), sceneModes));
        cameraList.add(new SimpleModel(getResources().getString(R.string.videostable_modes), videoModes));
        cameraList.add(new SimpleModel(getResources().getString(R.string.autowhitebalance), awbModes));
        if (maxRegionsAe != null || !maxRegionsAe.equalsIgnoreCase("null"))
            cameraList.add(new SimpleModel(getResources().getString(R.string.max_auto_exposure), maxRegionsAe));
        if (maxRegionsAf != null || !maxRegionsAf.equalsIgnoreCase("null"))
            cameraList.add(new SimpleModel(getResources().getString(R.string.autofocusreg), maxRegionsAf));
        if (maxRegionsAwb != null || !maxRegionsAwb.equalsIgnoreCase("null"))
            cameraList.add(new SimpleModel(getResources().getString(R.string.whitebalreg), maxRegionsAwb));
        cameraList.add(new SimpleModel(getResources().getString(R.string.edgemodes), edgeModes));
        cameraList.add(new SimpleModel(getResources().getString(R.string.flashavail), flashAvailable));
        cameraList.add(new SimpleModel(getResources().getString(R.string.hotpixelmode), hotPixelModes));
        cameraList.add(new SimpleModel(getResources().getString(R.string.hardwarelevel), hardwareLevel));
        if (thumbnailSizes != null && thumbnailSizes.equalsIgnoreCase("null"))
            cameraList.add(new SimpleModel(getResources().getString(R.string.thumbnailsize), thumbnailSizes));
        cameraList.add(new SimpleModel(getResources().getString(R.string.lensplacement), lensFacing));
        if (apertures != null && !apertures.equalsIgnoreCase("null"))
            cameraList.add(new SimpleModel(getResources().getString(R.string.apertures), apertures));
        if (filterDensity != null && !filterDensity.equalsIgnoreCase("null"))
            cameraList.add(new SimpleModel(getResources().getString(R.string.filter_dens), filterDensity));
        if (focalLengths != null && !focalLengths.equalsIgnoreCase("null"))
            cameraList.add(new SimpleModel(getResources().getString(R.string.focal_length), focalLengths));
        cameraList.add(new SimpleModel(getResources().getString(R.string.optical_stable), opticalStable));
        if (focusCaliber != null)
            cameraList.add(new SimpleModel(getResources().getString(R.string.focus_dist), focusCaliber));
        cameraList.add(new SimpleModel(getResources().getString(R.string.camera_capablity), capabilities));
        if (maxOutProc != null && !maxOutProc.equalsIgnoreCase("null"))
            cameraList.add(new SimpleModel(getResources().getString(R.string.max_out_stream), maxOutProc));
        if (maxOutProcStall != null && !maxOutProcStall.equalsIgnoreCase("null"))
            cameraList.add(new SimpleModel(getResources().getString(R.string.max_out_stream_stalling), maxOutProcStall));
        if (maxOutRaw != null && !maxOutRaw.equalsIgnoreCase("null"))
            cameraList.add(new SimpleModel(getResources().getString(R.string.max_raw_out_stream), maxOutRaw));
        if (partialCount != null && !partialCount.equalsIgnoreCase("null"))
            cameraList.add(new SimpleModel(getResources().getString(R.string.partial_res), partialCount));
        if (maxZoom != null && !maxZoom.equalsIgnoreCase("null"))
            cameraList.add(new SimpleModel(getResources().getString(R.string.max_dig_zoom), maxZoom));
        if (croppingType != null)
            cameraList.add(new SimpleModel(getResources().getString(R.string.cropping_type), croppingType));
        cameraList.add(new SimpleModel(getResources().getString(R.string.supported_resol), resolutions));
        cameraList.add(new SimpleModel(getResources().getString(R.string.test_pat_mode), testPattern));
        if (colorFilter != null)
            cameraList.add(new SimpleModel(getResources().getString(R.string.color_filter_arrg), colorFilter));
        if (sensorSize != null)
            cameraList.add(new SimpleModel(getResources().getString(R.string.sensor_size), sensorSize));
        if (pixelSize != null)
            cameraList.add(new SimpleModel(getResources().getString(R.string.pixel_array_size), pixelSize));
        if (timeStamp != null)
            cameraList.add(new SimpleModel(getResources().getString(R.string.timestamp_source), timeStamp));
        if (orientation != null)
            cameraList.add(new SimpleModel(getResources().getString(R.string.orientation), orientation));
        cameraList.add(new SimpleModel(getResources().getString(R.string.face_detect_mode), faceDetectModes));
        if (maxFaceCount != null)
            cameraList.add(new SimpleModel(getResources().getString(R.string.max_face_count), maxFaceCount));
        if (iso != null && !iso.equalsIgnoreCase("null"))
            cameraList.add(new SimpleModel(getResources().getString(R.string.iso_range), iso));
    }

    public void updateUI() {
        simpleAdapter = new SimpleAdapter(cameraList, getContext(), color);
        recycler_camera.setAdapter(simpleAdapter);
        recycler_camera.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        recycler_camera.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_camera.setNestedScrollingEnabled(false);
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
        if (horizonThread != null)
            horizonThread.quit();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("cameraList", cameraList);
        outState.putParcelableArrayList("horizonList", cameraModelList);
        outState.putInt("position", pos);
    }

    @Override
    public void onClickItem(int position, View v) {
        if (!cameraModelList.get(position).isSelected()) {
            cameraModelList.get(position).setSelected(true);
            switch (position) {
                case 0:
                    camera2Details(BACK);
                    pos = 0;
                    break;
                case 1:
                    camera2Details(FRONT);
                    pos = 1;
                    break;
                case 2:
                    camera2Details(EXTERNAL);
                    pos = 2;
                    break;
            }
            simpleAdapter.dataSetCamera(cameraList);
            recycler_camera.scheduleLayoutAnimation();
            cameraAdapter.notifyItemChanged(position, 1);
        }
        for (int i = 0; i < cameraModelList.size(); i++) {
            if (i != position) {
                cameraModelList.get(i).setSelected(false);
                cameraAdapter.notifyItemChanged(i, 2);
            }
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.getData().getInt("update")) {
            case 1:
                updateUI();
                break;
            case 2:
                cameraModelList.get(pos).setSelected(true);
                cameraAdapter = new CameraAdapter(getContext(), color, cameraModelList, this);
                recycler_horizon.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                recycler_horizon.setAdapter(cameraAdapter);
                recycler_horizon.setVisibility(View.VISIBLE);
                break;
        }
        return true;
    }
}
