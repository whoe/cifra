alter table CIFRA_DOCUMENT add constraint FK_CIFRA_DOCUMENT_ON_FILE foreign key (FILE_ID) references SYS_FILE(ID);
create index IDX_CIFRA_DOCUMENT_ON_FILE on CIFRA_DOCUMENT (FILE_ID);
