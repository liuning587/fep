alter table e_term_ecode
add EX_TYPE varchar2(2) default '0';

comment on column e_term_ecode.EX_TYPE
  is '�쳣���� 0���ն��¼� 1��©���¼�';
  
insert into e_term_ecode(ex_code,ex_name,ex_remark,ex_type)
values('36','©�籣������(Ǭ����Լ)','','1');
insert into e_term_ecode(ex_code,ex_name,ex_remark,ex_type)
values('50','��ѹ����ѹ������¼','','0');
insert into e_term_ecode(ex_code,ex_name,ex_remark,ex_type)
values('51','��ѹ�������ɱ�����¼','','0');
insert into e_term_ecode(ex_code,ex_name,ex_remark,ex_type)
values('52','��ѹ��Ƿѹ������¼','','0');
insert into e_term_ecode(ex_code,ex_name,ex_remark,ex_type)
values('53','��ѹ�����ȱ�����¼','','0');
insert into e_term_ecode(ex_code,ex_name,ex_remark,ex_type)
values('54','©�籣����©��澯��¼','','1');
insert into e_term_ecode(ex_code,ex_name,ex_remark,ex_type)
values('55','©�籣������','','1');
insert into e_term_ecode(ex_code,ex_name,ex_remark,ex_type)
values('56','ʣ���������������״̬�澯��¼','','1');
