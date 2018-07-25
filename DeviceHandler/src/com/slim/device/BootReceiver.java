/*
 * Copyright (C) 2014 SlimRoms Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.slim.device;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import com.slim.device.KernelControl;
import com.slim.device.settings.ScreenOffGesture;
import com.slim.device.settings.DeviceSettings;
import com.slim.device.util.FileUtils;
import com.slim.device.Utils;
import java.io.File;
import android.support.v7.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {


private void restore(String file, boolean enabled) {
        if (file == null) {
            return;
        }
if (enabled) {
            Utils.writeValue(file, "1");
        }
    }

private void restore(String file, String value) {
        if (file == null) {
            return;
        }
      Utils.writeValue(file, value);
  }


    @Override
       public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                enableComponent(context, ScreenOffGesture.class.getName());
                SharedPreferences screenOffGestureSharedPreferences = context.getSharedPreferences(
                        ScreenOffGesture.GESTURE_SETTINGS, Activity.MODE_PRIVATE);
                KernelControl.enableGestures(
                        screenOffGestureSharedPreferences.getBoolean(
                        ScreenOffGesture.PREF_GESTURE_ENABLE, true));
            }


                enableComponent(context, DeviceSettings.class.getName());
                boolean sliderSwap = getPreferenceBoolean(context, "button_swap", false);
                FileUtils.writeLine(KernelControl.SLIDER_SWAP_NODE, sliderSwap ? "1" : "0");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enabled = sharedPrefs.getBoolean(DeviceSettings.KEY_SRGB_SWITCH, false);
        restore(SRGBModeSwitch.getFile(), enabled);
        enabled = sharedPrefs.getBoolean(DeviceSettings.KEY_DCI_SWITCH, false);
        restore(DCIModeSwitch.getFile(), enabled);

    }

    private String getPreferenceString(Context context, String key, String defaultValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, defaultValue);
    }

    private boolean getPreferenceBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, defaultValue);
    }

    private void disableComponent(Context context, String component) {
        ComponentName name = new ComponentName(context, component);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(name,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void enableComponent(Context context, String component) {
        ComponentName name = new ComponentName(context, component);
        PackageManager pm = context.getPackageManager();
        if (pm.getComponentEnabledSetting(name)
                == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            pm.setComponentEnabledSetting(name,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}
