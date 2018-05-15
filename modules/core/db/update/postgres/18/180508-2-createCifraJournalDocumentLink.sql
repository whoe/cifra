alter table CIFRA_JOURNAL_DOCUMENT_LINK add constraint FK_JOUDOC_JOURNAL foreign key (JOURNAL_ID) references CIFRA_JOURNAL(ID);
alter table CIFRA_JOURNAL_DOCUMENT_LINK add constraint FK_JOUDOC_DOCUMENT foreign key (DOCUMENT_ID) references CIFRA_DOCUMENT(ID);
