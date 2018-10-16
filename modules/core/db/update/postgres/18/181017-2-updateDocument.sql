alter table CIFRA_DOCUMENT rename column doc_status to doc_status__u27669 ;
alter table CIFRA_DOCUMENT alter column doc_status__u27669 drop not null ;
alter table CIFRA_DOCUMENT add column DIRECTION varchar(50) ^
update CIFRA_DOCUMENT set DIRECTION = 'INCOME' where DIRECTION is null ;
alter table CIFRA_DOCUMENT alter column DIRECTION set not null ;
alter table CIFRA_DOCUMENT add column WF_STATUS integer ;
alter table CIFRA_DOCUMENT add column WF_STEP_NAME varchar(255) ;
alter table CIFRA_DOCUMENT add column WF_INITIATOR_ID uuid ;
