CREATE OR REPLACE PROCEDURE PRC_INSERT_ACCESSRECORD(p_logicAddress STRING,
                                             p_card_code      STRING,
                                             p_record_time      STRING,
                                             p_access_type    STRING,
                                             p_doorMark STRING) IS

  v_term_id     C_TERMINAL.TERM_ID%TYPE;
  v_record_time C_ACCESS_RECORD.RECORDTIME%TYPE;
  v_rowNumber number;
BEGIN
    SELECT term_id
      INTO v_term_id
      FROM C_TERMINAL 
     WHERE logical_addr = p_logicAddress;

  v_record_time := to_date(p_record_time, 'yyyy-mm-dd hh24:mi:ss');

  INSERT INTO C_ACCESS_RECORD(term_id,CARD_CODE,RECORDTIME,ACCESSTYPE,DOORNOMARK)
  VALUES
    (v_term_id,
     p_card_code,
     v_record_time,
     p_access_type,
     p_doorMark);
 
  COMMIT;
  exception
  when others then
    ty_errorprocess('PRC_INSERT_ACCESSRECORD', 'Error');
END PRC_INSERT_ACCESSRECORD;
/
