package com.groupstp.cifra.service;

import com.company.googledrive.service.FileIdResolverService;
import com.groupstp.cifra.entity.Document;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service(UploadedFilesFinderService.NAME)
public class UploadedFilesFinderServiceBean implements UploadedFilesFinderService {

    @Inject
    private Persistence persistence;

    private Logger log = LoggerFactory.getLogger(UploadedFilesFinderServiceBean.class);

    @Inject
    private FileIdResolverService fileIdResolverService;

    @Inject
    private Metadata metadata;

    @Override
    public void find() {
        List list;
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            list = em
                    .createQuery("select o from cifra$Document o where o.file is null")
                    .getResultList();
            tx.commit();
        }

        Pattern pattern = Pattern.compile("/?([a-zA-Z0-9_-]{44})/?");

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            for (Object entityObject:
                    list) {
                Document document = (Document)entityObject;
                String externalLink = document.getExternalLink();
                Matcher m = pattern.matcher(externalLink);
                if (!m.find()) {
                    log.warn("Nothing matched for external link: " + externalLink +
                            "\npattern: " + pattern.toString());
                    continue;
                }
                String googleFileId = m.group(1);
                String fileId = fileIdResolverService.getFileId(googleFileId);

                // creating FileDescriptor (sys$File)
                FileDescriptor fd = metadata.create(FileDescriptor.class);
                fd.setName(document.getDescription());
                fd.setCreateDate(document.getCreateTs());
                fd.setId(UUID.fromString(fileId));
                em.persist(fd);

                // set file_id in cifra$Document
                document.setFile(fd);
                em.merge(document);
            }
            tx.commit();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

}
