alter table CIFRA_EMPLOYEE add constraint FK_CIFRA_EMPLOYEE_USER foreign key (USER_ID) references SEC_USER(ID);
create unique index IDX_CIFRA_EMPLOYEE_UK_USER_ID on CIFRA_EMPLOYEE (USER_ID) where DELETE_TS is null ;
create index IDX_CIFRA_EMPLOYEE_USER on CIFRA_EMPLOYEE (USER_ID);
