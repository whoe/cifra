create table TASKS_TASK_TYPICAL (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(50) not null,
    DESCRIPTION text not null,
    INTERVAL_ integer,
    INTERVAL_TYPE integer,
    --
    primary key (ID)
);
