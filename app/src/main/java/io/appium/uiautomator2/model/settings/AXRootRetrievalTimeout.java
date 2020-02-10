package io.appium.uiautomator2.model.settings;

public class AXRootRetrievalTimeout  extends AbstractSetting<Long> {
    private static final String SETTING_NAME = "axRootRetrievalTimeout";
    private long value = 3000;

    public AXRootRetrievalTimeout() {
        super(Long.class, SETTING_NAME);
    }

    @Override
    public Long getValue() {
        return this.value;
    }

    @Override
    protected void apply(Long timeout) {
        this.value = timeout;
    }
}
