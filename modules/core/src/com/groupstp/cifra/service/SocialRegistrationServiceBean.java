package com.groupstp.cifra.service;

import com.groupstp.cifra.core.SocialRegistrationConfig;
import com.groupstp.cifra.entity.SocialUser;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.User;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Service(SocialRegistrationService.NAME)
public class SocialRegistrationServiceBean implements SocialRegistrationService {

    @Inject
    private Metadata metadata;

    @Inject
    private Persistence persistence;

    @Inject
    private Configuration configuration;

    @Override
    @Transactional
    public User findOrRegisterUser(String googleId, String email, String name) {
        EntityManager em = persistence.getEntityManager();

        // Find existing user
        TypedQuery<SocialUser> query = em.createQuery(
                "select u from sec$User u where u.googleId = :googleId or u.email like :email",
                SocialUser.class);
        query.setParameter("googleId", googleId);
        query.setParameter("email", email);
        query.setViewName(View.LOCAL);

        SocialUser existingUser = query.getFirstResult();
        if (existingUser != null) {
            if (existingUser.getGoogleId() == null) {
                existingUser.setGoogleId(googleId);
                em.persist(existingUser);
            }
            return existingUser;
        }

        SocialRegistrationConfig config = configuration.getConfig(SocialRegistrationConfig.class);

        Group defaultGroup = em.find(Group.class, config.getDefaultGroupId(), View.MINIMAL);

        // Register new user
        SocialUser user = metadata.create(SocialUser.class);
        user.setGoogleId(googleId);
        user.setEmail(email);
        user.setName(name);
        user.setGroup(defaultGroup);
        user.setActive(true);
        user.setLogin(email);

        em.persist(user);

        return user;
    }
}

