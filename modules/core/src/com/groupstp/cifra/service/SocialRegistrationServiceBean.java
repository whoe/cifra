package com.groupstp.cifra.service;

import com.groupstp.cifra.entity.SocialUser;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.security.entity.User;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.transaction.Transactional;

@Service(SocialRegistrationService.NAME)
public class SocialRegistrationServiceBean implements SocialRegistrationService {

    @Inject
    private Persistence persistence;

    @Override
    @Transactional
    @Nullable
    public User findUser(String googleId, String email, String name) {
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

        return null;
    }
}

