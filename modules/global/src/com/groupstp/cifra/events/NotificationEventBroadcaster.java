package com.groupstp.cifra.events;

import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.events.NotificationGlobalEvent;
import com.groupstp.workflowstp.entity.Stage;
import com.groupstp.workflowstp.entity.WorkflowInstanceTask;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Events;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.management.NotificationBroadcaster;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

@Component
public class NotificationEventBroadcaster {

    private final List<WeakReference<Consumer<NotificationGlobalEvent>>> subscriptions = new ArrayList<>();

    @Inject
    WorkflowService workflowService;

    Logger log = LoggerFactory.getLogger(NotificationBroadcaster.class);

    @EventListener
    protected void onNotification(NotificationGlobalEvent event) {
        log.info("notification in broadcaster");
        synchronized (subscriptions) {
            Iterator<WeakReference<Consumer<NotificationGlobalEvent>>> iterator = subscriptions.iterator();
            while (iterator.hasNext()) {
                WeakReference<Consumer<NotificationGlobalEvent>> reference = iterator.next();
                Consumer<NotificationGlobalEvent> eventConsumer = reference.get();
                if (eventConsumer == null) {
                    iterator.remove();
                } else {
                    eventConsumer.accept(event);
                }
            }
        }
    }

    public void subscribe(Consumer<NotificationGlobalEvent> handler) {
        synchronized (subscriptions) {
            subscriptions.add(new WeakReference<>(handler));
        }
    }

    public void publish(Object source, Document document) {
        WorkflowInstanceTask wit = workflowService.getWorkflowInstanceTaskIC(document);
        if (wit != null) {
            Stage stage = wit.getStep().getStage();
            NotificationGlobalEvent notificationEvent = new NotificationGlobalEvent(source, stage, document);
            AppBeans.get(Events.class).publish(notificationEvent);
            log.info("notification event published");
        }
    }

}
