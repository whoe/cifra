package com.groupstp.cifra.web.screens;

import com.groupstp.cifra.entity.ImportDocs1CService;
import com.groupstp.cifra.entity.Sync1CService;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.ResizableTextArea;

import javax.inject.Inject;
import javax.xml.bind.Element;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.TextField;
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

    public void onTestSyncClick() throws Exception {
            importDocs1CService.ImportCompanies1C("http://stpserver.groupstp.ru:1805/accnt2016/", txtPass.getRawValue());
            importDocs1CService.ImportDocs1C("http://stpserver.groupstp.ru:1805/accnt2016/", txtPass.getRawValue());
    }

    /**
     * This method is called when the screen is closed to save the screen settings to the database.
     */
    @Override
    public void saveSettings() {
        org.dom4j.Element x = getSettings().get(this.getId());
        x.addAttribute("value", txtPass.getRawValue());
        getSettings().setModified(true);
        super.saveSettings();
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
    }
}