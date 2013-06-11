declare  
      job1   integer;  
  begin  
      sys.dbms_job.submit(job1,  
                          'PRC_CHK_UPGRADE;',  
                          sysdate, --   计划的初次执行日期，请根据具体情况设定  
                          'sysdate+1/12'
                         );  
      commit;  
  end;  
