package com.groupstp.cifra.web.document;

import com.groupstp.cifra.entity.*;
import com.groupstp.workflowstp.entity.Workflow;
import com.groupstp.workflowstp.exception.WorkflowException;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.DsContext;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DocumentEdit extends AbstractEditor<Document> {
    @Inject
    private Datasource<Document> documentDs;

    @Inject
    private CollectionDatasource<CheckList, UUID> checklistDs;

    @Inject
    private CheckListService checkListService;

    @Inject
    private DocumentService documentService;

    @Named("fieldGroup.docType")
    private PickerField docType;

    @Named("fieldGroup.dateLoad")
    private DateField dateLoad;

    @Named("fieldGroup.company")
    private LookupPickerField company;

    @Named("fieldGroup.tag")
    private TokenList tags;

    @Inject
    private DsContext dsContext;

    @Inject
    private Metadata metadata;

    @Inject
    private DataManager dataManager;

    @Inject
    private WorkflowService workflowService;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected void initNewItem(Document item) {
        super.initNewItem(item);
        item.setDocStatus(DocStatus.NEW);
    }

    @Override
    public void init(Map<String, Object> params) {

        documentDs.addItemPropertyChangeListener(e -> {
            if ("docType".equals(e.getProperty())) {
                checkListService.clearCheckList(documentDs.getItem());
                List<CheckList> items = checkListService.fillCheckList(documentDs.getItem());
                items.forEach(checklistDs::addItem);
            } else if("file".equals(e.getProperty())) {
                dateLoad.setValue(new Date());
            }
        });

        company.addLookupAction();
        company.addOpenAction();
        company.addClearAction();

        addListenerForStartingrWorkflow();

    }

    /**
     * After commit document to DB start workflow
     */
    private void addListenerForStartingrWorkflow() {
        dsContext.addAfterCommitListener((context, result) -> {

            Workflow workflow = getActiveWorkflow(Document.class);
            Document savedDocument = (Document) result.stream().filter(entity -> entity.getClass() == Document.class).findFirst().get();

            try {
                workflowService.startWorkflow(savedDocument, workflow);
            } catch (WorkflowException e) {
                log.error(String.format("Failed to launch workflow %s for document %s", workflow, this), e);
                showNotification(String.format(getMessage("queryWorkflowBrowseTableFrame.workflowFailed"),
                        e.getMessage() == null ? getMessage("queryWorkflowBrowseTableFrame.notAvailable") : e.getMessage()),
                        NotificationType.ERROR);
            }
        });
    }

    /**
     * Return one active workflow for Class or return null
     *
     * @param classOfDocument Class
     * @return active workflow for Class
     */
    @Nullable
    private Workflow getActiveWorkflow(Class classOfDocument) {
        String entityName = metadata.getClassNN(classOfDocument).getName();
        List<Workflow> list = dataManager.loadList(LoadContext.create(Workflow.class)
                .setQuery(new LoadContext.Query("select e from wfstp$Workflow e where " +
                        "e.active = true and e.entityName = :entityName order by e.createTs asc")
                        .setParameter("entityName", entityName))
                .setView("query-workflow-browse"));
        if (!CollectionUtils.isEmpty(list)) {
            if (list.size() > 1) {
                log.warn(String.format("In system existing two active workflow for entity '%s'. The first will be used", entityName));
            }
            return list.get(0);
        }
        return null;
    }

    public void onCheckcheck(Component ignore) {
        Document currentDocument = documentDs.getItem();
        documentDs.commit();
        checkListService.fillCheckList(currentDocument);
        documentDs.refresh();
    }

    public void onArchive(Component ignore) {
        ChooseWarehouseCell dialog = (ChooseWarehouseCell) openWindow("chooseWarehouseCell", WindowManager.OpenType.DIALOG);
        dialog.addCloseWithCommitListener(()-> {
            Document doc = documentDs.getItem();
            doc.setWarehouse(dialog.getWarehouse().getValue());
            doc.setCell(dialog.getCell().getValue());
            documentService.ArchiveDocument(doc);
            documentDs.refresh();
        });
    }

    public void onOkBtnClick(Component ignore) {
        Boolean gotOriginal = getItem().getGotOriginal() == null ? false : getItem().getGotOriginal();
        if (gotOriginal) {

            for (CheckList object : checklistDs.getItems()) {
                Boolean checked = object.getChecked() == null ? false : object.getChecked();
                Boolean commented = object.getComment() != null;
                if (!checked && !commented) {
                    showNotification("Введите комментарии в чек-лист");
                    return;
                }
            }
            commitAndClose();
        } else {
            commitAndClose();
        }
    }

    public void onCancelBtnClick(Component ignore) {
        this.close("close");
    }

}