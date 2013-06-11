-- Create table
drop table R_ONLINE_INFO;
create table R_ONLINE_INFO
(
  logical_addr    VARCHAR2(20) not null,
  event_time    DATE not null,
  event_type    VARCHAR2(2),
  isCurrent     VARCHAR2(2),
  record_time     DATE default SYSDATE not null
);

-- Create/Recreate primary, unique and foreign key constraints 
alter table R_ONLINE_INFO
  add constraint UNI_LOGICADDR_RECORTIME unique (LOGICAL_ADDR, RECORD_TIME)
  using index 
  tablespace TAB_LOG
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
-- Grant/Revoke object privileges 
grant select on R_ONLINE_INFO to PSS_MANAGER;
