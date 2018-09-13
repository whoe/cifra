package com.groupstp.cifra.core;

import com.groupstp.cifra.bean.StartupEntitiesInitializationBean;
import com.haulmont.cuba.core.sys.AppContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * This bean is listening application lifecycle events
 *
 * @author adiatullin
 */
@Component("bills_ApplicationLifecycle")
public class ApplicationLifecycle implements AppContext.Listener {

    @Inject
    private StartupEntitiesInitializationBean startupEntitiesInit;

    public ApplicationLifecycle() {
        AppContext.addListener(this);
    }

    @Override
    public void applicationStarted() {
        startupEntitiesInit.init();
    }

    @Override
    public void applicationStopped() {
    }
}