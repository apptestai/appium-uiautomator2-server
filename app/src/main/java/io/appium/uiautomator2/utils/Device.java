package io.appium.uiautomator2.utils;

import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.view.Display;

import androidx.annotation.Nullable;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;

import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.core.UiAutomatorBridge;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.By;
import io.appium.uiautomator2.model.UiObject2Element;
import io.appium.uiautomator2.model.UiObjectElement;
import io.appium.uiautomator2.model.internal.CustomUiDevice;
import io.appium.uiautomator2.model.settings.Settings;
import io.appium.uiautomator2.model.settings.UseDeviceRealSize;
import io.appium.uiautomator2.model.settings.WaitForIdleTimeout;

public abstract class Device {

    public static UiDevice getUiDevice() {
        return UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    public static AndroidElement getAndroidElement(String id, Object element, boolean isSingleMatch,
                                                   @Nullable By by, @Nullable String contextId)
            throws UiAutomator2Exception {
        if (element instanceof UiObject2) {
            return new UiObject2Element(id, (UiObject2) element, isSingleMatch, by, contextId);
        } else if (element instanceof UiObject) {
            return new UiObjectElement(id, (UiObject) element, isSingleMatch, by, contextId);
        } else {
            throw new UiAutomator2Exception("Unknown Element type: " + element.getClass().getName());
        }
    }

    public static AndroidElement getAndroidElement(String id, Object element, boolean isSingleMatch,
                                                   @Nullable By by) throws UiAutomator2Exception {
        return getAndroidElement(id, element, isSingleMatch, by, null);
    }

    public static AndroidElement getAndroidElement(String id, Object element, boolean isSingleMatch)
            throws UiAutomator2Exception {
        return getAndroidElement(id, element, isSingleMatch, null, null);
    }

    public static void wake() throws RemoteException {
        getUiDevice().wakeUp();
    }

    public static void scrollToElement(UiSelector selector, int maxSwipes)
            throws UiObjectNotFoundException {
        UiScrollable uiScrollable = new UiScrollable(new UiSelector().scrollable(true).instance(0));
        String uiScrollableClassName = uiScrollable.getClassName();
        String hScrollViewClassName = android.widget.HorizontalScrollView.class.getName();
        int defaultMaxSwipes = uiScrollable.getMaxSearchSwipes();

        if (java.util.Objects.equals(uiScrollableClassName, hScrollViewClassName)) {
            uiScrollable.setAsHorizontalList();
        }

        if (maxSwipes > 0) {
            uiScrollable.setMaxSearchSwipes(maxSwipes);
        }

        try {
            if (!uiScrollable.scrollIntoView(selector)) {
                throw new UiObjectNotFoundException("Cannot scroll to the element.");
            }
        } finally {
            // The number of search swipes is held in a static property of the UiScrollable class.
            // Whenever a non-default number of search swipes is used during the scroll, we must
            // always restore the setting after the operation.
            uiScrollable.setMaxSearchSwipes(defaultMaxSwipes);
        }
    }

    public static boolean back() {
        return getUiDevice().pressBack();
    }

    /**
     * reason for explicit method, in some cases google UiAutomator2 throwing exception
     * while calling waitForIdle() which is causing appium UiAutomator2 server to fall in
     * unexpected behaviour.
     * for more info please refer
     * https://code.google.com/p/android/issues/detail?id=73297
     */
    public static void waitForIdle() {
        final WaitForIdleTimeout idleTimeout =
                (WaitForIdleTimeout) Settings.WAIT_FOR_IDLE_TIMEOUT.getSetting();
        waitForIdle(idleTimeout.getValue());
    }

    public static void waitForIdle(long timeInMS) {
        Logger.info(String.format("Waiting up to %sms for device to be idle", timeInMS));
        try {
            getUiDevice().waitForIdle(timeInMS);
            Logger.debug("Done waiting");
        } catch (Exception e) {
            Logger.error(String.format("Unable wait %sms for AUT to idle", timeInMS));
        }
    }

    /////////////////////////////////// ADDED BY MO: extends waitForIdel ///////////////////////////////////////////////////
    public static final long TOTAL_TIME_TO_WAIT_FOR_IDLE_STATE = 1000 * 5;

    public static void waitForIdle(long idleTimeInMS, long globaTimeInMS) {
        try {
            //MODIFIED BY MO: If run page_source after running waitForIdle(idleTimeInMS, globaTimeInMS), the screen is flickering
            //CustomUiDevice.getInstance().getInstrumentation().getUiAutomation().waitForIdle(idleTimeInMS, globaTimeInMS);
            getUiDevice().waitForIdle(globaTimeInMS);
        }catch (Exception e) {
            Logger.error(String.format("Unable wait %d for AUT to idle", globaTimeInMS));
        }
    }
    //END

    /////////////////////////////////// MODIFIED BY MO: In Galaxy series, the display size is cut off by navigation bar. However, the navigation bar is hidden. ///////////
    public static android.graphics.Point getDeviceSize() {
        Display display = UiAutomatorBridge.getInstance().getDefaultDisplay();
        android.graphics.Point size = new android.graphics.Point();

        UseDeviceRealSize setting = (UseDeviceRealSize) Settings.USE_DEVICE_REAL_SIZE.getSetting();
        if (setting.getValue()) {
            display.getRealSize(size);
        } else {
            display.getSize(size);
        }
        return size;
    }
    //END
}
