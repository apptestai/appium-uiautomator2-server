package io.appium.uiautomator2.model.settings;

/**
 * ADDEDB BY MO: In Galaxy series, the display size is cut off by navigation bar. However, the navigation bar is hidden.
 */
public class UseDeviceRealSize extends AbstractSetting<Boolean> {
    private static final String SETTING_NAME = "useDeviceRealSize";
    private boolean value = false;

    public UseDeviceRealSize() {
        super(Boolean.class, SETTING_NAME);
    }

    @Override
    public Boolean getValue() {
        return this.value;
    }

    @Override
    protected void apply(Boolean value) {
        this.value = value;
    }
}
//END