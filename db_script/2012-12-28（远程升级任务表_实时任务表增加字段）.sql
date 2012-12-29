ALTER TABLE R_REALTIME_TASK
ADD UPGRADE_TASK_ID NUMBER;

-- Add comments to the columns 
comment on column R_REALTIME_TASK.UPGRADE_TASK_ID
  is '升级任务ID，对应R_UPGRADE_TASK表的TASK_ID字段';
  
ALTER TABLE R_UPGRADE_TASK
ADD VALID VARCHAR2(5) DEFAULT '1';

-- Add comments to the columns 
comment on column R_UPGRADE_TASK.VALID
  is '是否有效，1：有效；0：失效';
