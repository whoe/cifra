update TASKS_TASK_TYPICAL set INTERVAL_TYPE = 10 where INTERVAL_TYPE is null ;
alter table TASKS_TASK_TYPICAL alter column INTERVAL_TYPE set not null ;
