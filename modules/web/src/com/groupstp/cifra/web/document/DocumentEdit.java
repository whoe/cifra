package com.groupstp.cifra.web.document;

import com.groupstp.cifra.entity.*;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            }
            else if("file".equals(e.getProperty()))
            {
                dateLoad.setValue(new Date());
            }
        });

        company.addLookupAction();
        company.addOpenAction();
        company.addClearAction();
    }

    public void onCheckcheck(Component source) {
        Document currentDocument = documentDs.getItem();
        documentDs.commit();
        checkListService.fillCheckList(currentDocument);
        documentDs.refresh();
    }

    public void onArchive(Component source) {
        ChooseWarehouseCell dialog = (ChooseWarehouseCell) openWindow("chooseWarehouseCell", WindowManager.OpenType.DIALOG);
        dialog.addCloseWithCommitListener(()-> {
            Document doc = documentDs.getItem();
            doc.setWarehouse(dialog.getWarehouse().getValue());
            doc.setCell(dialog.getCell().getValue());
            documentService.ArchiveDocument(doc);
            documentDs.refresh();
        });
    }

    public void onOkBtnClick(Component source) {
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

    public void onCancelBtnClick(Component source) {
        this.close("close");
    }
}