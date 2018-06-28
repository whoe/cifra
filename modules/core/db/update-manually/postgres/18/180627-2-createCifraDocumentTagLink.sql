alter table CIFRA_DOCUMENT_TAG_LINK add constraint FK_DOCTAG_ON_TAG foreign key (TAG_ID) references CIFRA_TAG(ID);
alter table CIFRA_DOCUMENT_TAG_LINK add constraint FK_DOCTAG_ON_DOCUMENT foreign key (DOCUMENT_ID) references CIFRA_DOCUMENT(ID);
