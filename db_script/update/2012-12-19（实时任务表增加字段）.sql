ALTER TABLE R_REALTIME_TASK
ADD TERMINAL_PROTOCOL VARCHAR2(2) DEFAULT '0';

ALTER TABLE R_REALTIME_TASK
ADD METER_PROTOCOL VARCHAR2(2) DEFAULT '0';

-- Add comments to the columns 
comment on column R_REALTIME_TASK.TERMINAL_PROTOCOL
  is '�ն˹�Լ��0��376��1����棻';

comment on column R_REALTIME_TASK.METER_PROTOCOL
  is '��ƣ�����������Լ��0��645��97����1��645��07����2����棻5��QLLB��6��HDLB';
