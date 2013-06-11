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
 * @author Thinkpad
 */
public class AccessRecordStoredProcedure extends StoredProcedure {
    private static final String SPROC_NAME = "PRC_INSERT_ACCESSRECORD";
    private static final String LOGICADDRESS_PARA = "p_logicAddress";
    private static final String CARD_CODE_PARA = "p_card_code";
    private static final String RECORD_TIME_PARA = "p_record_time";
    private static final String ACCESS_TYPE_PARA = "p_access_type";
    private static final String DOOR_MARK_PARA = "p_doorMark";
    
    public AccessRecordStoredProcedure(){

    }
    
    public AccessRecordStoredProcedure(DataSource dataSource) {
            super(dataSource, SPROC_NAME);
            declareParameter(new SqlParameter(LOGICADDRESS_PARA,Types.VARCHAR));
            declareParameter(new SqlParameter(CARD_CODE_PARA,Types.VARCHAR));
            declareParameter(new SqlParameter(RECORD_TIME_PARA,Types.VARCHAR));
            declareParameter(new SqlParameter(ACCESS_TYPE_PARA,Types.VARCHAR));
            declareParameter(new SqlParameter(DOOR_MARK_PARA,Types.VARCHAR));
            compile();
    }


    public Map execute(String logicalAddress,String card_code,String record_time,String access_type,String doorMark) {
            Map<String,Object> inputs = new HashMap<String,Object>();
            inputs.put(LOGICADDRESS_PARA, logicalAddress);
            inputs.put(CARD_CODE_PARA, card_code);
            inputs.put(RECORD_TIME_PARA, record_time);
            inputs.put(ACCESS_TYPE_PARA, access_type);
            inputs.put(DOOR_MARK_PARA, doorMark);
            return super.execute(inputs);
        }

}
