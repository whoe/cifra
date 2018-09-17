package com.groupstp.cifra.entity;

import javax.persistence.Entity;
import com.haulmont.cuba.core.entity.annotation.Extends;
import javax.persistence.Column;
import com.haulmont.cuba.security.entity.User;

@Extends(User.class)
@Entity(name = "cifra$SocialUser")
public class SocialUser extends User {
    private static final long serialVersionUID = -2823117926539607534L;

    @Column(name = "GOOGLE_ID")
    protected String googleId;

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getGoogleId() {
        return googleId;
    }
}