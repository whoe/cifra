create table CIFRA_CONTRAGENT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(50),
    FULL_NAME varchar(255),
    INN varchar(13),
    KPP varchar(13),
    --
    primary key (ID)
);
