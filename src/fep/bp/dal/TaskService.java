/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.bp.dal;


import fep.bp.model.TermTaskDAO;
import java.sql.Date;
import java.util.List;

/**
 *
 * @author Thinkpad
 */
public interface TaskService {
    public List<TermTaskDAO> getPollingTask(int CircleUnit,int interval);
    public void updatePollingTask(int TaskId,String ProtocolNo,String Sys_Object,Date StartTime,Date EndTime,int PollingNum);

}
