-- begin CIFRA_CHECK_LIST_ITEMS
create table CIFRA_CHECK_LIST_ITEMS (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ITEM varchar(255) not null,
    DOC_TYPE_ID varchar(36),
    --
    primary key (ID)
)^
-- end CIFRA_CHECK_LIST_ITEMS
-- begin CIFRA_DOC_TYPE
create table CIFRA_DOC_TYPE (
    ID varchar(36) not null,
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
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    DOC_STATUS integer not null,
    WAREHOUSE_ID varchar(36),
    CELL varchar(50),
    CONTRAGENT_ID varchar(36),
    COMPANY_ID varchar(36) not null,
    DIVISION_ID varchar(36),
    DOC_TYPE_ID varchar(36) not null,
    DIRECTION varchar(50) not null,
    FILE_ID varchar(36),
    GOT_ORIGINAL boolean,
    DESCRIPTION varchar(255),
    NUMBER_ varchar(15),
    DATE_ date not null,
    DATE_LOAD date,
    DOC_CAUSE_ID varchar(36),
    PROBLEMS longvarchar,
    FIX_DUE date,
    EXTERNAL_LINK varchar(255),
    EXTERNAL_ID varchar(255),
    STATUS integer,
    STEP_NAME varchar(255),
    INITIATOR_ID varchar(36) not null,
    --
    primary key (ID)
)^
-- end CIFRA_DOCUMENT
-- begin CIFRA_EMPLOYEE
create table CIFRA_EMPLOYEE (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    USER_ID varchar(36),
    NAME varchar(55),
    --
    primary key (ID)
)^
-- end CIFRA_EMPLOYEE
-- begin CIFRA_COMPANY
create table CIFRA_COMPANY (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(150) not null,
    INN varchar(13),
    FULL_NAME varchar(255),
    EXTERNAL_ID varchar(255),
    --
    primary key (ID)
)^
-- end CIFRA_COMPANY
-- begin CIFRA_DIVISION
create table CIFRA_DIVISION (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    COMPANY_ID varchar(36) not null,
    NAME varchar(255),
    --
    primary key (ID)
)^
-- end CIFRA_DIVISION
-- begin CIFRA_CONTRAGENT
create table CIFRA_CONTRAGENT (
    ID varchar(36) not null,
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
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    DOCUMENT_ID varchar(36) not null,
    ITEM_ID varchar(36) not null,
    CHECKED boolean,
    COMMENT_ varchar(255),
    --
    primary key (ID)
)^
-- end CIFRA_CHECK_LIST
-- begin CIFRA_JOURNAL
create table CIFRA_JOURNAL (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PREVIOUS_STATUS integer,
    EVENT_TYPE integer not null,
    RESPONSIBLE_ID varchar(36),
    CELL varchar(255),
    WAREHOUSE_ID varchar(36),
    HOLDER_ID varchar(36),
    --
    primary key (ID)
)^
-- end CIFRA_JOURNAL
-- begin CIFRA_WAREHOUSE
create table CIFRA_WAREHOUSE (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(50),
    --
    primary key (ID)
)^
-- end CIFRA_WAREHOUSE
-- begin CIFRA_TAG
create table CIFRA_TAG (
    ID varchar(36) not null,
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
-- end CIFRA_TAG
-- begin CIFRA_DOCUMENT_TAG_LINK
create table CIFRA_DOCUMENT_TAG_LINK (
    TAG_ID varchar(36) not null,
    DOCUMENT_ID varchar(36) not null,
    primary key (TAG_ID, DOCUMENT_ID)
)^
-- end CIFRA_DOCUMENT_TAG_LINK
-- begin CIFRA_JOURNAL_DOCUMENT_LINK
create table CIFRA_JOURNAL_DOCUMENT_LINK (
    JOURNAL_ID varchar(36) not null,
    DOCUMENT_ID varchar(36) not null,
    primary key (JOURNAL_ID, DOCUMENT_ID)
)^
-- end CIFRA_JOURNAL_DOCUMENT_LINK
