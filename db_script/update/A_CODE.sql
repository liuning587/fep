delete from A_CODE where CODE_CATE = 'PROTOCOL_METER';

insert into A_CODE (CODE_ID, CODE_CATE, CODE, NAME, REMARK, CODE_TYPE, VALUE)
values (274, 'PROTOCOL_METER', '1', 'DL/T 645��1997', '����Լ', '0', null);

insert into A_CODE (CODE_ID, CODE_CATE, CODE, NAME, REMARK, CODE_TYPE, VALUE)
values (276, 'PROTOCOL_METER', '100', 'Ǭ��V22', '����Լ', '0', null);

insert into A_CODE (CODE_ID, CODE_CATE, CODE, NAME, REMARK, CODE_TYPE, VALUE)
values (1456, 'PROTOCOL_METER', '101', 'Ǭ��V31', '����Լ', '0', null);

insert into A_CODE (CODE_ID, CODE_CATE, CODE, NAME, REMARK, CODE_TYPE, VALUE)
values (277, 'PROTOCOL_METER', '2', '��������װ��ͨ��Э��', '����Լ', '0', null);

insert into A_CODE (CODE_ID, CODE_CATE, CODE, NAME, REMARK, CODE_TYPE, VALUE)
values (275, 'PROTOCOL_METER', '30', 'DL/T 645��2007', '����Լ', '0', null);

commit;

delete from A_CODE where CODE_CATE = 'PROTOCOL_TERM';

insert into A_CODE (CODE_ID, CODE_CATE, CODE, NAME, REMARK, CODE_TYPE, VALUE)
values (314, 'PROTOCOL_TERM', '100', '376��Լ', '�ն˹�Լ', '0', null);

insert into A_CODE (CODE_ID, CODE_CATE, CODE, NAME, REMARK, CODE_TYPE, VALUE)
values (1455, 'PROTOCOL_TERM', '101', '376��Լ��2013��չ��', '�ն˹�Լ', '0', null);

commit;