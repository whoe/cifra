-- update CIFRA_DOCUMENT set DOC_TYPE_ID = <default_value> where DOC_TYPE_ID is null ;
alter table CIFRA_DOCUMENT alter column DOC_TYPE_ID set not null ;
