--增加故障参数字段
alter table e_loubao_event 
add EventPara NUMBER(10) default 0 ;

-- Add comments to the columns 
comment on column e_loubao_event.EventPara
  is '故障参数: 漏电、试验 ---  剩余电流值（mA）||过载、短路 -- 工作电流||过压、欠压 -- 工作电压';
