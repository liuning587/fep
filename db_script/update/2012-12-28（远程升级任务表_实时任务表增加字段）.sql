ALTER TABLE R_REALTIME_TASK
ADD UPGRADE_TASK_ID NUMBER;

-- Add comments to the columns 
comment on column R_REALTIME_TASK.UPGRADE_TASK_ID
  is '��������ID����ӦR_UPGRADE_TASK���TASK_ID�ֶ�';
  
ALTER TABLE R_UPGRADE_TASK
ADD VALID VARCHAR2(5) DEFAULT '1';

-- Add comments to the columns 
comment on column R_UPGRADE_TASK.VALID
  is '�Ƿ���Ч��1����Ч��0��ʧЧ';
