package com.toralabs.deviceinfo.impClasses;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintManager.AuthenticationCallback;
import android.os.Build;
import android.os.CancellationSignal;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerPrint extends AuthenticationCallback {
    CancellationSignal cancellationSignal;
    final private Context context;
    int position;
    OnAuthenticationResult onAuthenticationResult;

    public FingerPrint(Context context, int position, OnAuthenticationResult onAuthenticationResult) {
        this.context = context;
        this.position = position;
        this.onAuthenticationResult = onAuthenticationResult;
    }

    public void startAuthentication(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        System.out.println("FingerprintManager onAuthenticationError");
        onAuthenticationResult.result(position,"error");
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        onAuthenticationResult.result(position, "fail");
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        onAuthenticationResult.result(position, "success");
    }

    public interface OnAuthenticationResult {
        void result(int position, String status);
    }
}
