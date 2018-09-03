-- alter table TASKS_TASK add column AUTHOR_ID uuid ^
-- update TASKS_TASK set AUTHOR_ID = <default_value> ;
-- alter table TASKS_TASK alter column AUTHOR_ID set not null ;
alter table TASKS_TASK add column AUTHOR_ID uuid not null ;
-- alter table TASKS_TASK add column PERFORMER_ID uuid ^
-- update TASKS_TASK set PERFORMER_ID = <default_value> ;
-- alter table TASKS_TASK alter column PERFORMER_ID set not null ;
alter table TASKS_TASK add column PERFORMER_ID uuid not null ;
