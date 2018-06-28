create table CIFRA_DOCUMENT_TAG_LINK (
    TAG_ID uuid,
    DOCUMENT_ID uuid,
    primary key (TAG_ID, DOCUMENT_ID)
);
