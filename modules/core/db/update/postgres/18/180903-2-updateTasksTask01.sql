alter table TASKS_TASK add constraint FK_TASKS_TASK_ON_AUTHOR foreign key (AUTHOR_ID) references CIFRA_EMPLOYEE(ID);
create index IDX_TASKS_TASK_ON_AUTHOR on TASKS_TASK (AUTHOR_ID);
