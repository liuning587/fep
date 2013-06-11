--查询数据库当前进程的连接数：
select count(*) from v$process;

--查看数据库当前会话的连接数：
select count(*) from v$session;

--查看数据库的并发连接数：
select count(*) from v$session where status='ACTIVE';

--查看当前数据库建立的会话情况：
select sid,serial#,username,program,machine,status from v$session;
　
--查询数据库允许的最大进程连接数：
select value from v$parameter where name = 'processes';

--修改数据库允许的最大进程连接数：
alter system set processes = 300 scope = spfile;

--查询数据库允许的最大会话连接数：
select value from v$parameter where name = 'sessions';

--修改数据库允许的最大会话连接数：
alter system set sessions = 300 scope = spfile;
