alter table e_term_ecode
add EX_TYPE varchar2(2) default '0';

comment on column e_term_ecode.EX_TYPE
  is '异常类型 0：终端事件 1：漏保事件';
  
insert into e_term_ecode(ex_code,ex_name,ex_remark,ex_type)
values('36','漏电保护动作(乾龙规约)','','1');
insert into e_term_ecode(ex_code,ex_name,ex_remark,ex_type)
values('50','变压器过压保护记录','','0');
insert into e_term_ecode(ex_code,ex_name,ex_remark,ex_type)
values('51','变压器过负荷保护记录','','0');
insert into e_term_ecode(ex_code,ex_name,ex_remark,ex_type)
values('52','变压器欠压保护记录','','0');
insert into e_term_ecode(ex_code,ex_name,ex_remark,ex_type)
values('53','变压器过热保护记录','','0');
insert into e_term_ecode(ex_code,ex_name,ex_remark,ex_type)
values('54','漏电保护器漏电告警记录','','1');
insert into e_term_ecode(ex_code,ex_name,ex_remark,ex_type)
values('55','漏电保护动作','','1');
insert into e_term_ecode(ex_code,ex_name,ex_remark,ex_type)
values('56','剩余电流动作保护器状态告警记录','','1');
