package com.groupstp.cifra.web.login;


import com.groupstp.cifra.service.GoogleService;
import com.groupstp.cifra.service.SocialRegistrationService;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.gui.executors.BackgroundWorker;
import com.haulmont.cuba.gui.executors.UIAccessor;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.web.Connection;
import com.haulmont.cuba.web.app.loginwindow.AppLoginWindow;
import com.haulmont.cuba.web.controllers.ControllerUtils;
import com.haulmont.cuba.web.security.ExternalUserCredentials;
import com.vaadin.server.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import java.util.Map;

public class ExtAppLoginWindow extends AppLoginWindow {

    private final Logger log = LoggerFactory.getLogger(ExtAppLoginWindow.class);

    private RequestHandler googleCallBackRequestHandler =
            this::handleGoogleCallBackRequest;

    @Inject
    private BackgroundWorker backgroundWorker;

    @Inject
    private SocialRegistrationService socialRegistrationService;

    @Inject
    private GoogleService googleService;

    @Inject
    private GlobalConfig globalConfig;

    private URI redirectUri;
    private UIAccessor uiAccessor;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        this.uiAccessor = backgroundWorker.getUIAccessor();
    }

    public void signInGoogle() {
        VaadinSession.getCurrent()
                .addRequestHandler(googleCallBackRequestHandler);

        this.redirectUri = Page.getCurrent().getLocation();

        String loginUrl = googleService.getLoginUrl(globalConfig.getWebAppUrl(), GoogleService.OAuth2ResponseType.CODE);
        Page.getCurrent()
                .setLocation(loginUrl);
    }

    private boolean handleGoogleCallBackRequest(VaadinSession session, VaadinRequest request,
                                               VaadinResponse response) throws IOException {
        if (request.getParameter("code") != null) {
            uiAccessor.accessSynchronously(() -> {
                try {
                    String code = request.getParameter("code");

                    GoogleService.GoogleUserData userData = googleService.getUserData(globalConfig.getWebAppUrl(), code);

                    User user = socialRegistrationService.findOrRegisterUser(
                            userData.getId(), userData.getEmail(), userData.getName());

                    Connection connection = app.getConnection();

                    Locale defaultLocale = messages.getTools().getDefaultLocale();
                    connection.login(new ExternalUserCredentials(user.getLogin(), defaultLocale));
                } catch (Exception e) {
                    log.error("Unable to login using Google+", e);
                } finally {
                    session.removeRequestHandler(googleCallBackRequestHandler);
                }
            });

            ((VaadinServletResponse) response).getHttpServletResponse().
                    sendRedirect(ControllerUtils.getLocationWithoutParams(redirectUri));

            return true;
        }

        return false;
    }
}