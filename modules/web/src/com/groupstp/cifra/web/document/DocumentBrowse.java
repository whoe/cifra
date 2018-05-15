package com.groupstp.cifra.web.document;

import com.groupstp.cifra.entity.Document;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class DocumentBrowse extends AbstractLookup {

    @Named("income.edit")
    private EditAction incomeEditAction;

    @Named("ok.edit")
    private EditAction okEditAction;

    @Named("problems.edit")
    private EditAction problemsEditAction;

    @Inject
    GroupDatasource<Document, UUID> documentsIncomeDs;

    @Inject
    GroupDatasource<Document, UUID> documentsOkDs;

    @Inject
    GroupDatasource<Document, UUID> documentsProblemsDs;

    @Override
    public void init(Map<String, Object> params) {
        incomeEditAction.setAfterCommitHandler(entity -> refresh());
        okEditAction.setAfterCommitHandler(entity -> refresh());
        problemsEditAction.setAfterCommitHandler(entity -> refresh());
    }

    private void refresh()
    {
        documentsIncomeDs.refresh();
        documentsOkDs.refresh();
        documentsProblemsDs.refresh();
    }

    public void onIssue(Component source) {
    //TODO implement issue action
    }

}