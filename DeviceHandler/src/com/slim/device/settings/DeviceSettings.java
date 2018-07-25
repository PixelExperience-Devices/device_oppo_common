/*
 * Copyright (C) 2016 The CyanogenMod Project
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

package com.slim.device.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import com.slim.device.SRGBModeSwitch;
import com.slim.device.DCIModeSwitch;
import android.preference.TwoStatePreference;
import com.slim.device.KernelControl;
import com.slim.device.R;
import com.slim.device.util.FileUtils;

public class DeviceSettings extends PreferenceActivity
        implements OnPreferenceChangeListener {

    public static final String KEY_SRGB_SWITCH = "srgb";
    public static final String KEY_DCI_SWITCH = "dci";
    private static final String KEY_CATEGORY_GRAPHICS = "graphics";
    public static final String SLIDER_SWAP_NODE = "/proc/s1302/key_rep";
    public static final String KEYCODE_SLIDER_TOP = "/proc/tri-state-key/keyCode_top";
    public static final String KEYCODE_SLIDER_MIDDLE = "/proc/tri-state-key/keyCode_middle";
    public static final String KEYCODE_SLIDER_BOTTOM = "/proc/tri-state-key/keyCode_bottom";

    private SwitchPreference mSliderSwap;
    private ListPreference mSliderTop;
    private ListPreference mSliderMiddle;
    private ListPreference mSliderBottom;
    private TwoStatePreference mSRGBModeSwitch;
    private TwoStatePreference mDCIModeSwitch;

@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main);

        mSliderSwap = (SwitchPreference) findPreference("button_swap");
        mSliderSwap.setOnPreferenceChangeListener(this);

        mSliderTop = (ListPreference) findPreference("keycode_top_position");
        mSliderTop.setOnPreferenceChangeListener(this);

        mSliderMiddle = (ListPreference) findPreference("keycode_middle_position");
        mSliderMiddle.setOnPreferenceChangeListener(this);

        mSliderBottom = (ListPreference) findPreference("keycode_bottom_position");
        mSliderBottom.setOnPreferenceChangeListener(this);

        mSRGBModeSwitch = (TwoStatePreference) findPreference(KEY_SRGB_SWITCH);
        mSRGBModeSwitch.setEnabled(SRGBModeSwitch.isSupported());
        mSRGBModeSwitch.setChecked(SRGBModeSwitch.isCurrentlyEnabled(this));
        mSRGBModeSwitch.setOnPreferenceChangeListener(new SRGBModeSwitch());

        mDCIModeSwitch = (TwoStatePreference) findPreference(KEY_DCI_SWITCH);
        mDCIModeSwitch.setEnabled(DCIModeSwitch.isSupported());
        mDCIModeSwitch.setChecked(DCIModeSwitch.isCurrentlyEnabled(this));
        mDCIModeSwitch.setOnPreferenceChangeListener(new DCIModeSwitch());


    }

    private void setSummary(ListPreference preference, String file) {
        String keyCode;
        if ((keyCode = FileUtils.readOneLine(file)) != null) {
            preference.setValue(keyCode);
            preference.setSummary(preference.getEntry());
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String file;
        if (preference == mSliderTop) {
            file = KEYCODE_SLIDER_TOP;
        } else if (preference == mSliderMiddle) {
            file = KEYCODE_SLIDER_MIDDLE;
        } else if (preference == mSliderBottom) {
            file = KEYCODE_SLIDER_BOTTOM;
        } else if (preference == mSliderSwap) {
            Boolean value = (Boolean) newValue;
            FileUtils.writeLine(KernelControl.SLIDER_SWAP_NODE, value ? "1" : "0");
            return true;
        } else {
            return false;
        }

        FileUtils.writeLine(file, (String) newValue);
        setSummary((ListPreference) preference, file);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Remove padding around the listview
            getListView().setPadding(0, 0, 0, 0);

        setSummary(mSliderTop, KEYCODE_SLIDER_TOP);
        setSummary(mSliderMiddle, KEYCODE_SLIDER_MIDDLE);
        setSummary(mSliderBottom, KEYCODE_SLIDER_BOTTOM);
    }
}
