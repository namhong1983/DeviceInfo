package com.toralabs.deviceinfo.impClassMethods;

import android.content.Context;
import android.view.InputDevice;

import com.toralabs.deviceinfo.impClasses.BuildInfo;
import com.toralabs.deviceinfo.models.InputModel;

import java.util.ArrayList;
import java.util.List;

public class InputDeviceMethod {
    private final Context context;
    ArrayList<InputModel> inputList = new ArrayList<>();
    List<InputDevice.MotionRange> motionRangeList;
    int[] id;
    BuildInfo buildInfo;
    int i=0;

    public InputDeviceMethod(Context context) {
        this.context = context;
        id = InputDevice.getDeviceIds();
        inputDevices();
    }

    private void inputDevices() {
        buildInfo = new BuildInfo(context);
        for (int facing : id) {
            String name, desc, vendorId, proId, hasVibrator, keyboardType = null, deviceId, sources;
            String axis = null, range = null, flat = null, fuzz = null, resol = null, source = null, s = null;
            boolean hasMotionRange = false;
            StringBuilder stringBuilder = new StringBuilder();
            motionRangeList = null;
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
                hasMotionRange = true;
                for (int i = 0; i < motionRangeList.size(); i++) {
                    axis = buildInfo.getAxis(motionRangeList.get(i).getAxis());
                    range = String.valueOf(motionRangeList.get(i).getRange());
                    resol = String.valueOf(motionRangeList.get(i).getResolution());
                    flat = String.valueOf(motionRangeList.get(i).getFlat());
                    fuzz = String.valueOf(motionRangeList.get(i).getFuzz());
                    source = "0x" + Integer.toHexString(motionRangeList.get(i).getSource());
                }
            }
            if (Integer.parseInt(deviceId) >= 0) {
                if(i==1){
                    inputList.add(new InputModel(name, desc, vendorId, proId, hasVibrator, keyboardType, deviceId, s, axis, range, flat, fuzz, resol, source, hasMotionRange));
                }
                inputList.add(new InputModel(name, desc, vendorId, proId, hasVibrator, keyboardType, deviceId, s, axis, range, flat, fuzz, resol, source, hasMotionRange));
                i++;
            }
        }
    }

    public ArrayList<InputModel> getInputList() {
        return inputList;
    }
}
