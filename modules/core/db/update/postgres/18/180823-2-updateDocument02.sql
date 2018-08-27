alter table CIFRA_DOCUMENT rename column initiator_id to initiator_id__u14851 ;
alter table CIFRA_DOCUMENT alter column initiator_id__u14851 drop not null ;
drop index IDX_CIFRA_DOCUMENT_ON_INITIATOR ;
alter table CIFRA_DOCUMENT drop constraint FK_CIFRA_DOCUMENT_ON_INITIATOR ;
alter table CIFRA_DOCUMENT add column WF_INITIATOR_ID uuid ;
