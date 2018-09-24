alter table CIFRA_DOCUMENT add column DIRECTION varchar(50) ^
update CIFRA_DOCUMENT set DIRECTION = 'INCOME' where DIRECTION is null ;
alter table CIFRA_DOCUMENT alter column DIRECTION set not null ;
