alter table CIFRA_DOCUMENT rename column step_name to step_name__u13009 ;
alter table CIFRA_DOCUMENT rename column status to status__u65892 ;
alter table CIFRA_DOCUMENT add column WF_STATUS integer ;
alter table CIFRA_DOCUMENT add column WF_STEP_NAME varchar(255) ;
