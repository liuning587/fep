/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.bp.dal.storedProc;

import java.sql.Types;
import javax.sql.DataSource;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

/**
 *
 * @author THINKPAD
 */
public class PSCtrlPara_StoredProcedure extends StoredProcedure{
    private static final String SPROC_NAME = "PRC_INSERT_PS_CTRL_PARA";
    private static final String LOGICADDRESS_PARA = "p_logicAddress";
    private static final String DATE_TIME_PARA = "p_DateTime";
    private static final String SN_PARA = "p_sn";
    private static final String ISREMOTE_PARA = "p_ISREMOTE";
    private static final String DATA_ALARM_PARA = "p_DATA_ALARM";
    private static final String ALARM_LIGHTING_PARA = "p_ALARM_LIGHTING";
    private static final String ALARM_SOUND_PARA = "p_ALARM_SOUND";
    private static final String TRIAL_JUMP_PARA = "p_TRIAL_JUMP";
    private static final String GEARS_RETURN_JUMP_PARA = "p_GEARS_RETURN";
    private static final String RECLOSE_PARA = "p_RECLOSE";
    private static final String LOW_VOLT_DATA_ALARM_PARA = "p_LOW_VOLT_DATA_ALARM";
    private static final String LOW_VOLT_TRIP_CONTROL_PARA = "p_LOW_VOLT_TRIP_CONTROL";
    private static final String OVER_VOLT_DATA_ALARM_PARA = "p_OVER_VOLT_DATA_ALARM";
    private static final String OVER_VOLT_TRIP_CONTROL_PARA = "p_OVER_VOLT_TRIP_CONTROL";
    private static final String PHASE_LOSS_DATA_ALARM_PARA = "p_PHASE_LOSS_DATA_ALARM";    
    private static final String PHASE_LOSS_TRIP_CONTROL_PARA = "p_PHASE_LOSS_TRIP_CONTROL";
    private static final String OVER_CURRENT_DATA_ALARM_PARA = "p_OVER_CURRENT_DATA_ALARM";
    private static final String OVER_CURRENT_TRIP_CONTROL_PARA = "p_OVER_CURRENT_TRIP_CONTROL";
    private static final String ZERO_LOSS_DATA_ALARM_PARA = "p_ZERO_LOSS_DATA_ALARM";
    private static final String ZERO_LOSS_TRIP_CONTROL_PARA = "p_ZERO_LOSS_TRIP_CONTROL";
    private static final String TRIAL_JUMP_SOURCE_PARA = "p_TRIAL_JUMP_SOURCE";
    private static final String RATED_RESIDUAL_CURRENT_II_PARA = "p_RATED_RESIDUAL_CURRENT_II";
    private static final String RATED_RESIDUAL_CURRENT_I_PARA = "p_RATED_RESIDUAL_CURRENT_I";
    private static final String LEAK_ELEC_ALARM_TIME_PARA = "p_LEAK_ELEC_ALARM_TIME";
    private static final String LIMIT_NOT_DRIVER_TIME_PARA = "p_LIMIT_NOT_DRIVER_TIME";

    public PSCtrlPara_StoredProcedure(){
        
    }
    
    public PSCtrlPara_StoredProcedure(DataSource dataSource) {
            super(dataSource, SPROC_NAME);
            declareParameter(new SqlParameter(LOGICADDRESS_PARA,Types.VARCHAR));
            declareParameter(new SqlParameter(SN_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(DATE_TIME_PARA,Types.VARCHAR));
            declareParameter(new SqlParameter(ISREMOTE_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(DATA_ALARM_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(ALARM_LIGHTING_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(ALARM_SOUND_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(TRIAL_JUMP_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(GEARS_RETURN_JUMP_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(RECLOSE_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(LOW_VOLT_DATA_ALARM_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(LOW_VOLT_TRIP_CONTROL_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(OVER_VOLT_DATA_ALARM_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(OVER_VOLT_TRIP_CONTROL_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(PHASE_LOSS_DATA_ALARM_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(PHASE_LOSS_TRIP_CONTROL_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(OVER_CURRENT_DATA_ALARM_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(OVER_CURRENT_TRIP_CONTROL_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(ZERO_LOSS_DATA_ALARM_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(ZERO_LOSS_TRIP_CONTROL_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(TRIAL_JUMP_SOURCE_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(RATED_RESIDUAL_CURRENT_II_PARA,Types.VARCHAR));
            declareParameter(new SqlParameter(RATED_RESIDUAL_CURRENT_I_PARA,Types.VARCHAR));
            declareParameter(new SqlParameter(LEAK_ELEC_ALARM_TIME_PARA,Types.VARCHAR));
            declareParameter(new SqlParameter(LIMIT_NOT_DRIVER_TIME_PARA,Types.VARCHAR));
            compile();
    }
}
