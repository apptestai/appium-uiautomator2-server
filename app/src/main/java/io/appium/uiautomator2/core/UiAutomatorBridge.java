/*
 * Copyright (C) 2012 The Android Open Source Project
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
package io.appium.uiautomator2.core;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.UiAutomation;
import android.view.Display;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.test.uiautomator.Configurator;
import androidx.test.uiautomator.UiDevice;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.model.AppiumUIA2Driver;
import io.appium.uiautomator2.utils.Device;
import io.appium.uiautomator2.utils.Logger;

import static io.appium.uiautomator2.model.settings.Settings.ALLOW_INVISIBLE_ELEMENTS;
import static io.appium.uiautomator2.model.settings.Settings.DONT_SUPPRESS_ACCESSIBILITY_SERVICES;
import static io.appium.uiautomator2.utils.ReflectionUtils.invoke;
import static io.appium.uiautomator2.utils.ReflectionUtils.method;

public class UiAutomatorBridge {
    private static UiAutomatorBridge INSTANCE = null;

    private UiAutomatorBridge() { }

    public static synchronized UiAutomatorBridge getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UiAutomatorBridge();

            /////////////////////////////////// ADDED BY MO: setting global WaitForIdleTimeout ///////////////////////////////////////////////////
            try {
                INSTANCE.configureAccessibilityService();
            } catch (Exception e) {
                Logger.info("ERROR", e);
            }
            //END
        }
        return INSTANCE;
    }

    public InteractionController getInteractionController() throws UiAutomator2Exception {
        return new InteractionController(invoke(method(UiDevice.class, "getInteractionController"),
                Device.getUiDevice()));
    }

    public AccessibilityNodeInfo getAccessibilityRootNode() throws UiAutomator2Exception {
        Object queryController = invoke(method(UiDevice.class, "getQueryController"), Device.getUiDevice());
        return (AccessibilityNodeInfo) invoke(method(queryController.getClass(), "getRootNode"), queryController);
    }

    public UiAutomation getUiAutomation() {
        return (UiAutomation) invoke(method(UiDevice.class, "getUiAutomation"), Device.getUiDevice());
    }

    public Display getDefaultDisplay() throws UiAutomator2Exception {
        return (Display) invoke(method(UiDevice.class, "getDefaultDisplay"), Device.getUiDevice());
    }

    /////////////////////////////////// ADDED BY MO: setting global WaitForIdleTimeout ///////////////////////////////////////////////////
    private void configureAccessibilityService() {
        // setting global WaitForIdleTimeout
        Configurator configurator = Configurator.getInstance();
        configurator.setWaitForIdleTimeout(Device.TOTAL_TIME_TO_WAIT_FOR_IDLE_STATE);

        boolean dontSupressAccessibilityServices = AppiumUIA2Driver
                .getInstance()
                .getSessionOrThrow()
                .getCapability(DONT_SUPPRESS_ACCESSIBILITY_SERVICES.toString(), false);

        if (dontSupressAccessibilityServices) {
            configurator.setUiAutomationFlags(UiAutomation.FLAG_DONT_SUPPRESS_ACCESSIBILITY_SERVICES);
        }

        UiAutomation uiAutomation = this.getUiAutomation();
        AccessibilityServiceInfo serviceInfo = uiAutomation.getServiceInfo();
        serviceInfo.flags = serviceInfo.flags | AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS | AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        uiAutomation.setServiceInfo(serviceInfo);
    }
    //END
}
