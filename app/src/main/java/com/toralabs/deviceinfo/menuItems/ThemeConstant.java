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

import com.toralabs.deviceinfo.R;

/**
 * Created by @mrudultora
 */

public class ThemeConstant {
    private int constant;

    public ThemeConstant(int constant) {
        this.constant = constant;
    }

    public int themeChooser() {
        switch (constant) {
            case 1:
                constant = R.style.Theme1;
                break;
            case 2:
                constant = R.style.Theme2;
                break;
            case 3:
                constant = R.style.Theme3;
                break;
            case 4:
                constant = R.style.Theme4;
                break;
            case 5:
                constant = R.style.Theme5;
                break;
            case 6:
                constant = R.style.Theme6;
                break;
            case 7:
                constant = R.style.Theme7;
                break;
            case 8:
                constant = R.style.Theme8;
                break;
            case 9:
                constant = R.style.Theme9;
                break;
            case 10:
                constant = R.style.Theme10;
                break;
            case 11:
                constant = R.style.Theme11;
                break;
            case 12:
                constant = R.style.Theme12;
                break;
            case 13:
                constant = R.style.Theme13;
                break;
            case 14:
                constant = R.style.Theme14;
                break;
            case 15:
                constant = R.style.Theme15;
                break;

        }
        return constant;
    }
}
