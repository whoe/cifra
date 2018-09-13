alter table CIFRA_EMPLOYEE add constraint FK_CIFRA_EMPLOYEE_ON_USER foreign key (USER_ID) references SEC_USER(ID);
create index IDX_CIFRA_EMPLOYEE_ON_USER on CIFRA_EMPLOYEE (USER_ID);
