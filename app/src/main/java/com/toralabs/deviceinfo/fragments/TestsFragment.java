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
package com.toralabs.deviceinfo.fragments;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.activities.MainActivity;
import com.toralabs.deviceinfo.adapter.TestAdapter;
import com.toralabs.deviceinfo.activities.TestActivity;
import com.toralabs.deviceinfo.impClasses.FingerPrint;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.TestModel;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static android.app.Activity.RESULT_OK;

/**
 * Created by @mrudultora
 */

public class TestsFragment extends Fragment implements TestAdapter.ItemClickListener, FingerPrint.OnAuthenticationResult {
    Preferences preferences;
    RecyclerView recycler_tests;
    TestAdapter testAdapter;
    Context context;
    BiometricPrompt biometricPrompt;
    FingerprintManager fingerprintManager;
    ArrayList<TestModel> testList = new ArrayList<>();
    Executor executor;
    BiometricPrompt.PromptInfo promptInfo;
    String tag, status;
    int pos, d, color;
    MainActivity mainActivity;
    FingerprintManager.CryptoObject cryptoObject;
    Cipher cipher;
    AlertDialog dialog;
    ImageView fingerprintIcon;
    TextView fingerprintError;
    Handler handler;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        if (context != null)
            preferences = new Preferences(context);
        color = Color.parseColor(preferences.getCircleColor());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        }
        if (savedInstanceState != null)
            testList = savedInstanceState.getParcelableArrayList("testList");
        else
            testList = mainActivity.testList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_tests, container, false);
        setHasOptionsMenu(true);
        recycler_tests = view.findViewById(R.id.recycler_tests);
        testAdapter = new TestAdapter(context, testList, this, color);
        recycler_tests.setLayoutManager(new LinearLayoutManager(context));
        recycler_tests.setAdapter(testAdapter);
        recycler_tests.getRecycledViewPool().setMaxRecycledViews(1, 2);
        handler = new Handler();
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem item = menu.findItem(R.id.search);
        item.setVisible(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100) {
            if (data != null) {
                tag = data.getStringExtra("tag");
                status = data.getStringExtra("status");
                pos = data.getIntExtra("pos", 0);
            }
            if (resultCode == RESULT_OK && status.equalsIgnoreCase("success")) {
                d = R.drawable.ic_test_check;
                testList.get(pos).setImgStatus(d);
                testAdapter.notifyItemChanged(pos, 1);
                preferences.setValue(tag, 1);
            } else if (resultCode == RESULT_OK && status.equalsIgnoreCase("cancel")) {
                d = R.drawable.ic_test_cancel;
                testList.get(pos).setImgStatus(d);
                preferences.setValue(tag, 2);
                testAdapter.notifyItemChanged(pos, 2);
            } else if (resultCode == RESULT_OK && status.equalsIgnoreCase("back")) {
                d = R.drawable.ic_test_incomplete;
                testList.get(pos).setImgStatus(d);
                preferences.setValue(tag, 0);
                testAdapter.notifyItemChanged(pos, 0);
            }
        }
    }

    public void biometric(final int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            executor = context.getMainExecutor();
        }
        if (getActivity() != null)
            biometricPrompt = new BiometricPrompt(getActivity(), executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode,
                                                  @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                }

                @Override
                public void onAuthenticationSucceeded(
                        @NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    testList.get(position).setImgStatus(R.drawable.ic_test_check);
                    preferences.setValue("fingerprint", 1);
                    testAdapter.notifyItemChanged(position);
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    testList.get(position).setImgStatus(R.drawable.ic_test_cancel);
                    preferences.setValue("fingerprint", 2);
                    testAdapter.notifyItemChanged(position);
                }
            });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void generateKeyPrint() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(new KeyGenParameterSpec.Builder("Tora", KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();

        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    public boolean createCryptoObject() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
                SecretKey secretKey = (SecretKey) keyStore.getKey("Tora", null);
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                return true;
            } else
                return false;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | KeyStoreException | CertificateException | IOException | UnrecoverableKeyException | InvalidKeyException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onItemClick(int position, String tag, String name) {
        if (tag!=null && !tag.contains("fingerprint") && position != 1) {
            Intent intent = new Intent(getActivity(), TestActivity.class);
            intent.putExtra("tag", tag);
            intent.putExtra("name", name);
            intent.putExtra("position", position);
            ActivityOptions options =
                    ActivityOptions.makeCustomAnimation(getActivity(), android.R.anim.fade_in, android.R.anim.fade_out);
            startActivityForResult(intent, 100, options.toBundle());
        } else if (tag!=null && tag.contains("fingerprint")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (fingerprintManager.isHardwareDetected()) {
                    biometric(position);
                    promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle(context.getResources().getString(R.string.fingertest))
                            .setSubtitle(context.getResources().getString(R.string.fingersubtitle)).setDescription(context.getResources().getString(R.string.fingerdesc))
                            .setNegativeButtonText(context.getResources().getString(R.string.cancel)).build();
                    biometricPrompt.authenticate(promptInfo);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints()) {
                    showFingerprintDialog(position);
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("testList", testList);
    }

    @SuppressLint("InflateParams")
    public void showFingerprintDialog(int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            generateKeyPrint();
            if (createCryptoObject()) {
                cryptoObject = new FingerprintManager.CryptoObject(cipher);
            }
            FingerPrint fingerPrint = new FingerPrint(getContext(), position, this);
            fingerPrint.startAuthentication(fingerprintManager, cryptoObject);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        dialog = builder.create();
        View v = getLayoutInflater().inflate(R.layout.fingerprint_dialog_layout, null);
        dialog.setView(v);
        dialog.setCanceledOnTouchOutside(false);
        fingerprintIcon = v.findViewById(R.id.fingerprint_icon);
        fingerprintError = v.findViewById(R.id.fingerprint_error);
        dialog.setTitle(getResources().getString(R.string.fingertest));
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color);
    }

    @Override
    public void result(int position, String status) {
        if (status.equals("success")) {
            dialog.dismiss();
            testList.get(position).setImgStatus(R.drawable.ic_test_check);
            testAdapter.notifyItemChanged(position);
        } else if (status.equals("fail")) {
            if (getContext() != null)
                fingerprintIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.fingerprint_dialog_error_to_fp));
            AnimatedVectorDrawable animationDrawable = (AnimatedVectorDrawable) fingerprintIcon.getDrawable();
            animationDrawable.start();
            fingerprintError.setText(getResources().getString(R.string.notrecog));
            fingerprintError.setTextColor(Color.RED);
            preferences.setValue("fingerprint", 2);
            testList.get(position).setImgStatus(R.drawable.ic_test_cancel);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (fingerprintIcon != null && fingerprintError != null) {
                        fingerprintError.setText(getResources().getString(R.string.fingerprint_dialog_touch_sensor));
                        if (getContext() != null)
                            fingerprintError.setTextColor(ContextCompat.getColor(getContext(), R.color.textgrey));
                    }
                }
            }, 1000);
            testAdapter.notifyItemChanged(position);
        } else if (status.equals("error")) {
            testList.get(position).setImgStatus(R.drawable.ic_test_incomplete);
            preferences.setValue("fingerprint", 0);
            testAdapter.notifyItemChanged(position);
        }
    }
}
