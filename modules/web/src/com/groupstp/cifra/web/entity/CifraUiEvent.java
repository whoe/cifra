package com.groupstp.cifra.web.entity;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.events.UiEvent;
import org.springframework.context.ApplicationEvent;

public class CifraUiEvent extends ApplicationEvent implements UiEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    private CifraUiEvent(Object source) {
        super(source);
    }

    public static void push(String message) {
        AppBeans.get(Events.class).publish(new CifraUiEvent(message));
    }

}
