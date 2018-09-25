package com.groupstp.cifra.service;

import com.haulmont.cuba.security.entity.User;

import javax.annotation.Nullable;

public interface SocialRegistrationService {
    String NAME = "cifra_SocialRegistrationService";

    @Nullable
    User findUser(String googleId, String email, String name);
}