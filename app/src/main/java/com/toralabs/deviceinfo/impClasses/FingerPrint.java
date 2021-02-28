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

/**
 * Created by @mrudultora
 */

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
