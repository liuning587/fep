alter table D_EC_CURV_C 
add PHASE VARCHAR2(10) default '00';

-- Add comments to the columns 
comment on column D_EC_CURV_C.PHASE
  is '相位：00-未知 ，01-A相，10-B相，11-C相';
