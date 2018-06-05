alter table cifra_warehouse rename to CIFRA_WAREHOUSE__U83377 ;
alter table cifra_document drop constraint FK_CIFRA_DOCUMENT_WAREHOUSE ;
alter table cifra_journal drop constraint FK_CIFRA_JOURNAL_WAREHOUSE ;
