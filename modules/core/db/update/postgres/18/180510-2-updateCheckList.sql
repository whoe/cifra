-- alter table CIFRA_CHECK_LIST add column ITEM_ID uuid ^
-- update CIFRA_CHECK_LIST set ITEM_ID = <default_value> ;
-- alter table CIFRA_CHECK_LIST alter column ITEM_ID set not null ;
alter table CIFRA_CHECK_LIST add column ITEM_ID uuid not null ;
