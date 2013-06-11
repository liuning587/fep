CREATE OR REPLACE PROCEDURE PRC_INSERT_EVENT(p_logicAddress STRING,
                                             p_sn           NUMBER,
                                             p_ex_code      STRING,
                                             p_ex_time      STRING,
                                             p_ex_detail    STRING) IS

  v_gpchar     C_GP.GP_CHAR%TYPE;
  v_acceptTime E_EVENT_DATA.ACCEPT_TIME%TYPE;
  v_ed_id E_EVENT_DATA.ED_ID%TYPE;
  v_exTime E_EVENT_DATA.EX_TIME%TYPE;
  PowerOnTime E_EVENT_DATA.EX_TIME%TYPE;
  isPowerOn number(1);
  v_rowNumber number;
BEGIN
  IF p_sn <> 0 THEN
    SELECT a.gp_char
      INTO v_gpchar
      FROM C_GP a, C_TERMINAL b
     WHERE a.term_id = b.term_id
       AND b.logical_addr = p_logicAddress
       AND a.gp_sn = p_sn;
  END IF;

  IF p_sn = 0 THEN
    v_gpchar := 6;
  END IF;

  v_acceptTime := SYSDATE;
  v_exTime := to_date(p_ex_time, 'yyyy-mm-dd hh24:mi:ss');
 /* 
  select count(*) into v_rowNumber
  from E_EVENT_DATA
  where logical_addr = p_logicAddress
  and GP_SN = p_sn
  and EX_CODE_FEP = p_ex_code
  and EX_TIME = v_exTime;
  
  if v_rowNumber>0 then --重复事件过滤
    return;
  end if;
  */
  INSERT INTO E_EVENT_DATA(ed_id,LOGICAL_ADDR,GP_SN,GP_CHAR,EX_CODE_FEP,EX_TIME,ACCEPT_TIME,EX_DETAIL)
  VALUES
    (SEQ_E_EVENT.NEXTVAL,
     p_logicAddress,
     p_sn,
     v_gpchar,
     p_ex_code,
     v_exTime,
     v_acceptTime,
     p_ex_detail)
  returning ed_id into v_ed_id;
  --停上电事件处理
  if (p_ex_code = '14') then
    /*停电时间(5)+上电时间(5) 通过时间是否为FEFEFEFEFE来判断是上电事件还是停电事件*/
    if substr(p_ex_detail,1,10)='FEFEFEFEFE' then
       isPowerOn := 1;
       PowerOnTime := To_date(substr(p_ex_detail,11,10),'MIHH24DDMMYY');
       else
       isPowerOn := 0;
       PowerOnTime := To_date(substr(p_ex_detail,1,10),'MIHH24DDMMYY');
    end if;
    triger_event_14(v_ed_id,p_logicAddress,p_ex_code,p_sn,PowerOnTime,isPowerOn);
  elsif(p_ex_code = '4') then
     triger_event_4(v_ed_id,p_logicAddress,p_ex_code,v_exTime,p_ex_detail);
  end if;
  
  COMMIT;
  exception
  when others then
    ty_errorprocess('PRC_INSERT_EVENT', 'Error');
END PRC_INSERT_EVENT;
/
