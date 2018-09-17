alter table SEC_USER add column GOOGLE_ID varchar(50);
alter table SEC_USER add column DTYPE varchar(100);
update SEC_USER set DTYPE = 'cifra$SocialUser' where DTYPE is null;