-- Create table
create table R_UPGRADE_TASK
(
  task_id       NUMBER not null,
  sequence_code NUMBER not null,
  logical_addr  VARCHAR2(20),
  FILE_ID       NUMBER,
  post_time     DATE default SYSDATE,
  task_status   VARCHAR2(5) default '0',
  schedule      NUMBER default 0,
  failFrameNo   NUMBER default 0,
  valid         VARCHAR2(5) default '1'
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
comment on column R_UPGRADE_TASK.task_status
  is '����״̬��0��δ����1�������У�2��ִ�гɹ���3��ִ��ʧ��';
comment on column R_UPGRADE_TASK.FILE_ID
  is '�����Ķ������ļ�ID';
comment on column R_UPGRADE_TASK.schedule
  is '��ǰ�������ȣ�%';
comment on column R_UPGRADE_TASK.failFrameNo
  is '����ʧ�ܵ�֡���';
comment on column R_UPGRADE_TASK.valid
  is '���������Ƿ���Ч���Ƿ�ִ���Զ���������';
-- Create/Recreate primary, unique and foreign key constraints 
alter table R_UPGRADE_TASK
  add constraint PK_R_UPGRADE_TASK primary key (TASK_ID)
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
grant select on R_UPGRADE_TASK to PSS_MANAGER;
