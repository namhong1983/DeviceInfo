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
package com.toralabs.deviceinfo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.impClasses.NativeAdInflate;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.InputModel;

import java.util.List;

public class InputAdapter extends RecyclerView.Adapter<InputAdapter.ViewHolder> implements NativeAdListener {
    Preferences preferences;
    private final Context context;
    private final List<InputModel> inputModelList;
    NativeAd nativeAd;
    ViewHolder viewHolder;
    int color;
    int CONTENT = 0;
    int AD = 1;
    boolean bool;

    public InputAdapter(Context context, List<InputModel> inputModelList, int color) {
        this.context = context;
        this.inputModelList = inputModelList;
        this.color = color;
        preferences = new Preferences(context);
        bool = preferences.getPurchasePref();
        if (!bool)
            nativeAd = new NativeAd(context, context.getResources().getString(R.string.nativerectangle1));
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 1)
            return AD;
        return CONTENT;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == CONTENT) {
            v = LayoutInflater.from(context).inflate(R.layout.item_input_layout, parent, false);
        } else {
            v = LayoutInflater.from(context).inflate(R.layout.recyclerview_ad_layout, parent, false);
        }
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (getItemViewType(position) == AD) {
            holder.relMainInput.setVisibility(View.GONE);
            this.viewHolder = holder;
            if (!bool)
                nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(this).build());
        } else {
            if (inputModelList.get(position).isHasMotionRange()) {
                holder.relMotionRange.setVisibility(View.VISIBLE);
                holder.txtAxis.setText(context.getResources().getString(R.string.axis) + ": " + inputModelList.get(position).getAxis());
                holder.txtRange.setText(context.getResources().getString(R.string.range) + ": " + inputModelList.get(position).getRange());
                holder.txtResolution.setText(context.getResources().getString(R.string.resol) + ": " + inputModelList.get(position).getResol());
                holder.txtFlat.setText(context.getResources().getString(R.string.flat) + ": " + inputModelList.get(position).getFlat());
                holder.txtFuzz.setText(context.getResources().getString(R.string.fuzz) + ": " + inputModelList.get(position).getFuzz());
                holder.txtSource.setText(context.getResources().getString(R.string.source) + ": " + inputModelList.get(position).getSource());
            } else
                holder.relMotionRange.setVisibility(View.GONE);
            holder.txtVendorId.setText(context.getResources().getString(R.string.vendorid) + ": " + inputModelList.get(position).getVendorId());
            holder.txtHasVib.setText(context.getResources().getString(R.string.hasvibrator) + ": " + inputModelList.get(position).getHasVibrator());
            holder.txtProId.setText(context.getResources().getString(R.string.productid) + ": " + inputModelList.get(position).getProId());
            holder.txtName.setText(inputModelList.get(position).getName());
            holder.txtDescriptor.setText(inputModelList.get(position).getDesc());
            holder.txtKeyboardType.setText(context.getResources().getString(R.string.keyboardtype) + ": " + inputModelList.get(position).getKeyboardType());
            holder.txtDesc.setText(context.getResources().getString(R.string.descriptor) + ": ");
            holder.txtInputDevice.setText(context.getResources().getString(R.string.inputdevice) + ": " + inputModelList.get(position).getDeviceId());
            if (inputModelList.get(position).getSources() != null) {
                holder.txtSour.setText(context.getResources().getString(R.string.sources) + ": ");
                holder.txtSources.setText(inputModelList.get(position).getSources());
            } else {
                holder.txtSour.setVisibility(View.GONE);
                holder.txtSources.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return inputModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDesc, txtName, txtInputDevice, txtVendorId, txtProId, txtKeyboardType, txtDescriptor, txtHasVib, txtSour, txtSources, txtAxis, txtRange, txtFlat, txtFuzz, txtResolution, txtSource;
        RelativeLayout relMotionRange, relInput, relMainInput;
        NativeAdLayout nativeAdLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAxis = itemView.findViewById(R.id.txtAxis);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            txtDescriptor = itemView.findViewById(R.id.txtDescriptor);
            txtFlat = itemView.findViewById(R.id.txtFlat);
            txtFuzz = itemView.findViewById(R.id.txtFuzz);
            txtHasVib = itemView.findViewById(R.id.txtHasVib);
            txtInputDevice = itemView.findViewById(R.id.txtInputDevice);
            txtKeyboardType = itemView.findViewById(R.id.txtKeyboardType);
            txtName = itemView.findViewById(R.id.txtName);
            txtProId = itemView.findViewById(R.id.txtProId);
            txtRange = itemView.findViewById(R.id.txtRange);
            txtResolution = itemView.findViewById(R.id.txtResolution);
            txtSour = itemView.findViewById(R.id.txtSour);
            txtSource = itemView.findViewById(R.id.txtSource);
            txtSources = itemView.findViewById(R.id.txtSources);
            txtVendorId = itemView.findViewById(R.id.txtVendorId);
            relMotionRange = itemView.findViewById(R.id.relMotionRange);
            nativeAdLayout = itemView.findViewById(R.id.nativeBannerAd);
            relInput = itemView.findViewById(R.id.relInput);
            relMainInput = itemView.findViewById(R.id.relMainInput);
        }
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
            NativeAdInflate nativeAdInflate = new NativeAdInflate(context, color);
            nativeAdInflate.inflateAd(nativeAd, viewHolder.nativeAdLayout);
            viewHolder.relMainInput.setVisibility(View.VISIBLE);
            viewHolder.nativeAdLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAdClicked(Ad ad) {
    }

    @Override
    public void onLoggingImpression(Ad ad) {
    }
}
