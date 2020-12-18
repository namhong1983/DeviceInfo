package com.toralabs.deviceinfo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.impClasses.NativeBannerAdInflate;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.TestModel;

import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> implements NativeAdListener {
    private final Context context;
    private ViewHolder viewHolder;
    int color;
    List<TestModel> list;
    ItemClickListener itemClickListener;
    NativeBannerAd nativeBannerAd;
    int CONTENT = 0;
    int AD = 1;
    Preferences preferences;
    boolean bool;

    public TestAdapter(Context context, List<TestModel> list, ItemClickListener itemClickListener, int color) {
        this.context = context;
        this.list = list;
        this.itemClickListener = itemClickListener;
        this.color = color;
        preferences = new Preferences(context);
        bool = preferences.getPurchasePref();
        if (!bool)
            nativeBannerAd = new NativeBannerAd(context, context.getResources().getString(R.string.nativead2));
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
            v = LayoutInflater.from(context).inflate(R.layout.item_test_layout, parent, false);
        } else {
            v = LayoutInflater.from(context).inflate(R.layout.recyclerview_ad_layout, parent, false);
        }
        return new ViewHolder(v, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.size() != 0) {
            if (payloads.get(0) instanceof Integer) {
                switch ((Integer) payloads.get(0)) {
                    case 0:
                        holder.imgStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_test_incomplete));
                        break;
                    case 1:
                        holder.imgStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_test_check));
                        break;
                    case 2:
                        holder.imgStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_test_cancel));
                        break;
                }
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (getItemViewType(position) == AD) {
            this.viewHolder = holder;
            holder.cardView.setVisibility(View.GONE);
            if (!bool)
                nativeBannerAd.loadAd(nativeBannerAd.buildLoadAdConfig().withAdListener(this).build());
        } else {
            holder.imgStatus.setImageDrawable(ContextCompat.getDrawable(context, list.get(position).getImgStatus()));
            holder.testName.setText(list.get(position).getTestName());
            holder.imgMain.setImageDrawable(ContextCompat.getDrawable(context, list.get(position).getImgMain()));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgMain, imgStatus;
        TextView testName;
        ItemClickListener listener;
        CardView cardView;
        RelativeLayout relTest;
        NativeAdLayout nativeAdLayout;

        public ViewHolder(@NonNull View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            imgMain = itemView.findViewById(R.id.imgMain);
            testName = itemView.findViewById(R.id.testName);
            imgStatus = itemView.findViewById(R.id.imgStatus);
            cardView = itemView.findViewById(R.id.cardview);
            relTest = itemView.findViewById(R.id.relTest);
            nativeAdLayout = itemView.findViewById(R.id.nativeBannerAd);
            this.listener = itemClickListener;
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition(), list.get(getAdapterPosition()).getTag(), list.get(getAdapterPosition()).getTestName());
        }
    }

    public interface ItemClickListener {
        void onItemClick(int position, String tag, String name);
    }

    @Override
    public void onMediaDownloaded(Ad ad) {
    }

    @Override
    public void onError(Ad ad, AdError adError) {
    }

    @Override
    public void onAdLoaded(Ad ad) {
        if (nativeBannerAd == null || nativeBannerAd != ad) {
        } else {
            NativeBannerAdInflate nativeBannerAdInflate = new NativeBannerAdInflate(context, color);
            nativeBannerAdInflate.inflateAd(nativeBannerAd, viewHolder.nativeAdLayout);
            viewHolder.cardView.setVisibility(View.VISIBLE);
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
