/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.bp.dal.storedProc;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

/**
 *
 * @author xiekeli
 */
public class CD_DL_StoredProcedure extends StoredProcedure{
    private static final String SPROC_NAME = "PRC_INSERT_CD_DL";
    private static final String LOGICADDRESS_PARA = "p_logicAddress";
    private static final String SN_PARA = "p_sn";
    private static final String WGDL_DAY_PARA = "p_wgdl_day";
    private static final String WGDL_MONTH_PARA = "p_wgdl_month";
    
    public CD_DL_StoredProcedure(){
        
    }

    public CD_DL_StoredProcedure(DataSource dataSource) {
            super(dataSource, SPROC_NAME);
            declareParameter(new SqlParameter(LOGICADDRESS_PARA,Types.VARCHAR));
            declareParameter(new SqlParameter(SN_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(WGDL_DAY_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(WGDL_MONTH_PARA,Types.NUMERIC));

            compile();
    }


    public Map execute(String logicalAddress,int gpSn,float wgdl_day,
                        float wgdl_month)
    {
            Map<String,Object> inputs = new HashMap<String,Object>();
            inputs.put(LOGICADDRESS_PARA, logicalAddress);
            inputs.put(SN_PARA, gpSn);
            inputs.put(WGDL_DAY_PARA, wgdl_day);
            inputs.put(WGDL_MONTH_PARA, wgdl_month);
            return super.execute(inputs);
     }
}
