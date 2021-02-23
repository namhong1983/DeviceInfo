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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.toralabs.deviceinfo.R;

/**
 * Created by @mrudultora
 */

public class DisplayTestActivity extends AppCompatActivity {
    RelativeLayout relLayout, rel1, rel2, rel3, rel4, rel5, rel6, rel7, rel8, rel9, rel10, rel11, rel12, rel13, rel14, rel15, rel16;
    LinearLayout linear1, linear2, linear3, linear4, linear5, linear6, linear7, linear8;
    int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_test);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        findViewByIds();
        relLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (i) {
                    case 1:
                        relLayout.setBackgroundColor(ContextCompat.getColor(DisplayTestActivity.this, R.color.black));
                        i++;
                        break;
                    case 2:
                        relLayout.setBackgroundColor(ContextCompat.getColor(DisplayTestActivity.this, R.color.red));
                        i++;
                        break;
                    case 3:
                        relLayout.setBackgroundColor(ContextCompat.getColor(DisplayTestActivity.this, R.color.green));
                        i++;
                        break;
                    case 4:
                        relLayout.setBackgroundColor(ContextCompat.getColor(DisplayTestActivity.this, R.color.blue));
                        i++;
                        break;
                    case 5:
                        relLayout.setBackgroundColor(Color.DKGRAY);
                        i++;
                        break;
                    case 6:
                        rel1.setBackgroundColor(Color.WHITE);
                        rel2.setBackgroundColor(Color.BLACK);
                        rel3.setBackgroundColor(Color.YELLOW);
                        rel4.setBackgroundColor(Color.BLUE);
                        rel5.setBackgroundColor(Color.CYAN);
                        rel6.setBackgroundColor(Color.RED);
                        rel7.setBackgroundColor(Color.GREEN);
                        rel8.setBackgroundColor(Color.MAGENTA);
                        rel9.setBackgroundColor(Color.MAGENTA);
                        rel10.setBackgroundColor(Color.GREEN);
                        rel11.setBackgroundColor(Color.RED);
                        rel12.setBackgroundColor(Color.CYAN);
                        rel13.setBackgroundColor(Color.BLUE);
                        rel14.setBackgroundColor(Color.YELLOW);
                        rel15.setBackgroundColor(Color.BLACK);
                        rel16.setBackgroundColor(Color.WHITE);
                        i++;
                        break;
                    case 7:
                        setNullBg();
                        linear1.setBackgroundColor(Color.WHITE);
                        linear2.setBackgroundColor(Color.YELLOW);
                        linear3.setBackgroundColor(Color.CYAN);
                        linear4.setBackgroundColor(Color.GREEN);
                        linear5.setBackgroundColor(Color.MAGENTA);
                        linear6.setBackgroundColor(Color.RED);
                        linear7.setBackgroundColor(Color.BLUE);
                        linear8.setBackgroundColor(Color.BLACK);
                        i++;
                        break;
                    case 8:
                        rel1.setBackgroundColor(Color.parseColor("#a1a19f"));
                        rel2.setBackgroundColor(Color.parseColor("#242423"));
                        rel3.setBackgroundColor(Color.parseColor("#8f8f8d"));
                        rel4.setBackgroundColor(Color.parseColor("#363635"));
                        rel5.setBackgroundColor(Color.parseColor("#6c6c6a"));
                        rel6.setBackgroundColor(Color.parseColor("#484846"));
                        rel7.setBackgroundColor(Color.parseColor("#7e7e7b"));
                        rel8.setBackgroundColor(Color.parseColor("#5a5a58"));
                        rel9.setBackgroundColor(Color.parseColor("#5a5a58"));
                        rel10.setBackgroundColor(Color.parseColor("#7e7e7b"));
                        rel11.setBackgroundColor(Color.parseColor("#484846"));
                        rel12.setBackgroundColor(Color.parseColor("#6c6c6a"));
                        rel13.setBackgroundColor(Color.parseColor("#363635"));
                        rel14.setBackgroundColor(Color.parseColor("#8f8f8d"));
                        rel15.setBackgroundColor(Color.parseColor("#242423"));
                        rel16.setBackgroundColor(Color.parseColor("#a1a19f"));
                        i++;
                        break;
                    case 9:
                        setNullBg();
                        linear1.setBackgroundColor(Color.parseColor("#a1a19f"));
                        linear2.setBackgroundColor(Color.parseColor("#8f8f8d"));
                        linear3.setBackgroundColor(Color.parseColor("#7e7e7b"));
                        linear4.setBackgroundColor(Color.parseColor("#6c6c6a"));
                        linear5.setBackgroundColor(Color.parseColor("#5a5a58"));
                        linear6.setBackgroundColor(Color.parseColor("#484846"));
                        linear7.setBackgroundColor(Color.parseColor("#363635"));
                        linear8.setBackgroundColor(Color.parseColor("#242423"));
                        i++;
                        break;
                    default:
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void findViewByIds() {
        relLayout = findViewById(R.id.relLayout);
        rel1 = findViewById(R.id.rel1);
        rel2 = findViewById(R.id.rel2);
        rel3 = findViewById(R.id.rel3);
        rel4 = findViewById(R.id.rel4);
        rel5 = findViewById(R.id.rel5);
        rel6 = findViewById(R.id.rel6);
        rel7 = findViewById(R.id.rel7);
        rel8 = findViewById(R.id.rel8);
        rel9 = findViewById(R.id.rel9);
        rel10 = findViewById(R.id.rel10);
        rel11 = findViewById(R.id.rel11);
        rel12 = findViewById(R.id.rel12);
        rel13 = findViewById(R.id.rel13);
        rel14 = findViewById(R.id.rel14);
        rel15 = findViewById(R.id.rel15);
        rel16 = findViewById(R.id.rel16);
        linear1 = findViewById(R.id.linear1);
        linear2 = findViewById(R.id.linear2);
        linear3 = findViewById(R.id.linear3);
        linear4 = findViewById(R.id.linear4);
        linear5 = findViewById(R.id.linear5);
        linear6 = findViewById(R.id.linear6);
        linear7 = findViewById(R.id.linear7);
        linear8 = findViewById(R.id.linear8);
    }

    public void setNullBg() {
        rel1.setBackgroundColor(Color.TRANSPARENT);
        rel2.setBackgroundColor(Color.TRANSPARENT);
        rel3.setBackgroundColor(Color.TRANSPARENT);
        rel4.setBackgroundColor(Color.TRANSPARENT);
        rel5.setBackgroundColor(Color.TRANSPARENT);
        rel6.setBackgroundColor(Color.TRANSPARENT);
        rel7.setBackgroundColor(Color.TRANSPARENT);
        rel8.setBackgroundColor(Color.TRANSPARENT);
        rel9.setBackgroundColor(Color.TRANSPARENT);
        rel10.setBackgroundColor(Color.TRANSPARENT);
        rel11.setBackgroundColor(Color.TRANSPARENT);
        rel12.setBackgroundColor(Color.TRANSPARENT);
        rel13.setBackgroundColor(Color.TRANSPARENT);
        rel14.setBackgroundColor(Color.TRANSPARENT);
        rel15.setBackgroundColor(Color.TRANSPARENT);
        rel16.setBackgroundColor(Color.TRANSPARENT);

    }
}