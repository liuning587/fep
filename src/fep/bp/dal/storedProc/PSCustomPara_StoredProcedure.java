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
public class PSCustomPara_StoredProcedure extends StoredProcedure{
    private static final String SPROC_NAME = "PRC_INSERT_PS_CUSTOM_PARA";
    private static final String LOGICADDRESS_PARA = "p_logicAddress";
    private static final String SN_PARA = "p_sn";
    private static final String DATETIME_PARA = "p_DateTime";
    private static final String OVER_VOLT_LMT = "p_OVER_VOLT_LMT";
    private static final String LOW_VOLT_LMT = "p_LOW_VOLT_LMT";
    private static final String LOSE_PHASE_LMT = "p_LOSE_PHASE_LMT";
    private static final String RATED_CURRENT_LMT = "p_RATED_CURRENT_LMT";
    private static final String RATED_CURRENT_GEARS1 = "p_RATED_CURRENT_GEARS1";
    private static final String RATED_CURRENT_GEARS2 = "p_RATED_CURRENT_GEARS2";
    private static final String RATED_CURRENT_GEARS3 = "p_RATED_CURRENT_GEARS3";
    private static final String RATED_CURRENT_GEARS4 = "p_RATED_CURRENT_GEARS4";
    private static final String RATED_CURRENT_GEARS5 = "p_RATED_CURRENT_GEARS5";
    private static final String limit_not_driver_time_gears1 = "p_limit_not_driver_time_gears1";
    private static final String limit_not_driver_time_gears2 = "p_limit_not_driver_time_gears2";
    private static final String limit_not_driver_time_gears3 = "p_limit_not_driver_time_gears3";    

    public PSCustomPara_StoredProcedure(){
        
    }
    
    public PSCustomPara_StoredProcedure(DataSource dataSource) {
            super(dataSource, SPROC_NAME);
            declareParameter(new SqlParameter(LOGICADDRESS_PARA,Types.VARCHAR));
            declareParameter(new SqlParameter(SN_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(DATETIME_PARA,Types.VARCHAR));
            declareParameter(new SqlParameter(OVER_VOLT_LMT,Types.NUMERIC));
            declareParameter(new SqlParameter(LOW_VOLT_LMT,Types.NUMERIC));
            declareParameter(new SqlParameter(LOSE_PHASE_LMT,Types.NUMERIC));
            declareParameter(new SqlParameter(RATED_CURRENT_LMT,Types.NUMERIC));
            declareParameter(new SqlParameter(RATED_CURRENT_GEARS1,Types.NUMERIC));
            declareParameter(new SqlParameter(RATED_CURRENT_GEARS2,Types.NUMERIC));
            declareParameter(new SqlParameter(RATED_CURRENT_GEARS3,Types.NUMERIC));
            declareParameter(new SqlParameter(RATED_CURRENT_GEARS4,Types.NUMERIC));
            declareParameter(new SqlParameter(RATED_CURRENT_GEARS5,Types.NUMERIC));
            declareParameter(new SqlParameter(limit_not_driver_time_gears1,Types.NUMERIC));
            declareParameter(new SqlParameter(limit_not_driver_time_gears2,Types.NUMERIC));
            declareParameter(new SqlParameter(limit_not_driver_time_gears3,Types.NUMERIC));
            compile();
    }
}
