alter table CIFRA_CHECK_LIST add constraint FK_CIFRA_CHECK_LIST_ON_DOCUMENT foreign key (DOCUMENT_ID) references CIFRA_DOCUMENT(ID);
create index IDX_CIFRA_CHECK_LIST_ON_DOCUMENT on CIFRA_CHECK_LIST (DOCUMENT_ID);
