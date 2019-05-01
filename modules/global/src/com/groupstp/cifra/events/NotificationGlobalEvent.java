package com.groupstp.cifra.events;

import com.groupstp.cifra.entity.Document;
import com.groupstp.workflowstp.entity.Stage;
import com.haulmont.addon.globalevents.GlobalApplicationEvent;
import com.haulmont.addon.globalevents.GlobalUiEvent;

public class NotificationGlobalEvent extends GlobalApplicationEvent implements GlobalUiEvent {

    private Stage stage;

    private Document document;

    /**
     * Create a new NotificationGlobalEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     * @param stage  stage that will receive event
     * @param document document that was move to next stage
     */
    public NotificationGlobalEvent(Object source, Stage stage, Document document) {
        super(source);
        this.stage = stage;
        this.document = document;
    }

    public Stage getStage() {
        return stage;
    }

    public Document getDocument() {
        return document;
    }
}
