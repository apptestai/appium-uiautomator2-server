package io.appium.uiautomator2.model.settings;

/**
 * ADDEDB BY MO: to solve too many elements
 */
public class XMLDumpSkipUnboud extends AbstractSetting<Boolean> {
    private static final String SETTING_NAME = "xmlDumpSkipUnboud";
    private boolean value = true;

    public XMLDumpSkipUnboud() {
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