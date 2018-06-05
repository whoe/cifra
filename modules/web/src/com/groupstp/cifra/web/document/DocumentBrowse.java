package com.groupstp.cifra.web.document;

import com.groupstp.cifra.entity.CheckList;
import com.groupstp.cifra.entity.Document;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.sun.javafx.css.Declaration;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DocumentBrowse extends AbstractLookup {

    @Inject
    protected Table<Document> problems;

    @Inject
    protected ComponentsFactory componentsFactory;

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

        problems.addGeneratedColumn("problems", entity -> {

            List<CheckList> checkList = entity.getChecklist();
            StringBuilder itog = new StringBuilder();
            for (CheckList object : checkList) {
                if (!(object.getChecked() == null ? false : object.getChecked())) {
                    String comment = object.getComment();
                    if (comment == null) {
                        continue;
                    }
                    itog.append(comment).append("\n");
                }
            }

            if (!"".equals(itog.toString())) {
                String position = itog.toString();
                if (position.length() < 15) {
                    return new Table.PlainTextCell(position);
                }
                PopupView popupView = componentsFactory.createComponent(PopupView.class);
                popupView.setMinimizedValue(position.substring(0, 10));
                TextArea textArea = componentsFactory.createComponent(TextArea.class);
                textArea.setEditable(false);
                textArea.setWidth("200px");
                textArea.setHeight("150px");
                textArea.setValue(position);
                popupView.setPopupContent(textArea);
                return popupView;
            }
            return null;
        });

        problems.getColumn("problems").setMaxTextLength(5);
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