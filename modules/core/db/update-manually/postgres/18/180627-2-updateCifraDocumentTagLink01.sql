alter table CIFRA_DOCUMENT_TAG_LINK add constraint FK_CIFRA_DOCUMENT_TAG_LINK_ON_TAG foreign key (TAG_ID) references CIFRA_TAG(ID);
