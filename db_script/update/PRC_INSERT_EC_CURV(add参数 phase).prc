create or replace procedure PRC_INSERT_EC_CURV(p_logicAddress string,
                                               p_sn           number,
                                               p_DateTime     string,
                                               p_ecur_a       string,
                                               p_ecur_b       string,
                                               p_ecur_c       string,
                                               p_ecur_l       string,
                                               p_ecur_s       string,
                                               p_volt_a       string,
                                               p_volt_b       string,
                                               p_volt_c       string,
                                               p_phase        string) is

  v_assetNo    C_METER.ASSET_NO%type;
  v_gpid       C_GP.GP_ID%type;
  v_orgno      O_ORG.ORG_NO%type;
  v_ct         C_GP.Ct_Times%type;
  v_pt         C_GP.Pt_Times%type;
  v_DDate      D_EI_FREEZE_DAY.Ddate%type;
  v_date_time  D_EI_FREEZE_DAY.DATA_TIME%type;
  v_acceptTime Date;
  v_Err_Msg    varchar2(500);

begin
  select asset_no, gp_id, org_no, ct_times, pt_times
    into v_assetNo, v_gpid, v_orgno, v_ct, v_pt
    from v_GP
   where logical_addr = p_logicAddress
     and gp_sn = p_sn;

  v_date_time  := to_date(p_DateTime, 'YYYY-MM-DD HH24:MI:SS');
  v_DDate      := to_char(v_date_time, 'YYYYMMDD');
  v_acceptTime := sysdate;

  merge into D_EC_CURV_C t1
  using (select v_gpid       gpid,
                v_assetNo    assetNo,
                v_orgno      orgno,
                v_date_time  DateTime,
                v_DDate      ddate,
                v_acceptTime acceptTime,
                v_ct         ct,
                v_pt         pt,
                p_ecur_a     ecur_a,
                p_ecur_b     ecur_b,
                p_ecur_c     ecur_c,
                p_ecur_l     ecur_l,
                p_ecur_s     ecur_s,
                p_volt_a     volt_a,
                p_volt_b     volt_b,
                p_volt_c     volt_c,
                1            DATA_FLAG,
                1            DATA_SOURCE,
                p_phase      phase
                
           from dual) t2
  on ((t2.gpid = t1.gp_id) and (to_char(t2.DateTime, 'YYYYMMDDhh24mi') = to_char(t1.data_time, 'YYYYMMDDhh24mi')))
  when matched then
    update
       set t1.accept_time = t2.accepttime,
           t1.ecur_a      = t2.ecur_a,
           t1.ecur_b      = t2.ecur_b,
           t1.ecur_c      = t2.ecur_c,
           t1.ecur_l      = t2.ecur_l,
           t1.ecur_s      = t2.ecur_s,
           t1.volt_a      = t2.volt_a,
           t1.volt_b      = t2.volt_b,
           t1.volt_c      = t2.volt_c,
           t1.ddate       = t2.datetime,
           t1.phase       = t2.phase
  when not matched then
    insert
      (gp_id,
       asset_no,
       org_no,
       ddate,
       data_time,
       accept_time,
       ct_times,
       pt_times,
       ecur_a,
       ecur_b,
       ecur_c,
       ecur_l,
       ecur_s,
       volt_a,
       volt_b,
       volt_c,
       phase)
    values
      (t2.gpid,
       t2.assetno,
       t2.orgno,
       t2.ddate,
       t2.datetime,
       t2.accepttime,
       t2.ct,
       t2.pt,
       t2.ecur_a,
       t2.ecur_b,
       t2.ecur_c,
       t2.ecur_l,
       t2.ecur_s,
       t2.volt_a,
       t2.volt_b,
       t2.volt_c,
       t2.phase);
  commit;
exception
  when others then
    ty_errorprocess('PRC_INSERT_EC_CURV', 'Error');
end PRC_INSERT_EC_CURV;
/
