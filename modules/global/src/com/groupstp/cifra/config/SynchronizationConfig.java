package com.groupstp.cifra.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;

@Source(type = SourceType.APP)
public interface SynchronizationConfig extends Config {

    @Property("sync.password")
    String getPassword();

    @Property("sync.url")
    String getUrl();


}
