alter table R_TERM_TASK 
add LAST_EXEC_TIME DATE;

-- Add comments to the columns 
comment on column R_TERM_TASK.LAST_EXEC_TIME
  is '上一次执行时间';
