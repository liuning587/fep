create or replace procedure PRC_INSERT_PS_CTRL_PARA(p_logicAddress string,
                                                 p_sn           number,
                                                 p_DateTime     string,
                                                 p_ISREMOTE     number,
                                                 p_DATA_ALARM   number,
                                                 p_ALARM_LIGHTING    number,
                                                 p_ALARM_SOUND       number,
                                                 p_TRIAL_JUMP      number,
                                                 p_GEARS_RETURN      number,
                                                 p_RECLOSE      number,
                                                 p_LOW_VOLT_DATA_ALARM      number,
                                                 p_LOW_VOLT_TRIP_CONTROL      number,
                                                 p_OVER_VOLT_DATA_ALARM      number,
                                                 p_OVER_VOLT_TRIP_CONTROL      number,
                                                 p_PHASE_LOSS_DATA_ALARM      number,
                                                 p_PHASE_LOSS_TRIP_CONTROL      number,
                                                 p_OVER_CURRENT_DATA_ALARM      number,
                                                 p_OVER_CURRENT_TRIP_CONTROL      number,
                                                 p_ZERO_LOSS_DATA_ALARM      number,
                                                 p_ZERO_LOSS_TRIP_CONTROL      number,
                                                 p_TRIAL_JUMP_SOURCE      number,
                                                 p_RATED_RESIDUAL_CURRENT_II      string,
                                                 p_RATED_RESIDUAL_CURRENT_I      string,
                                                 p_LEAK_ELEC_ALARM_TIME      string,
                                                 p_LIMIT_NOT_DRIVER_TIME      string
                                                 )
                                                 is

  v_gpid       C_GP.GP_ID%type;
  v_date_time D_PS_CONTROL_PARA.DATA_TIME%type;
