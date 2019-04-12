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
    WAREHOUSE_ID uuid,
    CELL varchar(50),
    CONTRAGENT_ID uuid,
    COMPANY_ID uuid not null,
    DIVISION_ID uuid,
    DOC_TYPE_ID uuid not null,
    DIRECTION varchar(50) not null,
    FILE_ID uuid,
    GOT_ORIGINAL boolean,
    DESCRIPTION varchar(255),
    NUMBER_ varchar(15),
    DATE_ date not null,
    DATE_LOAD date,
    DOC_CAUSE_ID uuid,
    PROBLEMS text,
    FIX_DUE date,
    EXTERNAL_LINK varchar(255),
    EXTERNAL_ID varchar(255),
    WF_STATUS integer,
    WF_STEP_NAME varchar(255),
    WORKFLOW_ID uuid,
    WF_INITIATOR_ID uuid,
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
    DOCUMENT_ID uuid not null,
    ITEM_ID uuid not null,
    CHECKED boolean,
    COMMENT_ varchar(255),
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
    PREVIOUS_STATUS integer,
    EVENT_TYPE integer not null,
    RESPONSIBLE_ID uuid,
    CELL varchar(255),
    WAREHOUSE_ID uuid,
    HOLDER_ID uuid,
    --
    primary key (ID)
)^
-- end CIFRA_JOURNAL
-- begin CIFRA_WAREHOUSE
create table CIFRA_WAREHOUSE (
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
    --
    primary key (ID)
)^
-- end CIFRA_WAREHOUSE
-- begin CIFRA_TAG
create table CIFRA_TAG (
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
-- end CIFRA_TAG
-- begin TASKS_TASK
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
)^
-- end TASKS_TASK
-- begin TASKS_TASK_TEMPLATE
create table TASKS_TASK_TEMPLATE (
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
    DESCRIPTION varchar(255),
    --
    primary key (ID)
)^
-- end TASKS_TASK_TEMPLATE
-- begin TASKS_TASK_TYPICAL
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
    INTERVAL_TYPE integer not null,
    --
    primary key (ID)
)^
-- end TASKS_TASK_TYPICAL
-- begin TEMPLATE_TASK_TYPICAL_LINK
create table TEMPLATE_TASK_TYPICAL_LINK (
    TASK_TEMPLATE_ID uuid,
    TASK_TYPICAL_ID uuid,
    primary key (TASK_TEMPLATE_ID, TASK_TYPICAL_ID)
)^
-- end TEMPLATE_TASK_TYPICAL_LINK
-- begin CIFRA_DOCUMENT_TAG_LINK
create table CIFRA_DOCUMENT_TAG_LINK (
    TAG_ID uuid,
    DOCUMENT_ID uuid,
    primary key (TAG_ID, DOCUMENT_ID)
)^
-- end CIFRA_DOCUMENT_TAG_LINK
-- begin CIFRA_JOURNAL_DOCUMENT_LINK
create table CIFRA_JOURNAL_DOCUMENT_LINK (
    JOURNAL_ID uuid,
    DOCUMENT_ID uuid,
    primary key (JOURNAL_ID, DOCUMENT_ID)
)^
-- end CIFRA_JOURNAL_DOCUMENT_LINK
-- begin CIFRA_COMPANY_COMPANY_DIVISON_USER_LINK
create table CIFRA_COMPANY_DIVISON_USER_COMPANY_LINK (
    COMPANY_DIVISON_USER_ID uuid,
    COMPANY_ID uuid,
    primary key (COMPANY_DIVISON_USER_ID, COMPANY_ID)
)^
-- end CIFRA_COMPANY_COMPANY_DIVISON_USER_LINK
-- begin CIFRA_COMPANY_DIVISON_USER_DIVISION_LINK
create table CIFRA_COMPANY_DIVISON_USER_DIVISION_LINK (
    COMPANY_DIVISON_USER_ID uuid,
    DIVISION_ID uuid,
    primary key (COMPANY_DIVISON_USER_ID, DIVISION_ID)
)^
-- end CIFRA_COMPANY_DIVISON_USER_DIVISION_LINK
-- begin SEC_USER
alter table SEC_USER add column DTYPE varchar(100) ^
update SEC_USER set DTYPE = 'cifra$CompanyDivisionUser' where DTYPE is null ^
-- end SEC_USER
