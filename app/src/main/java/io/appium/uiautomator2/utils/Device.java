package io.appium.uiautomator2.utils;

import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;

import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.By;
import io.appium.uiautomator2.model.UiObject2Element;
import io.appium.uiautomator2.model.UiObjectElement;

public abstract class Device {

//    public static final UiDevice getUiDevice() {
//        return UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
//    }
//
//    public static AndroidElement getAndroidElement(String id, Object element, By by)
//            throws UiAutomator2Exception {
//        if (element instanceof UiObject2) {
//            return new UiObject2Element(id, (UiObject2) element, by);
//        } else if (element instanceof UiObject) {
//            return new UiObjectElement(id, (UiObject) element, by);
//        } else {
//            throw new UiAutomator2Exception("Unknown Element type: " + element.getClass().getName());
//        }
//    }
//
//    public static void wake() throws RemoteException {
//        getUiDevice().wakeUp();
//    }
//
//    public static void scrollTo(String scrollToString) throws UiObjectNotFoundException {
//        // TODO This logic needs to be changed according to the request body from the Driver
//        UiScrollable uiScrollable = new UiScrollable(new UiSelector().scrollable(true).instance(0));
//        uiScrollable.scrollIntoView(new UiSelector().descriptionContains(scrollToString).instance(0));
//        uiScrollable.scrollIntoView(new UiSelector().textContains(scrollToString).instance(0));
//    }
//
//    public static boolean back() {
//        return getUiDevice().pressBack();
//    }
//
//    /**
//     * reason for explicit method, in some cases google UiAutomator2 throwing exception
//     * while calling waitForIdle() which is causing appium UiAutomator2 server to fall in
//     * unexpected behaviour.
//     * for more info please refer
//     * https://code.google.com/p/android/issues/detail?id=73297
//     */
//    public static void waitForIdle() {
//        try {
//            getUiDevice().waitForIdle();
//        } catch (Exception e) {
//            Logger.error("Unable wait for AUT to idle");
//        }
//    }
//
//    public static void waitForIdle(long timeInMS) {
//        try {
//            getUiDevice().waitForIdle(timeInMS);
//        } catch (Exception e) {
//            Logger.error(String.format("Unable wait %d for AUT to idle", timeInMS));
//        }
//    }

    /////////////////////////////////// ADDED BY MO: extends waitForIdel ///////////////////////////////////////////////////
    // UiDevice#waitForIdle()은 10초로 되어 있음.
//    public static final long TOTAL_TIME_TO_WAIT_FOR_IDLE_STATE = 1000 * 6;
    public static final long TOTAL_TIME_TO_WAIT_FOR_IDLE_STATE = 1000 * 5;
    public static final long QUIET_TIME_TO_BE_CONSIDERD_IDLE_STATE = 3000;//ms

    // Uiautomation 참조
    private static final long QUICK_QUIET_TIME_TO_BE_CONSIDERD_IDLE_STATE = 500;//ms

    public static final UiDevice getUiDevice() {
        return UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    public static AndroidElement getAndroidElement(String id, Object element, By by) throws UiAutomator2Exception {
        if (element instanceof UiObject2) {
            return new UiObject2Element(id, (UiObject2) element, by);
        } else if (element instanceof UiObject) {
            return new UiObjectElement(id, (UiObject) element, by);
        } else {
            throw new UiAutomator2Exception("Unknown Element type: " + element.getClass().getName());
        }
    }

    public static void wake() throws RemoteException {
        getUiDevice().wakeUp();
    }

    public static void scrollTo(String scrollToString) throws UiObjectNotFoundException {
        // TODO This logic needs to be changed according to the request body from the Driver
        UiScrollable uiScrollable = new UiScrollable(new UiSelector().scrollable(true).instance(0));
        uiScrollable.scrollIntoView(new UiSelector().descriptionContains(scrollToString).instance(0));
        uiScrollable.scrollIntoView(new UiSelector().textContains(scrollToString).instance(0));
    }

    public static boolean back() {
        return getUiDevice().pressBack();
    }

    public static void waitForIdle() {
        Device.waitQuicklyForIdle();
    }

    public static void waitQuicklyForIdle() {
        try {
            InstrumentationRegistry.getInstrumentation().getUiAutomation().waitForIdle(QUICK_QUIET_TIME_TO_BE_CONSIDERD_IDLE_STATE, TOTAL_TIME_TO_WAIT_FOR_IDLE_STATE);
        }catch (Exception e) {
            Logger.error(String.format("Unable wait %d for AUT to idle", TOTAL_TIME_TO_WAIT_FOR_IDLE_STATE));
        }
    }

    public static void waitForIdle(long timeInMS) {
        try {
            InstrumentationRegistry.getInstrumentation().getUiAutomation().waitForIdle(QUIET_TIME_TO_BE_CONSIDERD_IDLE_STATE, timeInMS);
        }catch (Exception e) {
            Logger.error(String.format("Unable wait %d for AUT to idle", timeInMS));
        }
    }

    public static void waitForIdle(long idleTimeInMS, long globaTimeInMS) {
        try {
            InstrumentationRegistry.getInstrumentation().getUiAutomation().waitForIdle(idleTimeInMS, globaTimeInMS);
        }catch (Exception e) {
            Logger.error(String.format("Unable wait %d for AUT to idle", globaTimeInMS));
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
