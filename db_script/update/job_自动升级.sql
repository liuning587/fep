declare  
      job1   integer;  
  begin  
      sys.dbms_job.submit(job1,  
                          'PRC_CHK_UPGRADE;',  
                          sysdate, --   �ƻ��ĳ���ִ�����ڣ�����ݾ�������趨  
                          'sysdate+1/12'
                         );  
      commit;  
  end;  
