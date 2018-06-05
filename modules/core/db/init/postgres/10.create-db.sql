-- begin CIFRA_CHECK_LIST_ITEMS
create table CIFRA_CHECK_LIST_ITEMS (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ITEM varchar(255) not null,
    DOC_TYPE_ID uuid,
    --
    primary key (ID)
)^
-- end CIFRA_CHECK_LIST_ITEMS
-- begin CIFRA_DOC_TYPE
create table CIFRA_DOC_TYPE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(100) not null,
    --
    primary key (ID)
)^
-- end CIFRA_DOC_TYPE
-- begin CIFRA_DOCUMENT
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
    DOC_STATUS integer not null,
    WAREHOUSE_ID uuid,
    CELL varchar(50),
    CONTRAGENT_ID uuid,
    COMPANY_ID uuid not null,
    DIVISION_ID uuid,
    DOC_TYPE_ID uuid not null,
    FILE_ID uuid,
    GOT_ORIGINAL boolean,
    DESCRIPTION varchar(255),
    NUMBER_ varchar(15) not null,
    DATE_ date not null,
    DATE_LOAD date,
    DOC_CAUSE_ID uuid,
    PROBLEMS text,
    FIX_DUE date,
    --
    primary key (ID)
)^
-- end CIFRA_DOCUMENT
-- begin CIFRA_EMPLOYEE
create table CIFRA_EMPLOYEE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    USER_ID uuid,
    NAME varchar(55),
    --
    primary key (ID)
)^
-- end CIFRA_EMPLOYEE
-- begin CIFRA_COMPANY
create table CIFRA_COMPANY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(10) not null,
    INN varchar(13),
    FULL_NAME varchar(255),
    --
    primary key (ID)
)^
-- end CIFRA_COMPANY
-- begin CIFRA_DIVISION
create table CIFRA_DIVISION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    COMPANY_ID uuid not null,
    NAME varchar(255),
    --
    primary key (ID)
)^
-- end CIFRA_DIVISION
-- begin CIFRA_CONTRAGENT
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
)^
-- end CIFRA_CONTRAGENT
-- begin CIFRA_CHECK_LIST
create table CIFRA_CHECK_LIST (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ITEM_ID uuid not null,
    CHECKED boolean,
    COMMENT_ varchar(255),
    DOCUMENT_ID uuid not null,
    --
    primary key (ID)
)^
-- end CIFRA_CHECK_LIST
-- begin CIFRA_JOURNAL
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
    CELL varchar(255),
    WAREHOUSE_ID uuid,
    HOLDER_ID uuid,
    --
    primary key (ID)
)^
-- end CIFRA_JOURNAL
-- begin CIFRA_JOURNAL_DOCUMENT_LINK
create table CIFRA_JOURNAL_DOCUMENT_LINK (
    JOURNAL_ID uuid,
    DOCUMENT_ID uuid,
    primary key (JOURNAL_ID, DOCUMENT_ID)
)^
-- end CIFRA_JOURNAL_DOCUMENT_LINK
