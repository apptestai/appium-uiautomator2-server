package io.appium.uiautomator2.handler;

import android.os.SystemClock;

import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Device;
import io.appium.uiautomator2.utils.Logger;

public class WaitForIdle extends SafeRequestHandler {

    public WaitForIdle(String mappedUri) {
        super(mappedUri);
    }

    @Override
    protected AppiumResponse safeHandle(IHttpRequest request) throws JSONException {
//        Integer timeout = 1000;
//        JSONObject payload = getPayload(request);
//        if (payload.has("timeout")) {
//            timeout = Integer.parseInt(payload.getString("timeout"));
//        }
//        Device.waitForIdle(timeout);
//        return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, "Device waited");
        /////////////////////////////////// ADDED BY MO: extends waitForIdel ///////////////////////////////////////////////////
        Integer idleTimeout = (int)Device.QUIET_TIME_TO_BE_CONSIDERD_IDLE_STATE;
        Integer globalTimeout = (int)Device.TOTAL_TIME_TO_WAIT_FOR_IDLE_STATE;
        long start = SystemClock.uptimeMillis();
        try {
            JSONObject payload = getPayload(request);
            if (payload.has("idleTimeout")) {
                idleTimeout = Integer.parseInt(payload.getString("idleTimeout"));
            }

            if (payload.has("globalTimeout")) {
                globalTimeout = Integer.parseInt(payload.getString("globalTimeout"));
            }
            Device.waitForIdle(idleTimeout, globalTimeout);
        } catch (JSONException e) {
            Logger.error("Unable to get timeout value from the json payload", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        }
        long time = SystemClock.uptimeMillis() - start;
        return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, time);
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }
}
