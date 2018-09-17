package com.groupstp.cifra.core;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;

@Source(type = SourceType.APP)
public interface GoogleConfig extends Config {

    @Property("google.appId")
    String getGoogleAppId();

    @Property("google.appSecret")
    String getGoogleAppSecret();

    @Default("[\"https://www.googleapis.com/auth/plus.me\"," +
            "\"https://www.googleapis.com/auth/userinfo.email\"]")
    @Property("google.scope")
    String getGoogleScope();
}