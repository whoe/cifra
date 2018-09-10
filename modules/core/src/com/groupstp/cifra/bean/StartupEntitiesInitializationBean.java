package com.groupstp.cifra.bean;


import com.groupstp.workflowstp.entity.Stage;
import com.groupstp.workflowstp.entity.Step;
import com.groupstp.workflowstp.entity.StepDirection;
import com.groupstp.workflowstp.entity.Workflow;
import com.haulmont.cuba.core.app.ConfigStorageAPI;
import com.haulmont.cuba.core.app.importexport.CollectionImportPolicy;
import com.haulmont.cuba.core.app.importexport.EntityImportExportAPI;
import com.haulmont.cuba.core.app.importexport.EntityImportView;
import com.haulmont.cuba.core.app.importexport.ReferenceImportBehaviour;
import com.haulmont.cuba.core.global.Resources;
import com.haulmont.cuba.core.sys.encryption.Sha1EncryptionModule;
import com.haulmont.cuba.security.app.Authenticated;
import com.haulmont.cuba.security.entity.Permission;
import com.haulmont.cuba.security.entity.Role;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Objects;

/**
 * This bean is using for init entities from json (init folder) on application startup
 *
 * @author adiatullin
 */
@Component
public class StartupEntitiesInitializationBean {
    private static final Logger log = LoggerFactory.getLogger(StartupEntitiesInitializationBean.class);

    @Inject
    private EntityImportExportAPI entityImportExport;
    @Inject
    private Sha1EncryptionModule encryptionModule;
    @Inject
    private Resources resources;
    @Inject
    private ConfigStorageAPI configStorage;

    @Authenticated
    public void init() {
        initWorkflow();
    }

    private void initWorkflow() {
        importEntitiesFromJson("com/groupstp/cifra/init/Workflows.json", createWorkflowImportView());
    }

    /**
     * Checkup and import entities
     *
     * @param filePath         json file path
     * @param entityImportView entities import view
     */
    private void importEntitiesFromJson(String filePath, EntityImportView entityImportView) {
        try {
            String json = resources.getResourceAsString(filePath);
            if (StringUtils.isEmpty(json)) {
                log.warn("{} not found", filePath);
                return;
            }
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

            String currentHash = encryptionModule.getPlainHash(json);
            String prevHash = getPreviousHash(fileName);

            if (!Objects.equals(currentHash, prevHash)) {
                log.info("'{}' content has been changed. The import will be performed", filePath);
                entityImportExport.importEntitiesFromJson(json, entityImportView);
                setHash(fileName, currentHash);//update hash with new value
            }
        } catch (Exception e) {
            log.error(String.format("Failed to import entities on startup from '%s'", filePath), e);
        }
    }

    private String getPreviousHash(String fileName) {
        return configStorage.getDbProperty(fileName);
    }

    private void setHash(String fileName, String hash) {
        configStorage.setDbProperty(fileName, hash);
    }

    private EntityImportView createRolesImportView() {
        return new EntityImportView(Role.class)
                .addLocalProperties()
                .addOneToManyProperty("permissions",
                        new EntityImportView(Permission.class).addLocalProperties(),
                        CollectionImportPolicy.REMOVE_ABSENT_ITEMS);
    }

    private EntityImportView createWorkflowImportView() {
        return new EntityImportView(Workflow.class)
                .addLocalProperties()
                .addOneToManyProperty("steps",
                        new EntityImportView(Step.class)
                                .addLocalProperties()
                                .addManyToOneProperty("stage",
                                        new EntityImportView(Stage.class)
                                                .addLocalProperties()
                                                .addManyToManyProperty("actors", ReferenceImportBehaviour.ERROR_ON_MISSING, CollectionImportPolicy.REMOVE_ABSENT_ITEMS)
                                                .addManyToOneProperty("actorsRoles", ReferenceImportBehaviour.ERROR_ON_MISSING))
                                .addOneToManyProperty("directions",
                                        new EntityImportView(StepDirection.class)
                                                .addLocalProperties()
                                                .addManyToOneProperty("from", ReferenceImportBehaviour.ERROR_ON_MISSING)
                                                .addManyToOneProperty("to", ReferenceImportBehaviour.ERROR_ON_MISSING), CollectionImportPolicy.REMOVE_ABSENT_ITEMS)
                                .addManyToOneProperty("workflow", ReferenceImportBehaviour.ERROR_ON_MISSING),
                        CollectionImportPolicy.REMOVE_ABSENT_ITEMS);

    }
}
