--���ӹ��ϲ����ֶ�
alter table e_loubao_event 
add EventPara NUMBER(10) default 0 ;

-- Add comments to the columns 
comment on column e_loubao_event.EventPara
  is '���ϲ���: ©�硢���� ---  ʣ�����ֵ��mA��||���ء���· -- ��������||��ѹ��Ƿѹ -- ������ѹ';
