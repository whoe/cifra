create table CIFRA_COMPANY_DIVISON_USER_DIVISION_LINK (
    COMPANY_DIVISON_USER_ID uuid,
    DIVISION_ID uuid,
    primary key (COMPANY_DIVISON_USER_ID, DIVISION_ID)
);
