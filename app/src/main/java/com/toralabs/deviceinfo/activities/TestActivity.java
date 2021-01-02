package com.toralabs.deviceinfo.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.menuItems.ThemeConstant;

import java.util.List;

public class TestActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {
    ImageView imgCancel, imgTest, imgSuccess;
    TextView txtQues, txtTouch, txtInfo;
    Button buttonNext, buttonDone;
    RelativeLayout relMain;
    String tag = "", status;
    LinearLayout linearLayout;
    int position;
    int color;
    ThemeConstant themeConstant;
    int themeNo;
    Preferences preferences;
    int count = 0;
    Paint paint = new Paint();
    Canvas canvas = new Canvas();
    SensorManager sensorManager;
    Sensor proximity, lightSensor, accelerometer;
    CameraManager cameraManager;
    CameraCharacteristics characteristics;
    Camera camera;
    String[] id;
    ProgressBar progressMic;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    boolean isSpeaker = false;
    boolean isProximity = false;
    boolean isLight = false;
    boolean isAcc = false;
    boolean isVibrating = false;
    boolean isVolumeUp = false;
    boolean isVolumeDown = false;
    Handler handler;
    int cameraId = 0;
    float x, y, z, lastX, lastY, lastZ;
    long lastUpdate = 0, diffTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = new Preferences(TestActivity.this);
        themeNo = preferences.getThemeNo();
        themeConstant = new ThemeConstant(themeNo);
        if (themeNo == 0) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(themeConstant.themeChooser());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        relMain = findViewById(R.id.relMain);
        imgCancel = findViewById(R.id.imgCancel);
        imgTest = findViewById(R.id.imgTest);
        buttonNext = findViewById(R.id.buttonNext);
        buttonDone = findViewById(R.id.buttonDone);
        imgSuccess = findViewById(R.id.imgSuccess);
        txtQues = findViewById(R.id.txtQues);
        txtTouch = findViewById(R.id.txtTouch);
        linearLayout = findViewById(R.id.linear);
        txtInfo = findViewById(R.id.txtInfo);
        progressMic = findViewById(R.id.progressMic);
        buttonNext.setOnClickListener(this);
        buttonDone.setOnClickListener(this);
        imgCancel.setOnClickListener(this);
        imgSuccess.setOnClickListener(this);
        color = Color.parseColor(preferences.getCircleColor());
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
        tag = getIntent().getStringExtra("tag");
        position = getIntent().getIntExtra("position", 0);
        if (tag.contains("display"))
            testDisplay();
        if (tag.contains("multitouch"))
            testMultitouch();
        if (tag.contains("flashlight"))
            testFlashLight();
        if (tag.contains("loudspeaker"))
            testLoudSpeaker();
        if (tag.contains("earspeaker"))
            testEarSpeaker();
        if (tag.contains("earproximity"))
            testProximity();
        if (tag.contains("lightsensor"))
            testLight();
        if (tag.contains("accel"))
            testAcc();
        if (tag.contains("vibration"))
            testVibration();
        if (tag.contains("bluetooth"))
            testBluetooth();
        if (tag.contains("volumeup"))
            testVolumeUp();
        if (tag.contains("volumedown"))
            testVolumeDown();
    }

    public void testVolumeUp() {
        isVolumeUp = true;
        imgTest.setVisibility(View.VISIBLE);
        imgTest.setImageDrawable(ContextCompat.getDrawable(TestActivity.this, R.drawable.ic_volume_up_image));
        txtQues.setText(getResources().getString(R.string.test_volumeup));
    }

    public void testVolumeDown() {
        isVolumeDown = true;
        imgTest.setVisibility(View.VISIBLE);
        imgTest.setImageDrawable(ContextCompat.getDrawable(TestActivity.this, R.drawable.ic_volume_down_image));
        txtQues.setText(getResources().getString(R.string.test_volumedown));
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (isVolumeUp) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                imgTest.setImageDrawable(ContextCompat.getDrawable(TestActivity.this, R.drawable.ic_volume_up_image_active));
                vibrateIt(600);
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && isVolumeDown) {
            imgTest.setImageDrawable(ContextCompat.getDrawable(TestActivity.this, R.drawable.ic_volume_down_image_active));
            vibrateIt(600);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void testDisplay() {
        txtQues.setText(getResources().getString(R.string.test_display));
        linearLayout.setVisibility(View.INVISIBLE);
        imgSuccess.setEnabled(false);
        imgCancel.setEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            buttonNext.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(preferences.getCircleColor())));
        } else
            buttonNext.setBackgroundColor(Color.parseColor(preferences.getCircleColor()));
        buttonNext.setVisibility(View.VISIBLE);
        imgTest.setImageDrawable(ContextCompat.getDrawable(TestActivity.this, R.drawable.ic_display));
    }

    @SuppressLint("SetTextI18n")
    public void testMultitouch() {
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        paint.setColor(getCircleColor(count));
        canvas.drawCircle(729, 622, 20, paint);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        imgTest.setVisibility(View.GONE);
        relMain.setBackgroundColor(Color.parseColor("#FF000000"));
        txtQues.setText(getResources().getString(R.string.test_multitouch));
        txtTouch.setVisibility(View.VISIBLE);
        txtQues.setTextColor(ContextCompat.getColor(TestActivity.this, R.color.white));
        txtTouch.setTextColor(ContextCompat.getColor(TestActivity.this, R.color.white));
        txtTouch.setText(getResources().getString(R.string.touchdet) + " : 0");
    }

    public void testFlashLight() {
        boolean avail = false;
        imgTest.setVisibility(View.VISIBLE);
        imgTest.setImageDrawable(ContextCompat.getDrawable(TestActivity.this, R.drawable.ic_flashlight));
        txtQues.setText(getResources().getString(R.string.test_flashlight));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                id = cameraManager.getCameraIdList();
                for (String ids : id) {
                    characteristics = cameraManager.getCameraCharacteristics(ids);
                    if (!avail) {
                        if (characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                            cameraId = Integer.parseInt(ids);
                            cameraManager.setTorchMode(ids, true);
                            avail = true;
                        }
                    }
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            if (ActivityCompat.checkSelfPermission(TestActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                flash();
            } else
                ActivityCompat.requestPermissions(TestActivity.this, new String[]{Manifest.permission.CAMERA}, 9);
        }
    }

    public void testLoudSpeaker() {
        isSpeaker = true;
        imgTest.setVisibility(View.VISIBLE);
        imgTest.setImageDrawable(ContextCompat.getDrawable(TestActivity.this, R.drawable.ic_speaker));
        txtQues.setText(getResources().getString(R.string.test_speaker));
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.STREAM_RING);
        audioManager.setSpeakerphoneOn(true);
        mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        if (mediaPlayer != null) {
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    public void testEarSpeaker() {
        isSpeaker = true;
        imgTest.setVisibility(View.VISIBLE);
        imgTest.setImageDrawable(ContextCompat.getDrawable(TestActivity.this, R.drawable.ic_earspeaker));
        txtQues.setText(getResources().getString(R.string.test_speaker));
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(false);
        mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        if (mediaPlayer != null) {
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    public void testProximity() {
        isProximity = true;
        imgTest.setVisibility(View.VISIBLE);
        imgTest.setImageDrawable(ContextCompat.getDrawable(TestActivity.this, R.drawable.ic_ear_blue));
        txtQues.setText(getResources().getString(R.string.test_earproximity));
    }

    public void testLight() {
        isLight = true;
        txtInfo.setVisibility(View.VISIBLE);
        imgTest.setVisibility(View.VISIBLE);
        imgTest.setImageDrawable(ContextCompat.getDrawable(TestActivity.this, R.drawable.ic_light_sensor));
        txtQues.setText(getResources().getString(R.string.test_lightsensor));
    }

    public void testAcc() {
        isAcc = true;
        imgTest.setVisibility(View.VISIBLE);
        imgTest.setImageDrawable(ContextCompat.getDrawable(TestActivity.this, R.drawable.ic_accel_image));
        txtQues.setText(getResources().getString(R.string.test_accel));
    }

    public void testVibration() {
        isVibrating = true;
        imgTest.setVisibility(View.VISIBLE);
        imgTest.setImageDrawable(ContextCompat.getDrawable(TestActivity.this, R.drawable.ic_vibration));
        txtQues.setText(getResources().getString(R.string.test_vibration));
        handler = new Handler();
        vibrateIt(600);
        vibrateRepeat();
    }

    public void testBluetooth() {
        imgTest.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.INVISIBLE);
        imgSuccess.setEnabled(false);
        imgCancel.setEnabled(false);
        imgTest.setImageDrawable(ContextCompat.getDrawable(TestActivity.this, R.drawable.ic_bluetooth_image));
        txtQues.setText(getResources().getString(R.string.test_bluetooth));
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            txtQues.setText(getResources().getString(R.string.test_bluetooth));
            Intent enableBlue = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            status = "cancel";
            startActivityForResult(enableBlue, 10);
        } else {
            imgTest.setImageDrawable(ContextCompat.getDrawable(TestActivity.this, R.drawable.ic_bluetooth_passed));
            txtQues.setText(getResources().getString(R.string.test_passed));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                buttonDone.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(preferences.getCircleColor())));
            } else
                buttonDone.setBackgroundColor(Color.parseColor(preferences.getCircleColor()));
            buttonDone.setVisibility(View.VISIBLE);
            status = "success";
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        txtTouch.setText(getResources().getString(R.string.touchdet) + " : " + event.getPointerCount());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                paint.setColor(getCircleColor(count));
                paint.setStyle(Paint.Style.STROKE);
                count++;
                canvas.drawCircle(event.getX(), event.getY(), 20, paint);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                txtTouch.setText(getResources().getString(R.string.touchdet) + " : 0");
                break;
        }
        return true;
    }

    public int getCircleColor(int x) {
        int color = Color.WHITE;
        switch (x % 5) {
            case 0:
                color = Color.YELLOW;
                break;
            case 1:
                color = Color.MAGENTA;
                break;
            case 2:
                color = Color.GREEN;
                break;
            case 3:
                color = Color.RED;
                break;
            case 4:
                color = Color.BLUE;
                break;
        }
        return color;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.buttonNext) {
            imgSuccess.setEnabled(true);
            imgCancel.setEnabled(true);
            buttonNext.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
            Intent newIntent = new Intent(TestActivity.this, DisplayTestActivity.class);
            ActivityOptions options =
                    ActivityOptions.makeCustomAnimation(TestActivity.this, android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(newIntent, options.toBundle());
        } else if (id == R.id.imgCancel) {
            Intent intent = new Intent();
            intent.putExtra("tag", tag);
            intent.putExtra("pos", position);
            intent.putExtra("status", "cancel");
            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.imgSuccess) {
            Intent i = new Intent();
            i.putExtra("tag", tag);
            i.putExtra("pos", position);
            i.putExtra("status", "success");
            setResult(RESULT_OK, i);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.buttonDone) {
            Intent intentDone = new Intent();
            intentDone.putExtra("tag", tag);
            intentDone.putExtra("pos", position);
            intentDone.putExtra("status", status);
            setResult(RESULT_OK, intentDone);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("tag", tag);
        intent.putExtra("pos", position);
        intent.putExtra("status", "back");
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tag.contains("flashlight"))
            flashOff();
        if (isSpeaker) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            audioManager.setMode(AudioManager.MODE_NORMAL);
            mediaPlayer = null;
            audioManager = null;
        }
        if (isVibrating)
            handler.removeCallbacks(runnable);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 9) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                flash();
            }
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY && isProximity) {
            if (event.values[0] >= event.sensor.getMaximumRange()) {
                imgTest.setImageDrawable(ContextCompat.getDrawable(TestActivity.this, R.drawable.ic_ear_blue));
            } else {
                imgTest.setImageDrawable(ContextCompat.getDrawable(TestActivity.this, R.drawable.ic_ear_green));
            }
        }
        if (event.sensor.getType() == Sensor.TYPE_LIGHT && isLight) {
            txtInfo.setText(String.format("%.1f", event.values[0]) + " lx");
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && isAcc) {
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];
            long currTime = System.currentTimeMillis();
            if (currTime - lastUpdate > 100) {
                diffTime = currTime - lastUpdate;
                lastUpdate = currTime;
                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;
                if (speed > 800)
                    vibrateIt(500);
                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (proximity != null)
            sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
        if (lightSensor != null)
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (accelerometer != null)
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (proximity != null)
            sensorManager.unregisterListener(this, proximity);
        if (lightSensor != null)
            sensorManager.unregisterListener(this, lightSensor);
        if (accelerometer != null)
            sensorManager.unregisterListener(this, accelerometer);
    }

    public void flash() {
        camera = Camera.open();
        Camera.Parameters parameters = camera.getParameters();
        parameters.getSupportedFlashModes();
        List<String> modes = parameters.getSupportedFlashModes();
        if (modes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        } else if (modes.contains(Camera.Parameters.FLASH_MODE_ON)) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        } else {
            System.out.println("No Flash Available");
        }
        camera.setParameters(parameters);
    }

    public void flashOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                cameraManager.setTorchMode(String.valueOf(cameraId), false);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            camera = Camera.open();
            Camera.Parameters parameters = camera.getParameters();
            parameters.getSupportedFlashModes();
            List<String> modes = parameters.getSupportedFlashModes();
            if (modes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            camera.setParameters(parameters);
        }
    }

    public void vibrateIt(int DURATION) {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(DURATION, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(DURATION);
        }
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            vibrateIt(600);
            vibrateRepeat();
        }
    };

    public void vibrateRepeat() {
        handler.postDelayed(runnable, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                imgTest.setImageDrawable(ContextCompat.getDrawable(TestActivity.this, R.drawable.ic_bluetooth_passed));
                txtQues.setText(getResources().getString(R.string.test_passed));
                status = "success";
            } else {
                status = "cancel";
                imgTest.setImageDrawable(ContextCompat.getDrawable(TestActivity.this, R.drawable.ic_bluetooth_failed));
                txtQues.setText(getResources().getString(R.string.test_failed));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                buttonDone.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(preferences.getCircleColor())));
            } else
                buttonDone.setBackgroundColor(Color.parseColor(preferences.getCircleColor()));
            buttonDone.setVisibility(View.VISIBLE);
        }
    }
}