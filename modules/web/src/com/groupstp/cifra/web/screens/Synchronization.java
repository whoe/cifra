package com.groupstp.cifra.web.screens;

import com.groupstp.cifra.entity.ImportDocs1CService;
import com.groupstp.cifra.entity.Sync1CService;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.components.*;

import javax.inject.Inject;
import javax.xml.bind.Element;

import com.haulmont.cuba.gui.settings.Settings;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Synchronization extends AbstractWindow {

    @Inject
    private ImportDocs1CService importDocs1CService;
    @Inject
    private ResizableTextArea log;

    @Inject
    private TextField txtPass;

    @Inject
    private TextField txtUrl;

    @Inject
    private DateField dateEnd;
    @Inject
    private DateField dateStart;

    public void onTestSyncClick() throws Exception {
            importDocs1CService.ImportCompanies1C(txtUrl.getRawValue(), txtPass.getRawValue());
            importDocs1CService.ImportDocs1C(txtUrl.getRawValue(), txtPass.getRawValue(), dateStart.getValue(), dateEnd.getValue());
            // http://stpserver.groupstp.ru:1805/accnt2016/
    }

    /**
     * This method is called when the screen is closed to save the screen settings to the database.
     */
    @Override
    public void saveSettings() {
        org.dom4j.Element x = getSettings().get(this.getId());
        x.addAttribute("value", txtPass.getRawValue());
        x.addAttribute("url", txtUrl.getRawValue());
        getSettings().setModified(true);
        super.saveSettings();
    }

    @Inject
    private TimeSource timeSource;

    /**
     * Hook to be implemented in subclasses. <br>
     * Called by the framework after the screen is fully initialized and opened. <br>
     * Override this method and put custom initialization logic here.
     */
    @Override
    public void ready() {
        dateStart.setValue(timeSource.currentTimestamp());
        dateEnd.setValue(timeSource.currentTimestamp());
    }

    /**
     * This method is called when the screen is opened to restore settings saved in the database for the current user.
     * <p>You can override it to restore custom settings.
     * <p>For example:
     * <pre>
     * public void applySettings(Settings settings) {
     *     super.applySettings(settings);
     *     String visible = settings.get(hintBox.getId()).attributeValue("visible");
     *     if (visible != null)
     *         hintBox.setVisible(Boolean.valueOf(visible));
     * }
     * </pre>
     *
     * @param settings settings object loaded from the database for the current user
     */
    @Override
    public void applySettings(Settings settings) {
        super.applySettings(settings);
        txtPass.setValue(settings.get(this.getId()).attributeValue("value"));
        txtUrl.setValue(settings.get(this.getId()).attributeValue("url"));
    }
}