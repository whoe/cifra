alter table CIFRA_CHECK_LIST add constraint FK_CIFRA_CHECK_LIST_ON_ITEM foreign key (ITEM_ID) references CIFRA_CHECK_LIST_ITEMS(ID);
create index IDX_CIFRA_CHECK_LIST_ON_ITEM on CIFRA_CHECK_LIST (ITEM_ID);