begin
  select gp_id
    into v_gpid
    from V_PS
   where logical_addr = p_logicAddress
     and gp_sn = p_sn;
     
     v_date_time  := to_date(p_DateTime, 'YYYY-MM-DD HH24:MI:SS');

  /*Â©±£¿ØÖÆ×Ö*/
    merge into D_PS_CONTROL_PARA t1
    using (select v_gpid       gpid,
                  v_date_time   datetime,
                  p_ISREMOTE   ISREMOTE,
                  p_DATA_ALARM DATA_ALARM,
                  p_ALARM_LIGHTING     ALARM_LIGHTING,
                  p_ALARM_SOUND     ALARM_SOUND,
                  p_TRIAL_JUMP      TRIAL_JUMP,
                  p_GEARS_RETURN GEARS_RETURN,
                  p_RECLOSE      RECLOSE,
                  p_LOW_VOLT_DATA_ALARM      LOW_VOLT_DATA_ALARM,
                  p_LOW_VOLT_TRIP_CONTROL    LOW_VOLT_TRIP_CONTROL,
                  p_OVER_VOLT_DATA_ALARM     OVER_VOLT_DATA_ALARM,
                  p_OVER_VOLT_TRIP_CONTROL   OVER_VOLT_TRIP_CONTROL,
                  p_PHASE_LOSS_DATA_ALARM    PHASE_LOSS_DATA_ALARM,
                  p_PHASE_LOSS_TRIP_CONTROL  phase_loss_trip_control,
                  p_OVER_CURRENT_DATA_ALARM  OVER_CURRENT_DATA_ALARM,
                  p_OVER_CURRENT_TRIP_CONTROL OVER_CURRENT_TRIP_CONTROL,
                  p_ZERO_LOSS_DATA_ALARM     ZERO_LOSS_DATA_ALARM,
                  p_ZERO_LOSS_TRIP_CONTROL   ZERO_LOSS_TRIP_CONTROL,
                  p_TRIAL_JUMP_SOURCE        TRIAL_JUMP_SOURCE,
                  p_RATED_RESIDUAL_CURRENT_II      RATED_RESIDUAL_CURRENT_II,
                  p_RATED_RESIDUAL_CURRENT_I      RATED_RESIDUAL_CURRENT_I,
                  p_LEAK_ELEC_ALARM_TIME      LEAK_ELEC_ALARM_TIME,
                  p_LIMIT_NOT_DRIVER_TIME     LIMIT_NOT_DRIVER_TIME
             from dual) t2
    on (t2.gpid = t1.gp_id)
    when matched then
      update
         set t1.ISREMOTE = t2.ISREMOTE,
             t1.data_time = t2.datetime,
             t1.DATA_ALARM      = t2.DATA_ALARM,
             t1.ALARM_LIGHTING       = t2.ALARM_LIGHTING,
             t1.ALARM_SOUND       = t2.ALARM_SOUND,
             t1.TRIAL_JUMP  = t2.TRIAL_JUMP,
             t1.GEARS_RETURN  = t2.GEARS_RETURN,
             t1.RECLOSE  = t2.RECLOSE,
             t1.LOW_VOLT_DATA_ALARM  = t2.LOW_VOLT_DATA_ALARM,
             t1.LOW_VOLT_TRIP_CONTROL  = t2.LOW_VOLT_TRIP_CONTROL,
             t1.OVER_VOLT_DATA_ALARM  = t2.OVER_VOLT_DATA_ALARM,
             t1.over_volt_trip_control = t2.over_volt_trip_control,
             t1.PHASE_LOSS_DATA_ALARM  = t2.PHASE_LOSS_DATA_ALARM,             
             t1.phase_loss_trip_control  = t2.phase_loss_trip_control,
             t1.over_current_data_alarm = t2.over_current_data_alarm,
             t1.over_current_trip_control = t2.over_current_trip_control,
             t1.zero_loss_data_alarm = t2.zero_loss_data_alarm,
             t1.ZERO_LOSS_TRIP_CONTROL  = t2.ZERO_LOSS_TRIP_CONTROL,
             t1.TRIAL_JUMP_SOURCE  = t2.TRIAL_JUMP_SOURCE,
             t1.RATED_RESIDUAL_CURRENT_II  = t2.RATED_RESIDUAL_CURRENT_II,
             t1.RATED_RESIDUAL_CURRENT_I  = t2.RATED_RESIDUAL_CURRENT_I,
             t1.LEAK_ELEC_ALARM_TIME  = t2.LEAK_ELEC_ALARM_TIME,
             t1.LIMIT_NOT_DRIVER_TIME  = t2.LIMIT_NOT_DRIVER_TIME
    when not matched then
      insert
        (gp_id,
         DATA_TIME,
         ISREMOTE,
         DATA_ALARM,
         ALARM_LIGHTING,
         ALARM_SOUND,
         TRIAL_JUMP,
         GEARS_RETURN,
         reclose,
         low_volt_data_alarm,
         low_volt_trip_control,
         over_volt_data_alarm,
         over_volt_trip_control,
         phase_loss_data_alarm,
         phase_loss_trip_control,
         over_current_data_alarm,
         over_current_trip_control,
         zero_loss_data_alarm,
         zero_loss_trip_control,
         trial_jump_source,
         rated_residual_current_ii,
         rated_residual_current_i,
         leak_elec_alarm_time,
         limit_not_driver_time
         )
      values
        (t2.gpid,
         t2.datetime,
         t2.isremote,
         t2.data_alarm,
         t2.alarm_lighting,
         t2.alarm_sound,
         t2.trial_jump,
         t2.gears_return,
         t2.reclose,
         t2.low_volt_data_alarm,
         t2.low_volt_trip_control,
         t2.over_volt_data_alarm,
         t2.over_volt_trip_control,
         t2.phase_loss_data_alarm,
         t2.phase_loss_trip_control,
         t2.over_current_data_alarm,
         t2.over_current_trip_control,
         t2.zero_loss_data_alarm,
         t2.zero_loss_trip_control,
         t2.trial_jump_source,
         t2.rated_residual_current_ii,
         t2.rated_residual_current_i,
         t2.leak_elec_alarm_time,
         t2.limit_not_driver_time);
  commit;
  
  exception
  when others then
    ty_errorprocess('PRC_INSERT_PS_CTRL_PARA', 'Error');  
end PRC_INSERT_PS_CTRL_PARA;
/
