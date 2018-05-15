-- update CIFRA_DOCUMENT set COMPANY_ID = <default_value> where COMPANY_ID is null ;
alter table CIFRA_DOCUMENT alter column COMPANY_ID set not null ;
