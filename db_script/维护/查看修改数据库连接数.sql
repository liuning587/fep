--��ѯ���ݿ⵱ǰ���̵���������
select count(*) from v$process;

--�鿴���ݿ⵱ǰ�Ự����������
select count(*) from v$session;

--�鿴���ݿ�Ĳ�����������
select count(*) from v$session where status='ACTIVE';

--�鿴��ǰ���ݿ⽨���ĻỰ�����
select sid,serial#,username,program,machine,status from v$session;
��
--��ѯ���ݿ��������������������
select value from v$parameter where name = 'processes';

--�޸����ݿ��������������������
alter system set processes = 300 scope = spfile;

--��ѯ���ݿ���������Ự��������
select value from v$parameter where name = 'sessions';

--�޸����ݿ���������Ự��������
alter system set sessions = 300 scope = spfile;
