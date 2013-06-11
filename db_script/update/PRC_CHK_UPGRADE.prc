create or replace procedure PRC_CHK_UPGRADE is
begin
  UPDATE r_upgrade_task SET task_status = 0
  where task_id in
  (
        select task_id
        from 
        (
             SELECT task_id,   
             ROW_NUMBER() OVER(partition by  logical_addr ORDER BY POST_TIME DESC )rownumber  
             FROM R_UPGRADE_TASK 
             WHERE valid =1
             and task_status = 3
         ) 
         WHERE rownumber = 1 
  );
  commit;
exception
  when others then
    ty_errorprocess('PRC_CHK_UPGRADE', 'Error');
end PRC_CHK_UPGRADE;
/
