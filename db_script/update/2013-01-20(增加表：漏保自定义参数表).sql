-- Create table
create table D_PS_CUSTOM_PARA
(
  gp_id       NUMBER not null,
  data_time   DATE not null,
  over_volt_limit_value    NUMBER(10),
  low_volt_limit_value     NUMBER(10),
  lose_phase_limit_value   NUMBER(10),
  rated_current_limit_value  NUMBER(10),
  rated_current_gears1  NUMBER(10),
  rated_current_gears2  NUMBER(10),
  rated_current_gears3  NUMBER(10),
  rated_current_gears4  NUMBER(10),
  rated_current_gears5  NUMBER(10),
  limit_not_driver_time_gears1 NUMBER(10),
  limit_not_driver_time_gears2 NUMBER(10),
  limit_not_driver_time_gears3 NUMBER(10)
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
comment on column D_PS_CUSTOM_PARA.gp_id
  is '测量点ID';
comment on column D_PS_CUSTOM_PARA.over_volt_limit_value
  is '过压超限值';
comment on column D_PS_CUSTOM_PARA.low_volt_limit_value
  is '欠压超限值';
comment on column D_PS_CUSTOM_PARA.lose_phase_limit_value
  is '缺相超限值';
comment on column D_PS_CUSTOM_PARA.rated_current_limit_value
  is '额定电流值';
comment on column D_PS_CUSTOM_PARA.rated_current_gears1
  is '第一档剩余电流档位值（毫安）';
comment on column D_PS_CUSTOM_PARA.rated_current_gears2
  is '第二档剩余电流档位值（毫安）';
comment on column D_PS_CUSTOM_PARA.rated_current_gears3
  is '第三档剩余电流档位值（毫安）';
comment on column D_PS_CUSTOM_PARA.rated_current_gears4
  is '第四档剩余电流档位值（毫安）';
comment on column D_PS_CUSTOM_PARA.rated_current_gears5
  is '第五档剩余电流档位值（毫安）';
comment on column D_PS_CUSTOM_PARA.limit_not_driver_time_gears1
  is '第一档不驱动时间（毫秒）';
comment on column D_PS_CUSTOM_PARA.limit_not_driver_time_gears2
  is '第二档不驱动时间（毫秒）';
comment on column D_PS_CUSTOM_PARA.limit_not_driver_time_gears3
  is '第三档不驱动时间（毫秒）';

alter table D_PS_CUSTOM_PARA
  add constraint PK_D_PS_CUSTOM_PARA primary key (GP_ID)
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
grant select on D_PS_CUSTOM_PARA to PSS_MANAGER;
