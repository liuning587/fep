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
  is '电容器';
-- Add comments to the columns 
comment on column C_CONDENSER.cd_no
  is '电容器组序号（1-16）';
comment on column C_CONDENSER.compen_way
  is '补偿方式 : 1～2依次表示共补、分补';
comment on column C_CONDENSER.loading_capa
  is '电容装接容量';
comment on column C_CONDENSER.target_pf
  is '目标功率因数';
comment on column C_CONDENSER.in_reactive_power_lmt
  is '投入无功功率门限';
comment on column C_CONDENSER.resect_reactive_power_lmt
  is '切除无功功率门限';
comment on column C_CONDENSER.delay_time
  is '延时时间';
comment on column C_CONDENSER.action_interval
  is '动作时间间隔';
comment on column C_CONDENSER.overvoltage
  is '过电压';
comment on column C_CONDENSER.overvoltage_return_dif_value
  is '过电压回差值';
comment on column C_CONDENSER.undervoltage
  is '欠电压';
comment on column C_CONDENSER.undervoltage_return_dif_value
  is '欠电压回差值';
comment on column C_CONDENSER.tdccr_limit
  is '总畸变电流含有率上限';
comment on column C_CONDENSER.tdccr_return_dif_value
  is '总畸变电流含有率越限回差值';
comment on column C_CONDENSER.tdvcr_limit
  is '总畸变电压含有率上限';
comment on column C_CONDENSER.tdvcr_return_dif_value
  is '总畸变电压含有率越限回差值';
comment on column C_CONDENSER.control_way
  is '控制方式';
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
