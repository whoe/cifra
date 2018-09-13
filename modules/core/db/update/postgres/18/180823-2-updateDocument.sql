alter table CIFRA_DOCUMENT add column STATUS integer ;
alter table CIFRA_DOCUMENT add column STEP_NAME varchar(255) ;
-- alter table CIFRA_DOCUMENT add column INITIATOR_ID uuid ^
-- update CIFRA_DOCUMENT set INITIATOR_ID = <default_value> ;
-- alter table CIFRA_DOCUMENT alter column INITIATOR_ID set not null ;
alter table CIFRA_DOCUMENT add column INITIATOR_ID uuid not null ;
