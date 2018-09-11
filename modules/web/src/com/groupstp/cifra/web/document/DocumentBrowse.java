package com.groupstp.cifra.web.document;

import com.groupstp.cifra.entity.*;
import com.groupstp.cifra.web.data.FilterTagsCollectionDatasource;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.data.impl.CustomGroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.vaadin.ui.Layout;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class DocumentBrowse extends AbstractLookup {

    private final String incomeStatus = "10";

    private final String okStatus = "20";

    private final String problemStatus = "30";

    private final String issueStatus = "40";

    @Inject
    protected Table<Document> problems;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Named("tagsDs")
    private CollectionDatasource<Tag, UUID> optionsTagsDs;

    @Named("tabs")
    private TabSheet tabSheet;


    @Named("income.edit")
    private EditAction incomeEditAction;

    @Named("ok.edit")
    private EditAction okEditAction;

    @Named("problems.edit")
    private EditAction problemsEditAction;

    @Named("filter")
    private Filter filter;

    @Named("tabIncome.tokenList")
    private TokenList tokenList;

    @Named("topTagsContainer")
    private BoxLayout tagsContainer;

    @Inject
    private Table<Document> issue;

    @Inject
    private Table<Document> ok;

    @Inject
    CollectionDatasource<Document, UUID> documentDs;

    @Inject
    GroupDatasource<Document, UUID> documentsIncomeDs;

    @Inject
    GroupDatasource<Document, UUID> documentsOkDs;

    @Inject
    GroupDatasource<Document, UUID> documentsProblemsDs;

    @Inject
    CustomGroupDatasource<Document, UUID> documentsIssueDs;

    @Inject
    private DocumentService documentService;

    @Inject private Layout tabularBox;

    @Inject
    protected Messages messages;

    private List<String> initializedTabs;

    @Override
    public void init(Map<String, Object> params) {
        initializedTabs = new ArrayList<>();

        //Init default tab
        initializedTabs.add("II");
        genTagFilterComponent("incomeHBox");

        tabSheet.addSelectedTabChangeListener((event) -> {
            String tabName = event.getSelectedTab().getName();
            if(initializedTabs.contains(tabName))
                return;

            initializedTabs.add(tabName);

            switch (tabName) {
                case "II":
                    genTagFilterComponent("incomeHBox");
                    break;

                case "tabOk":
                    genTagFilterComponent("okHBox");
                    break;

                case "tabProblems":
                    genTagFilterComponent("problemsHBox");
                    break;

                case "tabIssued":
                    genTagFilterComponent("issuedHBox");
                    break;
            }
        });

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

        ((IssueDocumentDs)documentsIssueDs).setDocumentService(documentService);

    }

    private void refresh()
    {
        documentsIncomeDs.refresh();
        documentsOkDs.refresh();
        documentsProblemsDs.refresh();
        documentsIssueDs.refresh();
    }

    //Add top 3 tags to window
    private void fillTopTags(String mainHBoxId,HBoxLayout tagsContainer,CollectionDatasource<Tag, UUID> filteredTagsDs){
        List<Tag> tags = documentService.requestTopTags();
        tags.forEach((tag) -> {
            LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
            linkButton.setCaption(tag.getName());
            Action action = new AbstractAction("tagClick") {
                @Override
                public void actionPerform(Component component) {
                    filteredTagsDs.addItem(tag);
                    applyFilter(mainHBoxId,filteredTagsDs);
                }
            };
            linkButton.setAction(action);
            tagsContainer.add(linkButton);
        });


    }

    private String makeQueryString(Collection<UUID> coll){
         String defaultQuery = "select d from cifra$Document d";
         String filterQuery = defaultQuery;
         Iterator it = coll.iterator();
         while (it.hasNext()){
             if(filterQuery.equals(defaultQuery)){
                 filterQuery += " inner join d.tag tag where (d.docStatus = ':statusCode') and (tag.id = ':uuid')";
             }

             else {
                 filterQuery = filterQuery.substring(0,filterQuery.length()-1);
                 filterQuery += " or tag.id = ':uuid')";
             }

             filterQuery = filterQuery.replace(":uuid",it.next().toString());

         }
        if(filterQuery.indexOf("where") < 0)
            filterQuery += " where d.docStatus = ':statusCode'";
         return filterQuery;
    }

    public void onIssue(Component source) {
        Set<Document> doc = ok.getSelected();
        if(doc.size()>0){
            String desc;
            if(doc.size()>1)
                desc=messages.getMessage(MessageEnum.DOCUMENT_ROD);
            else
                desc=messages.getMessage(MessageEnum.DOCUMENTS_ROD);
            this.openLookup(Employee.class,
                    e->{
                        Employee emp = (Employee) e.toArray()[0];
                        makeConfirmDialog(messages.getMessage(MessageEnum.DOCUMENT),messages.getMessage(MessageEnum.MAKE_ISSUE)+" "+desc+"?",()->{
                            doc.forEach((item)->documentService.issueDocument(item, emp));
                            refresh();
                        });
                    }, WindowManager.OpenType.DIALOG);

        }
        else{
            showNotification(messages.getMessage(MessageEnum.SELECT_IN_TABLE), NotificationType.TRAY);
        }
    }

    public void applyFilter(String hBoxId,CollectionDatasource<Tag, UUID> filteredTagsDs) {
        String query = makeQueryString(filteredTagsDs.getItemIds());
        switch (hBoxId){
            case "incomeHBox":
                query = query.replace(":statusCode",incomeStatus);
                documentsIncomeDs.setQuery(query);
                documentsIncomeDs.refresh();
                break;

            case "okHBox":
                query = query.replace(":statusCode",okStatus);
                documentsOkDs.setQuery(query);
                documentsOkDs.refresh();
                break;

            case "problemsHBox":
                query = query.replace(":statusCode",problemStatus);
                documentsProblemsDs.setQuery(query);
                documentsProblemsDs.refresh();
                break;

            case "issuedHBox":
                query = query.replace(":statusCode",issueStatus);
                documentsIssueDs.setQuery(query);
                documentsIssueDs.refresh();
                break;

        }
    }

    public void onReturn(Component source) {

        Set<Document> doc = issue.getSelected();
        if(doc.size()>0){
            String desc;
            if(doc.size()>1) desc=messages.getMessage(MessageEnum.DOCUMENT_ROD);
                 else desc=messages.getMessage(MessageEnum.DOCUMENTS_ROD);
            makeConfirmDialog(messages.getMessage(MessageEnum.DOCUMENT),messages.getMessage(MessageEnum.MAKE_RETURN)+" "+desc+"?",()->{
                doc.forEach((item)->documentService.returnDocument(item));
                refresh();
            });
        }
        else{
            showNotification(messages.getMessage(MessageEnum.SELECT_IN_TABLE), NotificationType.TRAY);
        }

    }

    private void makeConfirmDialog(String header,String content,SomeAction action){
        String capitalHeader= header.substring(0, 1).toUpperCase() + header.substring(1);
        String capitalContent= content.substring(0, 1).toUpperCase() + content.substring(1);
        showOptionDialog(
                capitalHeader,
                capitalContent ,
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                              action.call();
                            }
                        },
                        new DialogAction(DialogAction.Type.NO)
                }
        );
    }

    private void genTagFilterComponent(String mainHBoxId){
        final HBoxLayout mainHBox = (HBoxLayout)getComponentNN(mainHBoxId);
        CollectionDatasource<Tag, UUID> filteredTagsDs = new FilterTagsCollectionDatasource();

        HBoxLayout tagsContainer = componentsFactory.createComponent(HBoxLayout.class);
        tagsContainer.setSpacing(true);
        tagsContainer.setMargin(true);

        Label label = componentsFactory.createComponent(Label.class);
        label.setValue("Фильтрация по тегам");

        TokenList tokenList  = componentsFactory.createComponent(TokenList.class);
        tokenList.setClearEnabled(false);
        tokenList.setDatasource(filteredTagsDs);
        tokenList.setWidth("100%");
        tokenList.setOptionsDatasource(optionsTagsDs);
        tokenList.setLookup(false);

        Button button = componentsFactory.createComponent(Button.class);
        button.setCaption("Применить");
        button.setAction(new AbstractAction("approveClick") {
            @Override
            public void actionPerform(Component component) {
                applyFilter(mainHBoxId,filteredTagsDs);
            }
        });

        //Add top tags to tag container
        fillTopTags(mainHBox.getId(),tagsContainer,filteredTagsDs);

        mainHBox.add(label);
        mainHBox.add(tokenList);
        mainHBox.add(button);
        mainHBox.add(tagsContainer);
    }
}

interface SomeAction{
    void call();
}

