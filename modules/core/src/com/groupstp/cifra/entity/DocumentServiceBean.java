package com.groupstp.cifra.entity;


import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service(DocumentService.NAME)
public class DocumentServiceBean implements DocumentService {

    private final int NUMBER_OF_TOP_TAGS = 3;

    @Inject
    private Persistence persistence;


    @Override
    public List requestTopTags() {
        EntityManager em = persistence.getEntityManager();
        Query query = em.createQuery(
                "select o from cifra$Tag o join o.documents doc group by o.id order by count(o.id) desc");
        return query.setMaxResults(NUMBER_OF_TOP_TAGS)
                .getResultList();
    }
}