create table CIFRA_DOCUMENT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    DOC_TYPE integer not null,
    FILE_ID uuid,
    GOT_ORIGINAL boolean,
    DESCRIPTION varchar(255),
    NUMBER_ varchar(15) not null,
    DATE_ date not null,
    DATE_LOAD date not null,
    DOC_CAUSE_ID uuid,
    PROBLEMS text,
    FIX_DUE date,
    --
    primary key (ID)
);
