/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.bp.dal;

import fep.bp.model.StatusDAO;
import java.util.HashMap;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author THINKPAD
 */
public class StatusServiceIMP implements StatusService{
    private HashMap statusMap=new HashMap();
    private final static Logger log = LoggerFactory.getLogger(StatusServiceIMP.class);
    private JdbcTemplate jdbcTemplate;
    
    private boolean needUpdateStatus(StatusDAO status)
    {
        boolean result = false;
        if(!statusMap.containsKey(status.getLogicalAddress()))
        {
            statusMap.put(status.getLogicalAddress(), status);
            result = true;
        }
        else
        {
            boolean IsConnect = ((StatusDAO)statusMap.get(status.getLogicalAddress())).isIsConnect();
            if(status.isIsConnect() != IsConnect)
            {
                statusMap.put(status.getLogicalAddress(), status);
                result = true;
            }
        }
        return result;
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public void insertStatus(StatusDAO status) {
        try {
            if(needUpdateStatus(status))
            {
                jdbcTemplate.update("update R_ONLINE_INFO set isCurrent = '0' where logical_addr=? and isCurrent=?",new Object[]{status.getLogicalAddress(),"1"});                     
                jdbcTemplate.update("insert into  R_ONLINE_INFO(logical_addr,event_time,event_type,isCurrent) "
                    + "values(?,?,?,?)", new Object[]{status.getLogicalAddress(), status.getEventTime(),status.isIsConnect(),status.isIscurrent()});        
            }
        } catch (DataAccessException dataAccessException) {
            log.error(dataAccessException.getMessage());
        }
    }   
}
