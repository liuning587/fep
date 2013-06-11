-- Create table
create table R_UPGRADE_FILE
(
  file_id       NUMBER not null,
  file_version  VARCHAR2(20),
  file_name     VARCHAR2(50),
  binfile       BLOB,
  post_time     DATE default SYSDATE
)
tablespace TABS_INTERACTION
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 2M
    next 1M
    minextents 1
    maxextents unlimited
  );
-- Add comments to the columns 
comment on column R_UPGRADE_FILE.binfile
  is '升级的二进制文件';
comment on column R_UPGRADE_FILE.file_version
  is '升级文件的版本号';
-- Create/Recreate primary, unique and foreign key constraints 
alter table R_UPGRADE_FILE
  add constraint PK_R_UPGRADE_FILE primary key (file_id)
  using index 
  tablespace TABS_INTERACTION
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 192K
    next 1M
    minextents 1
    maxextents unlimited
  );
-- Grant/Revoke object privileges 
grant select on R_UPGRADE_FILE to PSS_MANAGER;


-- Create sequence 
create sequence SEQ_UPGRADE_FILE_ID
minvalue 1
maxvalue 9999999999
start with 1
increment by 1
cache 20;
