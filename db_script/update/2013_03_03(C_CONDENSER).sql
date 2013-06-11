-- Create table
create table C_CONDENSER
(
  cd_id                         NUMBER not null,
  term_id                       NUMBER,
  cd_no                         NUMBER,
  asset_no                      VARCHAR2(20),
  compen_way                    VARCHAR2(2),
  compen_phase_a                VARCHAR2(2),
  compen_phase_b                VARCHAR2(2),
  compen_phase_c                VARCHAR2(2),
  loading_capa                  NUMBER,
  target_pf                     NUMBER,
  in_reactive_power_lmt         NUMBER,
  resect_reactive_power_lmt     NUMBER,
  delay_time                    NUMBER,
  action_interval               NUMBER,
  overvoltage                   NUMBER,
  overvoltage_return_dif_value  NUMBER,
  undervoltage                  NUMBER,
  undervoltage_return_dif_value NUMBER,
  tdccr_limit                   NUMBER,
  tdccr_return_dif_value        NUMBER,
  tdvcr_limit                   NUMBER,
  tdvcr_return_dif_value        NUMBER,
  control_way                   VARCHAR2(2)
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
-- Add comments to the table 
comment on table C_CONDENSER
  is '������';
-- Add comments to the columns 
comment on column C_CONDENSER.cd_no
  is '����������ţ�1-16��';
comment on column C_CONDENSER.compen_way
  is '������ʽ : 1��2���α�ʾ�������ֲ�';
comment on column C_CONDENSER.loading_capa
  is '����װ������';
comment on column C_CONDENSER.target_pf
  is 'Ŀ�깦������';
comment on column C_CONDENSER.in_reactive_power_lmt
  is 'Ͷ���޹���������';
comment on column C_CONDENSER.resect_reactive_power_lmt
  is '�г��޹���������';
comment on column C_CONDENSER.delay_time
  is '��ʱʱ��';
comment on column C_CONDENSER.action_interval
  is '����ʱ����';
comment on column C_CONDENSER.overvoltage
  is '����ѹ';
comment on column C_CONDENSER.overvoltage_return_dif_value
  is '����ѹ�ز�ֵ';
comment on column C_CONDENSER.undervoltage
  is 'Ƿ��ѹ';
comment on column C_CONDENSER.undervoltage_return_dif_value
  is 'Ƿ��ѹ�ز�ֵ';
comment on column C_CONDENSER.tdccr_limit
  is '�ܻ����������������';
comment on column C_CONDENSER.tdccr_return_dif_value
  is '�ܻ������������Խ�޻ز�ֵ';
comment on column C_CONDENSER.tdvcr_limit
  is '�ܻ����ѹ����������';
comment on column C_CONDENSER.tdvcr_return_dif_value
  is '�ܻ����ѹ������Խ�޻ز�ֵ';
comment on column C_CONDENSER.control_way
  is '���Ʒ�ʽ';
-- Create/Recreate primary, unique and foreign key constraints 
alter table C_CONDENSER
  add constraint PK_C_CONDENSER primary key (CD_ID)
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
alter table C_CONDENSER
  add constraint FK_C_CONDENSER_GP_ID foreign key (CD_NO)
  references C_GP (GP_ID);
alter table C_CONDENSER
  add constraint FK_C_CONDENSER_TERMIN foreign key (TERM_ID)
  references C_TERMINAL (TERM_ID);
-- Grant/Revoke object privileges 
grant select on C_CONDENSER to PSS_MANAGER;
