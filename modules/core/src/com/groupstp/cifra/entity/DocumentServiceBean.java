package com.groupstp.cifra.entity;


import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.DataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service(DocumentService.NAME)
public class DocumentServiceBean implements DocumentService {

    private final int NUMBER_OF_TOP_TAGS = 3;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private DataManager dataManager;

    @Inject
    private Persistence persistence;


    @Override
    public List<Tag> requestTopTags(){
        Transaction tx = persistence.createTransaction();
        EntityManager em = persistence.getEntityManager();
        Query query = em.createQuery(
                "select o from cifra$Tag o join o.documents doc group by o.id order by count(o.id) desc");
        return query.setMaxResults(NUMBER_OF_TOP_TAGS).getResultList();
    }
}