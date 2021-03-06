alter table TASKS_TASK add constraint FK_TASKS_TASK_ON_TASK_TYPICAL foreign key (TASK_TYPICAL_ID) references TASKS_TASK_TYPICAL(ID);
alter table TASKS_TASK add constraint FK_TASKS_TASK_ON_AUTHOR foreign key (AUTHOR_ID) references CIFRA_EMPLOYEE(ID);
alter table TASKS_TASK add constraint FK_TASKS_TASK_ON_PERFORMER foreign key (PERFORMER_ID) references CIFRA_EMPLOYEE(ID);
alter table TASKS_TASK add constraint FK_TASKS_TASK_ON_DOCUMENT foreign key (DOCUMENT_ID) references CIFRA_DOCUMENT(ID);
create index IDX_TASKS_TASK_ON_TASK_TYPICAL on TASKS_TASK (TASK_TYPICAL_ID);
create index IDX_TASKS_TASK_ON_AUTHOR on TASKS_TASK (AUTHOR_ID);
create index IDX_TASKS_TASK_ON_PERFORMER on TASKS_TASK (PERFORMER_ID);
create index IDX_TASKS_TASK_ON_DOCUMENT on TASKS_TASK (DOCUMENT_ID);
