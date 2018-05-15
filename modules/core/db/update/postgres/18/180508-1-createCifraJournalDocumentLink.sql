create table CIFRA_JOURNAL_DOCUMENT_LINK (
    JOURNAL_ID uuid,
    DOCUMENT_ID uuid,
    primary key (JOURNAL_ID, DOCUMENT_ID)
);
