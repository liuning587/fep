-- Create table
create table D_PS_CONTROL_PARA
(
  gp_id       NUMBER not null,
  isremote    NUMBER(1),
  data_alarm  NUMBER(1),
  alarm_lighting  NUMBER(1),
  alarm_sound NUMBER(1),
  trial_jump  NUMBER(1),
  gears_return  NUMBER(1),
  reclose NUMBER(1),
  low_volt_data_alarm NUMBER(1),
  low_volt_trip_control NUMBER(1),
  over_volt_data_alarm NUMBER(1),
  over_volt_trip_control NUMBER(1),
  phase_loss_data_alarm NUMBER(1),
  phase_loss_trip_control NUMBER(1),
  over_current_data_alarm NUMBER(1),
  over_current_trip_control NUMBER(1),
  zero_loss_data_alarm NUMBER(1),
  zero_loss_trip_control NUMBER(1),
  trial_jump_source  NUMBER(1),
  rated_residual_current_II NUMBER(1),
  rated_residual_current_I NUMBER(1),
  leak_elec_alarm_time NUMBER(1),
  limit_not_driver_time NUMBER(1)
)
tablespace TABS_DATA
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 88M
    next 16K
    minextents 1
    maxextents unlimited
  );
-- Add comments to the columns 
comment on column D_PS_CONTROL_PARA.gp_id
  is '测量点ID';
comment on column D_PS_CONTROL_PARA.isremote
  is '现场/远程选择：0-远程1-现场';
comment on column D_PS_CONTROL_PARA.data_alarm
  is '数据告警:0-全禁止,1-全允许';
comment on column D_PS_CONTROL_PARA.alarm_lighting
  is '报警灯光:0–禁止,1–允许';
comment on column D_PS_CONTROL_PARA.alarm_sound
  is '报警声音:0–禁止,1–允许';
comment on column D_PS_CONTROL_PARA.trial_jump
  is '定时试跳:0 – 禁止,1 – 允许';
comment on column D_PS_CONTROL_PARA.gears_return
  is '档位返回:0-允许,1-禁止';
comment on column D_PS_CONTROL_PARA.reclose
  is '重合闸:0-允许,1-禁止';
comment on column D_PS_CONTROL_PARA.low_volt_data_alarm
  is '欠压保护,数据告警:0-禁止,1-允许';
comment on column D_PS_CONTROL_PARA.low_volt_trip_control
  is '欠压保护,跳闸控制:0-禁止,1-允许';
comment on column D_PS_CONTROL_PARA.over_volt_data_alarm
  is '过压保护,数据告警:0-禁止,1-允许';
comment on column D_PS_CONTROL_PARA.over_volt_trip_control
  is '过压保护,跳闸控制:0-禁止,1-允许';
  
  comment on column D_PS_CONTROL_PARA.phase_loss_data_alarm
  is '缺相保护,数据告警:0-禁止,1-允许';
comment on column D_PS_CONTROL_PARA.phase_loss_trip_control
  is '缺相保护,跳闸控制:0-禁止,1-允许';
  comment on column D_PS_CONTROL_PARA.over_current_data_alarm
  is '过流保护,数据告警:0-禁止,1-允许';
comment on column D_PS_CONTROL_PARA.over_current_trip_control
  is '过流保护,跳闸控制:0-禁止,1-允许';
  
comment on column D_PS_CONTROL_PARA.zero_loss_data_alarm
  is '缺零保护,数据告警:0-禁止,1-允许';
comment on column D_PS_CONTROL_PARA.zero_loss_trip_control
  is '缺零保护,跳闸控制:0-禁止,1-允许';

comment on column D_PS_CONTROL_PARA.trial_jump_source
  is '试跳源:0-内部,1-外部';
comment on column D_PS_CONTROL_PARA.rated_residual_current_II
  is 'II档额定剩余电流：00 - 低档,01 - 中档,10 - 高档,11 - 保留';
comment on column D_PS_CONTROL_PARA.rated_residual_current_I
  is 'I档额定剩余电流：00 – 低档,01 - 中档,10 - 高档,11 - 保留';
comment on column D_PS_CONTROL_PARA.leak_elec_alarm_time
  is '极限不驱动时间：00 – 低档,01 - 中档,10 - 高档,11 - 保留';
comment on column D_PS_CONTROL_PARA.limit_not_driver_time
  is '漏电报警时间：00-漏电报警功能关闭,01-漏电报警功能启用24小时,10-漏电报警功能长期启用,11-保留';
-- Create/Recreate primary, unique and foreign key constraints 
alter table D_PS_CONTROL_PARA
  add constraint PK_D_PS_CONTROL_PARA primary key (GP_ID)
  using index 
  tablespace TABS_DATA
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 88M
    next 1M
    minextents 1
    maxextents unlimited
  );
-- Grant/Revoke object privileges 
grant select on D_PS_STATUS to PSS_MANAGER;
