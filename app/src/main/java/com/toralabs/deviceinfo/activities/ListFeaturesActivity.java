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
package com.toralabs.deviceinfo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.impClasses.NativeAdInflate;
import com.toralabs.deviceinfo.menuItems.ChangeLocale;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.menuItems.ThemeConstant;

/**
 * Created by @mrudultora
 */

public class ListFeaturesActivity extends AppCompatActivity implements NativeAdListener {
    TextView txtFeatures;
    NativeAdLayout nativeAdLayout;
    NativeAd nativeAd;
    Preferences preferences;
    ThemeConstant themeConstant;
    boolean bool;
    int themeNo, color;
    String name = "";
    ChangeLocale changeLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new Preferences(ListFeaturesActivity.this);
        changeLocale=new ChangeLocale(ListFeaturesActivity.this);
        changeLocale.setLocale(preferences.getLocalePref());
        color = Color.parseColor(preferences.getCircleColor());
        themeNo = preferences.getThemeNo();
        themeConstant = new ThemeConstant(themeNo);
        if (themeNo == 0) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(themeConstant.themeChooser());
        }
        bool = preferences.getPurchasePref();
        setContentView(R.layout.activity_list_features);
        if (getIntent() != null && getIntent().getStringExtra("name") != null)
            name = getIntent().getStringExtra("name");
        if (getSupportActionBar() != null && getIntent() != null) {
            if (name.equals("feature")) {
                getSupportActionBar().setTitle(getResources().getString(R.string.devicefeat));
            } else if (name.equals("ext")) {
                getSupportActionBar().setTitle(getResources().getString(R.string.gpuext));
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        txtFeatures = findViewById(R.id.txtFeatures);
        nativeAdLayout = findViewById(R.id.nativeBannerAd);
        if (getIntent() != null && name.equals("feature")) {
            if (getIntent().getStringArrayListExtra("featuresList") != null)
                txtFeatures.setText(getIntent().getStringArrayListExtra("featuresList").toString().replace("[", "").replace("]", "").replace(", ", "\n").trim());
        }
        if (getIntent() != null && name.equals("ext")) {
            txtFeatures.setText(preferences.getGlExt().replace("[", "").replace("]", "").replace(", ", "\n").trim());
        }
        if (!bool) {
            nativeAd = new NativeAd(ListFeaturesActivity.this, getResources().getString(R.string.nativerectangle1));
            nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(this).build());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onMediaDownloaded(Ad ad) {
    }

    @Override
    public void onError(Ad ad, AdError adError) {
    }

    @Override
    public void onAdLoaded(Ad ad) {
        if (nativeAd == null || nativeAd != ad) {
        } else {
            NativeAdInflate nativeAdInflate = new NativeAdInflate(ListFeaturesActivity.this, color);
            nativeAdInflate.inflateAd(nativeAd, nativeAdLayout);
            nativeAdLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAdClicked(Ad ad) {
    }

    @Override
    public void onLoggingImpression(Ad ad) {
    }
}