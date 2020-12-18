package com.toralabs.deviceinfo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.models.CameraModel;

import java.util.List;

public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.Viewholder> {
    private final Context context;
    int color;
    List<CameraModel> list;
    CameraClickListener clickListener;

    public CameraAdapter(Context context, int color, List<CameraModel> list, CameraClickListener clickListener) {
        this.context = context;
        this.color = color;
        this.list = list;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_camera_layout, parent, false);
        return new Viewholder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            if (payloads.get(0) instanceof Integer) {
                if ((Integer) payloads.get(0) == 1 && list.get(position).isSelected()) {
                    holder.checkBox.setVisibility(View.VISIBLE);
                } else if ((Integer) payloads.get(0) == 2) {
                    holder.checkBox.setVisibility(View.GONE);
                }
            }
        } else
            super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        holder.cardView.setCardBackgroundColor(color);
        holder.txtFocal.setText(list.get(position).getTxtFocal());
        holder.title.setText(list.get(position).getTitle());
        holder.txtPixel.setText(list.get(position).getTxtPixel());
        holder.checkBox.setEnabled(false);
        holder.checkBox.setVisibility(View.INVISIBLE);
        if (list.get(position).isSelected()) {
            holder.checkBox.setEnabled(true);
            holder.checkBox.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        TextView title, txtPixel, txtFocal;
        CheckBox checkBox;
        RelativeLayout rel_cam;
        CameraClickListener cameraClickListener;

        public Viewholder(@NonNull View itemView, CameraClickListener clickListener) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            this.cameraClickListener = clickListener;
            title = itemView.findViewById(R.id.title);
            txtPixel = itemView.findViewById(R.id.txtPixel);
            txtFocal = itemView.findViewById(R.id.txtFocal);
            checkBox = itemView.findViewById(R.id.checkbox);
            rel_cam = itemView.findViewById(R.id.rel_cam);
            rel_cam.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            cameraClickListener.onClickItem(getAdapterPosition(), checkBox);
        }
    }

    public interface CameraClickListener {
        void onClickItem(int position, View v);
    }
}
