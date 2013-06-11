-- Create table
create table C_ACCESS_RECORD
(
  term_id    NUMBER not null,
  card_code  VARCHAR2(20),
  recordtime DATE not null,
  accesstype VARCHAR2(5),
  doornomark VARCHAR2(20)
)
tablespace TABS_ARCHIVE
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 128K
    next 1M
    minextents 1
    maxextents unlimited
  );
-- Add comments to the columns 
comment on column C_ACCESS_RECORD.term_id
  is '�����ն�ID';
comment on column C_ACCESS_RECORD.card_code
  is '�Ž�������';
comment on column C_ACCESS_RECORD.recordtime
  is '��ʼʱ��';
comment on column C_ACCESS_RECORD.accesstype
  is '�������ͣ�1��Զ�̿��ţ�2��������';
comment on column C_ACCESS_RECORD.doornomark
  is '�źű�ʶ';
-- Create/Recreate primary, unique and foreign key constraints 
alter table C_ACCESS_RECORD
  add constraint PK_C_ACCESS_RECORD primary key (TERM_ID, RECORDTIME)
  using index 
  tablespace TABS_ARCHIVE
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
