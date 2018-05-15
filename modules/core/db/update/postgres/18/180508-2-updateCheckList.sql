-- alter table CIFRA_CHECK_LIST add column DOCUMENT_ID uuid ^
-- update CIFRA_CHECK_LIST set DOCUMENT_ID = <default_value> ;
-- alter table CIFRA_CHECK_LIST alter column DOCUMENT_ID set not null ;
alter table CIFRA_CHECK_LIST add column DOCUMENT_ID uuid not null ;
