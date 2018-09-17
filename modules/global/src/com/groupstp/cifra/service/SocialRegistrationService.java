package com.groupstp.cifra.service;

import com.haulmont.cuba.security.entity.User;

public interface SocialRegistrationService {
    String NAME = "cifra_SocialRegistrationService";

    User findOrRegisterUser(String facebookId, String email, String name);
}