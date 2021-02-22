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
package com.toralabs.deviceinfo.menuItems;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.toralabs.deviceinfo.R;

import java.util.ArrayList;
import java.util.List;

public class RemoveAds implements PurchasesUpdatedListener {
    private final Context context;
    BillingClient billingClient;
    Preferences preferences;
    View view;
    CustomSnackBar customSnackBar;
    List<String> skuList = new ArrayList<>();
    private final String sku = "remove_ads";

    public RemoveAds(Context context, View view) {
        this.context = context;
        preferences = new Preferences(context);
        skuList.add(sku);
        this.view = view;
        customSnackBar = new CustomSnackBar(context, view);
    }

    public void setupbillingclient() {
        billingClient = BillingClient.newBuilder(context).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    loadAllSku();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });
    }

    public void loadAllSku() {
        if (billingClient.isReady()) {
            final SkuDetailsParams params = SkuDetailsParams.newBuilder()
                    .setSkusList(skuList)
                    .setType(BillingClient.SkuType.INAPP).build();

            billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        if (list != null) {
                            for (int i = 0; i < list.size(); i++) {
                                final SkuDetails skuDetails =list.get(i);
                                if (skuDetails.getSku().equals(sku)) {
                                    boolean isOwned = false;
                                    Purchase.PurchasesResult result = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
                                    List<Purchase> purchases = result.getPurchasesList();
                                    if (purchases != null) {
                                        for (int j = 0; j < purchases.size(); j++) {
                                            if (purchases.get(j).getSku().equals(sku)) {
                                                isOwned = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (!isOwned) {
                                        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                                .setSkuDetails(skuDetails)
                                                .build();
                                        billingClient.launchBillingFlow((Activity) context, billingFlowParams);
                                    } else {
                                        preferences.setPurchasePref(true);
                                        customSnackBar.showSnackBar(context.getResources().getString(R.string.premium_user));
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

        int responseCode = billingResult.getResponseCode();
        if (responseCode == BillingClient.BillingResponseCode.OK && list != null) {
            handlePurchase(list);
        } else if (responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            customSnackBar.showSnackBar(context.getResources().getString(R.string.premium_user));
            preferences.setPurchasePref(true);
        }
    }

    public void handlePurchase(List<Purchase> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getSku().equals(sku)) {
                if (list.get(i).getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    preferences.setPurchasePref(true);
                    if (!list.get(i).isAcknowledged()) {
                        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams
                                .newBuilder()
                                .setPurchaseToken(list.get(i).getPurchaseToken())
                                .build();
                        billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                            @Override
                            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                    System.out.println("Ok");
                                }
                            }
                        });
                    }
                    customSnackBar.showSnackBar(context.getResources().getString(R.string.purchase_done));
                }
            }
        }
    }
}
