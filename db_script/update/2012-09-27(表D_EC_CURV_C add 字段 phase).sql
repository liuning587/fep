alter table D_EC_CURV_C 
add PHASE VARCHAR2(10) default '00';

-- Add comments to the columns 
comment on column D_EC_CURV_C.PHASE
  is '��λ��00-δ֪ ��01-A�࣬10-B�࣬11-C��';
