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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.toralabs.deviceinfo.R;

/**
 * Created by @mrudultora
 */

public class CustomSnackBar {
    private final Context context;
    private final View view;

    public CustomSnackBar(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    @SuppressLint("inflateParams")
    public void showSnackBar(String string) {
        Snackbar snackbar = Snackbar.make(view, "", BaseTransientBottomBar.LENGTH_LONG);
        View custom = ((Activity) context).getLayoutInflater().inflate(R.layout.snackbar_layout, null);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        TextView tv_snack = custom.findViewById(R.id.tv_snack);
        tv_snack.setText(string);
        Button btn_open = custom.findViewById(R.id.btn_open);
        btn_open.setVisibility(View.GONE);
        snackbarLayout.addView(custom, 0);
        snackbar.show();
    }
}
