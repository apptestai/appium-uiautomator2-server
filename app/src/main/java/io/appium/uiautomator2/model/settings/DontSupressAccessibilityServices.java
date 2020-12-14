/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.appium.uiautomator2.model.settings;

import android.app.UiAutomation;
import androidx.test.uiautomator.Configurator;

/**
 * ADDEDB BY MO: change default timeout(10sec -> 3sec)
 */
public class DontSupressAccessibilityServices extends AbstractSetting<Boolean> {

    private static final String SETTING_NAME = "dontSupressAccessibilityServices";

    public DontSupressAccessibilityServices() {
        super(Boolean.class, SETTING_NAME);
    }

    @Override
    public Boolean getValue() {
        return Configurator.getInstance().getUiAutomationFlags() == UiAutomation.FLAG_DONT_SUPPRESS_ACCESSIBILITY_SERVICES;
    }

    @Override
    protected void apply(Boolean dontSupressAccessibilityServices) {
        if (dontSupressAccessibilityServices) {
            Configurator.getInstance().setUiAutomationFlags(UiAutomation.FLAG_DONT_SUPPRESS_ACCESSIBILITY_SERVICES);

        }
    }

}
