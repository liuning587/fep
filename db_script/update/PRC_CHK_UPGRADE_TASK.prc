CREATE OR REPLACE PROCEDURE PRC_CHK_UPGRADE_TASK IS
BEGIN
  INSERT INTO r_trip_plan
    SELECT a.ps_id,
           to_char(d.post_time, 'yyyymmdd') ddate,
           d.post_time,
           NULL,
           1,
           0,
           d.task_id
      FROM c_ps a,
           c_gp b,
           c_terminal c,
           (SELECT aa.logical_addr,
                   substr(aa.gp_mark, 1, 1) gp_sn,
                   aa.post_time,
                   aa.task_id
              FROM R_REALTIME_TASK aa
             WHERE aa.task_type = 2
               AND NOT EXISTS (SELECT 1
                      FROM r_realtime_task_recv bb
                     WHERE aa.task_id = bb.task_id)) d
     WHERE a.term_id = c.term_id
       AND a.gp_id = b.gp_id
       AND b.gp_sn = d.gp_sn
       AND c.logical_addr = d.logical_addr
       AND (SYSDATE - d.post_time) > 1 / 96
       AND NOT EXISTS
     (SELECT 1 FROM r_trip_plan e WHERE e.task_id = d.task_id);
  COMMIT;
  exception
  when others then
    ty_errorprocess('PRC_CHK_UPGRADE_TASK', 'Error');
END PRC_CHK_UPGRADE_TASK;
/
