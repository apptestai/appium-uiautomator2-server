package io.appium.uiautomator2.model.settings;

/**
 * ADDEDB BY MO: to solve too many elements
 */
public class XMLDumpMaxSize extends AbstractSetting<Long> {
    private static final String SETTING_NAME = "xmlDumpMaxSize";
    private long value = 1000000; // bytes (1.0Mb)

    public XMLDumpMaxSize() {
        super(Long.class, SETTING_NAME);
    }

    @Override
    public Long getValue() {
        return this.value;
    }

    @Override
    protected void apply(Long value) {
        this.value = value;
    }
}
//END