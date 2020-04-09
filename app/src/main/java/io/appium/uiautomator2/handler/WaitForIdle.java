package io.appium.uiautomator2.handler;

import android.os.SystemClock;

import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.settings.Settings;
import io.appium.uiautomator2.model.settings.WaitForIdleTimeout;
import io.appium.uiautomator2.utils.Device;
import io.appium.uiautomator2.utils.Logger;

/**
 * ADDED BY MO: extends waitForIdel
 */

public class WaitForIdle extends SafeRequestHandler {

    public WaitForIdle(String mappedUri) {
        super(mappedUri);
    }

    @Override
    protected AppiumResponse safeHandle(IHttpRequest request) throws JSONException {
        Long idleTimeout = ((WaitForIdleTimeout) Settings.WAIT_FOR_IDLE_TIMEOUT.getSetting()).getValue();
        Long globalTimeout = Device.TOTAL_TIME_TO_WAIT_FOR_IDLE_STATE;
        long start = SystemClock.uptimeMillis();
        try {
            JSONObject payload = toJSON(request);
            if (payload.has("idleTimeout")) {
                idleTimeout = Long.parseLong(payload.getString("idleTimeout"));
            }

            if (payload.has("globalTimeout")) {
                globalTimeout = Long.parseLong(payload.getString("globalTimeout"));
            }
            Device.waitForIdle(idleTimeout, globalTimeout);
        } catch (JSONException e) {
            Logger.error("Unable to get timeout value from the json payload", e);
            return new AppiumResponse(getSessionId(request), e);
        }
        long time = SystemClock.uptimeMillis() - start;
        return new AppiumResponse(getSessionId(request), time);
    }
}
//END
