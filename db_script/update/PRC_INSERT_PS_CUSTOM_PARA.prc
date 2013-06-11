create or replace procedure PRC_INSERT_PS_CUSTOM_PARA(p_logicAddress string,
                                                 p_sn           number,
                                                 p_DateTime     string,
                                                 p_OVER_VOLT_LMT     number,
                                                 p_LOW_VOLT_LMT   number,
                                                 p_LOSE_PHASE_LMT    number,
                                                 p_RATED_CURRENT_LMT       number,
                                                 p_RATED_CURRENT_GEARS1      number,
                                                 p_RATED_CURRENT_GEARS2      number,
                                                 p_RATED_CURRENT_GEARS3      number,
                                                 p_RATED_CURRENT_GEARS4      number,
                                                 p_RATED_CURRENT_GEARS5      number,
                                                 p_limit_not_driver_time_gears1      number,
                                                 p_limit_not_driver_time_gears2      number,
                                                 p_limit_not_driver_time_gears3      number
                                                 )
                                                 is

  v_gpid       C_GP.GP_ID%type;
  v_date_time  D_PS_CUSTOM_PARA.DATA_TIME%TYPE;
begin
  select gp_id
    into v_gpid
    from V_PS
   where logical_addr = p_logicAddress
     and gp_sn = p_sn;
     
   v_date_time  := to_date(p_DateTime, 'YYYY-MM-DD HH24:MI:SS');

  /*用户定制参数*/
    merge into D_PS_CUSTOM_PARA t1
    using (select v_gpid       gpid,
                  v_date_time   DateTime,
                  p_OVER_VOLT_LMT   OVER_VOLT_LMT,
                  p_LOW_VOLT_LMT    LOW_VOLT_LMT,
                  p_LOSE_PHASE_LMT  LOSE_PHASE_LMT,
                  p_RATED_CURRENT_LMT     RATED_CURRENT_LMT,
                  p_RATED_CURRENT_GEARS1      RATED_CURRENT_GEARS1,
                  p_RATED_CURRENT_GEARS2      RATED_CURRENT_GEARS2,
                  p_RATED_CURRENT_GEARS3      RATED_CURRENT_GEARS3,
                  p_RATED_CURRENT_GEARS4      RATED_CURRENT_GEARS4,
                  p_RATED_CURRENT_GEARS5      RATED_CURRENT_GEARS5,
                  p_limit_not_driver_time_gears1     limit_not_driver_time_gears1,
                  p_limit_not_driver_time_gears2     limit_not_driver_time_gears2,
                  p_limit_not_driver_time_gears3     limit_not_driver_time_gears3

             from dual) t2
    on (t2.gpid = t1.gp_id)
    when matched then
      update
         set t1.data_time = t2.datetime,
             t1.over_volt_limit_value = t2.OVER_VOLT_LMT,
             t1.low_volt_limit_value      = t2.LOW_VOLT_LMT,
             t1.lose_phase_limit_value       = t2.LOSE_PHASE_LMT,
             t1.rated_current_limit_value       = t2.RATED_CURRENT_LMT,
             t1.RATED_CURRENT_GEARS1  = t2.RATED_CURRENT_GEARS1,
             t1.RATED_CURRENT_GEARS2  = t2.RATED_CURRENT_GEARS2,
             t1.RATED_CURRENT_GEARS3  = t2.RATED_CURRENT_GEARS3,
             t1.RATED_CURRENT_GEARS4  = t2.RATED_CURRENT_GEARS4,
             t1.RATED_CURRENT_GEARS5  = t2.RATED_CURRENT_GEARS5,
             t1.limit_not_driver_time_gears1  = t2.limit_not_driver_time_gears1,
             t1.limit_not_driver_time_gears2 = t2.limit_not_driver_time_gears2,
             t1.limit_not_driver_time_gears3  = t2.limit_not_driver_time_gears3
    when not matched then
      insert
        (gp_id,
         DATA_TIME,
         over_volt_limit_value,
         low_volt_limit_value,
         lose_phase_limit_value,
         rated_current_limit_value,
         rated_current_gears1,
         rated_current_gears2,
         rated_current_gears3,
         rated_current_gears4,
         rated_current_gears5,
         limit_not_driver_time_gears1,
         limit_not_driver_time_gears2,
         limit_not_driver_time_gears3
         )
      values
        (t2.gpid,
         t2.datetime,
         t2.over_volt_lmt,
         t2.low_volt_lmt,
         t2.lose_phase_lmt,
         t2.rated_current_lmt,
         t2.rated_current_gears1,
         t2.rated_current_gears2,
         t2.rated_current_gears3,
         t2.rated_current_gears4,
         t2.rated_current_gears5,
         t2.limit_not_driver_time_gears1,
         t2.limit_not_driver_time_gears2,
         t2.limit_not_driver_time_gears3);
  commit;
  exception
  when others then
    ty_errorprocess('PRC_INSERT_PS_CUSTOM_PARA', 'Error');
end PRC_INSERT_PS_CUSTOM_PARA;
/
