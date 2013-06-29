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
public class ObjectStatus_StoredProcedure extends StoredProcedure {
    private static final String SPROC_NAME = "PRC_INSERT_OBJECT_STATUS";
    private static final String LOGICADDRESS_PARA = "p_logicAddress";
    private static final String SN_PARA = "p_sn";
    private static final String OBJECT_TYPE_PARA = "p_object_type";
    private static final String STATUS_VALUE_PARA = "p_status_value";

    public ObjectStatus_StoredProcedure(){
        
    }

    public ObjectStatus_StoredProcedure(DataSource dataSource) {
            super(dataSource, SPROC_NAME);
            declareParameter(new SqlParameter(LOGICADDRESS_PARA,Types.VARCHAR));
            declareParameter(new SqlParameter(SN_PARA,Types.NUMERIC));
            declareParameter(new SqlParameter(OBJECT_TYPE_PARA,Types.VARCHAR));
            declareParameter(new SqlParameter(STATUS_VALUE_PARA,Types.NUMERIC));

            compile();
    }


    public Map execute(String logicalAddress,int gpSn,String objectType,
                        int status_value)
    {
            Map<String,Object> inputs = new HashMap<String,Object>();
            inputs.put(LOGICADDRESS_PARA, logicalAddress);
            inputs.put(SN_PARA, gpSn);
            inputs.put(OBJECT_TYPE_PARA, objectType);
            inputs.put(STATUS_VALUE_PARA, status_value);
            return super.execute(inputs);
     }

}
