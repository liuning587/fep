-- Create table
create table D_PS_CONTROL_PARA
(
  gp_id                     NUMBER not null,
  data_time                 DATE not null,
  isremote                  NUMBER(1),
  data_alarm                NUMBER(1),
  alarm_lighting            NUMBER(1),
  alarm_sound               NUMBER(1),
  trial_jump                NUMBER(1),
  gears_return              NUMBER(1),
  reclose                   NUMBER(1),
  low_volt_data_alarm       NUMBER(1),
  low_volt_trip_control     NUMBER(1),
  over_volt_data_alarm      NUMBER(1),
  over_volt_trip_control    NUMBER(1),
  phase_loss_data_alarm     NUMBER(1),
  phase_loss_trip_control   NUMBER(1),
  over_current_data_alarm   NUMBER(1),
  over_current_trip_control NUMBER(1),
  zero_loss_data_alarm      NUMBER(1),
  zero_loss_trip_control    NUMBER(1),
  trial_jump_source         NUMBER(1),
  rated_residual_current_ii VARCHAR2(2),
  rated_residual_current_i  VARCHAR2(2),
  leak_elec_alarm_time      VARCHAR2(2),
  limit_not_driver_time     VARCHAR2(2)
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
  is '������ID';
comment on column D_PS_CONTROL_PARA.isremote
  is '�ֳ�/Զ��ѡ��0-Զ��1-�ֳ�';
comment on column D_PS_CONTROL_PARA.data_alarm
  is '���ݸ澯:0-ȫ��ֹ,1-ȫ����';
comment on column D_PS_CONTROL_PARA.alarm_lighting
  is '�����ƹ�:0�C��ֹ,1�C����';
comment on column D_PS_CONTROL_PARA.alarm_sound
  is '��������:0�C��ֹ,1�C����';
comment on column D_PS_CONTROL_PARA.trial_jump
  is '��ʱ����:0 �C ��ֹ,1 �C ����';
comment on column D_PS_CONTROL_PARA.gears_return
  is '��λ����:0-����,1-��ֹ';
comment on column D_PS_CONTROL_PARA.reclose
  is '�غ�բ:0-����,1-��ֹ';
comment on column D_PS_CONTROL_PARA.low_volt_data_alarm
  is 'Ƿѹ����,���ݸ澯:0-��ֹ,1-����';
comment on column D_PS_CONTROL_PARA.low_volt_trip_control
  is 'Ƿѹ����,��բ����:0-��ֹ,1-����';
comment on column D_PS_CONTROL_PARA.over_volt_data_alarm
  is '��ѹ����,���ݸ澯:0-��ֹ,1-����';
comment on column D_PS_CONTROL_PARA.over_volt_trip_control
  is '��ѹ����,��բ����:0-��ֹ,1-����';
comment on column D_PS_CONTROL_PARA.phase_loss_data_alarm
  is 'ȱ�ౣ��,���ݸ澯:0-��ֹ,1-����';
comment on column D_PS_CONTROL_PARA.phase_loss_trip_control
  is 'ȱ�ౣ��,��բ����:0-��ֹ,1-����';
comment on column D_PS_CONTROL_PARA.over_current_data_alarm
  is '��������,���ݸ澯:0-��ֹ,1-����';
comment on column D_PS_CONTROL_PARA.over_current_trip_control
  is '��������,��բ����:0-��ֹ,1-����';
comment on column D_PS_CONTROL_PARA.zero_loss_data_alarm
  is 'ȱ�㱣��,���ݸ澯:0-��ֹ,1-����';
comment on column D_PS_CONTROL_PARA.zero_loss_trip_control
  is 'ȱ�㱣��,��բ����:0-��ֹ,1-����';
comment on column D_PS_CONTROL_PARA.trial_jump_source
  is '����Դ:0-�ڲ�,1-�ⲿ';
comment on column D_PS_CONTROL_PARA.rated_residual_current_ii
  is 'II���ʣ�������00 - �͵�,01 - �е�,10 - �ߵ�,11 - ����';
comment on column D_PS_CONTROL_PARA.rated_residual_current_i
  is 'I���ʣ�������00 �C �͵�,01 - �е�,10 - �ߵ�,11 - ����';
comment on column D_PS_CONTROL_PARA.leak_elec_alarm_time
  is '���޲�����ʱ�䣺00 �C �͵�,01 - �е�,10 - �ߵ�,11 - ����';
comment on column D_PS_CONTROL_PARA.limit_not_driver_time
  is '©�籨��ʱ�䣺00-©�籨�����ܹر�,01-©�籨����������24Сʱ,10-©�籨�����ܳ�������,11-����';
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
