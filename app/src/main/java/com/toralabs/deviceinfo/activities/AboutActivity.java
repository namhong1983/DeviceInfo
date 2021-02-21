package com.toralabs.deviceinfo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.toralabs.deviceinfo.BuildConfig;
import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.impClasses.NativeAdInflate;
import com.toralabs.deviceinfo.menuItems.CustomSnackBar;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.menuItems.RemoveAds;
import com.toralabs.deviceinfo.menuItems.ThemeConstant;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener, NativeAdListener {
    NativeAdLayout nativeAdLayout;
    NativeAd nativeAd;
    Button btnRemoveAds, btnTranslate;
    ImageView imgInsta, imgLinked, imgMail, imgTelegram;
    TextView txtVersion, txtPackage;
    Preferences preferences;
    RemoveAds removeAds;
    ThemeConstant themeConstant;
    CardView cardAd;
    boolean bool;
    int themeNo, color;
    ScrollView scrollView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new Preferences(AboutActivity.this);
        color = Color.parseColor(preferences.getCircleColor());
        themeNo = preferences.getThemeNo();
        themeConstant = new ThemeConstant(themeNo);
        if (themeNo == 0) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(themeConstant.themeChooser());
        }
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.button_border);
        Drawable wrappedDrawable = null;
        if (unwrappedDrawable != null) {
            wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        }
        if (themeNo != 0) {
            if (wrappedDrawable != null) {
                DrawableCompat.setTint(wrappedDrawable, color);
            }
        } else {
            if (wrappedDrawable != null)
                DrawableCompat.setTint(wrappedDrawable, Color.parseColor("#2F4FE3"));
        }
        bool = preferences.getPurchasePref();
        setContentView(R.layout.activity_about);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.about));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        cardAd = findViewById(R.id.cardAd);
        scrollView = findViewById(R.id.scrollView);
        nativeAdLayout = findViewById(R.id.nativeBannerAd);
        txtVersion = findViewById(R.id.txtVersion);
        txtPackage = findViewById(R.id.txtPackage);
        btnRemoveAds = findViewById(R.id.btnRemoveAds);
        btnTranslate = findViewById(R.id.btnTranslate);
        imgInsta = findViewById(R.id.imgInsta);
        imgLinked = findViewById(R.id.imgLinked);
        imgMail = findViewById(R.id.imgMail);
        imgTelegram = findViewById(R.id.imgTelegram);
        btnRemoveAds.setTextColor(color);
        btnTranslate.setTextColor(color);
        btnRemoveAds.setBackgroundTintList(ColorStateList.valueOf(color));
        btnTranslate.setBackgroundTintList(ColorStateList.valueOf(color));
        btnRemoveAds.setOnClickListener(this);
        btnTranslate.setOnClickListener(this);
        imgMail.setOnClickListener(this);
        imgLinked.setOnClickListener(this);
        imgInsta.setOnClickListener(this);
        imgTelegram.setOnClickListener(this);
        txtVersion.setText(BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");
        txtPackage.setText(getPackageName());
        if (!bool) {
            nativeAd = new NativeAd(AboutActivity.this, getResources().getString(R.string.nativerectangle1));
            nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(this).build());
        }
    }

    @SuppressLint("IntentReset")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imgInsta) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/mrudul.tora/")));
        } else if (id == R.id.imgLinked) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/mrudul-tora-571004166/")));
        } else if (id == R.id.imgTelegram) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Device_Info")));
        } else if (id == R.id.imgMail) {
            Intent contact_intent = new Intent(Intent.ACTION_SENDTO);
            contact_intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"toralabs24@gmail.com"});
            contact_intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for DeviceInfo App");
            contact_intent.setType("message/rfc822");
            contact_intent.setData(Uri.parse("mailto:"));
            startActivity(contact_intent);
        } else if (id == R.id.btnTranslate) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://crowdin.com/translate/deviceinfo/")));
        } else if (id == R.id.btnRemoveAds) {
            if (bool) {
                CustomSnackBar customSnackBar = new CustomSnackBar(AboutActivity.this, scrollView);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.premium_user), Toast.LENGTH_SHORT).show();
                customSnackBar.showSnackBar(getResources().getString(R.string.premium_user));
            } else {
                removeAds = new RemoveAds(AboutActivity.this, scrollView);
                removeAds.setupbillingclient();
            }
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
            NativeAdInflate nativeAdInflate = new NativeAdInflate(AboutActivity.this, color);
            nativeAdInflate.inflateAd(nativeAd, nativeAdLayout);
            cardAd.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAdClicked(Ad ad) {
    }

    @Override
    public void onLoggingImpression(Ad ad) {
    }
}