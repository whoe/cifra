alter table CIFRA_DOCUMENT rename column doc_type to doc_type__u73459 ;
alter table CIFRA_DOCUMENT alter column doc_type__u73459 drop not null ;
alter table CIFRA_DOCUMENT add column DOC_STATUS integer ^
update CIFRA_DOCUMENT set DOC_STATUS = 10 where DOC_STATUS is null ;
alter table CIFRA_DOCUMENT alter column DOC_STATUS set not null ;
alter table CIFRA_DOCUMENT add column DOC_TYPE_ID uuid ;
