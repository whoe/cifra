create table CIFRA_JOURNAL (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    EVENT_TYPE integer not null,
    RESPONSIBLE_ID uuid,
    ADDRESS varchar(255),
    HOLDER_ID uuid,
    --
    primary key (ID)
);
