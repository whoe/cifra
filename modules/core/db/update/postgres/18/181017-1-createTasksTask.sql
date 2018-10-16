create table TASKS_TASK (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TASK_TYPICAL_ID uuid not null,
    START_DATE date,
    END_DATE date,
    STATUS integer not null,
    CONTROL boolean,
    AUTHOR_ID uuid not null,
    PERFORMER_ID uuid not null,
    COMMENT_ text,
    DOCUMENT_ID uuid not null,
    SMARTSHEET_ID bigint,
    --
    primary key (ID)
);
